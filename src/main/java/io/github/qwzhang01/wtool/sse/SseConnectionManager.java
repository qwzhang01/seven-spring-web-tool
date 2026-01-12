package io.github.qwzhang01.wtool.sse;

import io.github.qwzhang01.wtool.domain.SseMsg;
import io.github.qwzhang01.wtool.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.qwzhang01.wtool.util.StrUtil.uuidStr;

/**
 * Manages SSE connections with support for multi-instance deployment.
 * <p>
 * This class handles local SSE emitter connections and coordinates with
 * a {@link SseMessageBroker} to enable message delivery across multiple
 * application instances.
 * <p>
 * Usage:
 * <pre>
 * // Create with local broker (single instance)
 * SseConnectionManager manager = new SseConnectionManager();
 *
 * // Create with Redis broker (multi-instance)
 * SseConnectionManager manager = new SseConnectionManager(redisBroker, "instance-1");
 * </pre>
 *
 * @author avinzhang
 */
public class SseConnectionManager {

    private static final Logger log =
            LoggerFactory.getLogger(SseConnectionManager.class);

    /**
     * Default timeout for SSE connections: 30 minutes
     */
    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L;

    /**
     * Maps client IDs to their corresponding connection IDs (local)
     */
    private final ConcurrentHashMap<String, String> clientToConnection =
            new ConcurrentHashMap<>();

    /**
     * Stores all active SSE emitter connections (local)
     */
    private final ConcurrentHashMap<String, SseEmitter> emitters =
            new ConcurrentHashMap<>();

    /**
     * The message broker for cross-instance communication
     */
    private final SseMessageBroker broker;

    /**
     * Unique identifier for this instance
     */
    private final String instanceId;

    /**
     * Creates a connection manager with a local broker (single instance mode).
     */
    public SseConnectionManager() {
        this(new LocalSseMessageBroker(), uuidStr());
    }

    /**
     * Creates a connection manager with the specified broker.
     *
     * @param broker     the message broker for cross-instance communication
     * @param instanceId unique identifier for this instance
     */
    public SseConnectionManager(SseMessageBroker broker, String instanceId) {
        this.broker = broker;
        this.instanceId = instanceId;
        initBrokerSubscription();
    }

    /**
     * Initializes the broker subscription for receiving messages.
     */
    private void initBrokerSubscription() {
        // Subscribe to messages for this instance
        broker.subscribe(instanceId, this::handleIncomingMessage);

        // Subscribe to broadcast messages
        broker.subscribeBroadcast(this::handleBroadcastMessage);

        log.info("SSE Connection Manager initialized with instance ID: {}",
                instanceId);
    }

    /**
     * Handles incoming messages from the broker.
     */
    private void handleIncomingMessage(String clientId, String message) {
        sendToLocalClient(clientId, message);
    }

    /**
     * Handles broadcast messages from the broker.
     */
    private void handleBroadcastMessage(String message) {
        clientToConnection.keySet().forEach(clientId ->
                sendToLocalClient(clientId, message));
    }

    /**
     * Gets the mapping of client IDs to connection IDs.
     *
     * @return a map of client IDs to their connection IDs
     */
    public Map<String, String> getClientIds() {
        return clientToConnection;
    }

    /**
     * Gets all connected client IDs on this instance.
     *
     * @return set of client IDs
     */
    public Set<String> getLocalClientIds() {
        return clientToConnection.keySet();
    }

    /**
     * Creates a new SSE emitter connection with timeout configuration.
     * If a connection already exists for the client, it will be closed first.
     *
     * @param clientId the unique identifier for the client
     * @param message  the initial message to send upon connection
     * @return a new SseEmitter instance configured with timeout and event
     *         handlers
     */
    public SseEmitter createEmitter(String clientId, String message) {
        String oldConnectionId = clientToConnection.remove(clientId);

        if (!StrUtil.isBlank(oldConnectionId)) {
            removeEmitter(oldConnectionId);
        }

        String connectionId = uuidStr();
        clientToConnection.put(clientId, connectionId);

        // Register with broker for cross-instance discovery
        broker.registerClient(clientId, instanceId);

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(connectionId, emitter);

        // Remove emitter when connection completes normally
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for client: {}", clientId);
            cleanup(clientId, connectionId);
        });

        // Remove emitter when connection times out
        emitter.onTimeout(() -> {
            log.info("SSE connection timeout for client: {}", clientId);
            cleanup(clientId, connectionId);
        });

        // Remove emitter when an error occurs
        emitter.onError(throwable -> {
            log.error("SSE error for client: {}", clientId, throwable);
            cleanup(clientId, connectionId);
        });

        // Send initial connection success message
        sendToLocalClient(clientId, message);
        return emitter;
    }

    /**
     * Cleans up client and connection mappings.
     */
    private void cleanup(String clientId, String connectionId) {
        clientToConnection.remove(clientId, connectionId);
        removeEmitter(connectionId);
        broker.unregisterClient(clientId);
    }

    /**
     * Removes an emitter from the active connections and completes it.
     *
     * @param connectionId the connection ID of the emitter to remove
     */
    private void removeEmitter(String connectionId) {
        SseEmitter emitter = emitters.remove(connectionId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("Error completing emitter: {}", e.getMessage());
            }
        }
    }

    /**
     * Sends a message to a specific client.
     * If the client is on another instance, the message will be routed
     * through the broker.
     *
     * @param clientId the client ID to send the message to
     * @param message  the message content
     * @return true if the message was sent successfully, false otherwise
     */
    public boolean sendToClient(String clientId, String message) {
        // Check if client is local
        if (clientToConnection.containsKey(clientId)) {
            return sendToLocalClient(clientId, message);
        }

        // Client might be on another instance, use broker
        String targetInstance = broker.getClientInstance(clientId);
        if (targetInstance != null) {
            broker.publishToClient(clientId, message);
            return true;
        }

        return false;
    }

    /**
     * Sends a message to a local client.
     *
     * @param clientId the client ID
     * @param message  the message content
     * @return true if sent successfully
     */
    private boolean sendToLocalClient(String clientId, String message) {
        String connectionId = clientToConnection.get(clientId);
        if (StrUtil.isBlank(connectionId)) {
            return false;
        }

        SseEmitter emitter = emitters.get(connectionId);
        if (emitter != null) {
            try {
                SseMsg messageBean = new SseMsg(message, "system",
                        SseMsg.MessageType.TEXT);

                emitter.send(SseEmitter.event()
                        .id(messageBean.getId())
                        .name("message")
                        .data(messageBean));

                return true;
            } catch (IOException e) {
                log.error("Failed to send message to client: {}", clientId, e);
                String connId = clientToConnection.remove(clientId);
                if (connId != null) {
                    removeEmitter(connId);
                    broker.unregisterClient(clientId);
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Broadcasts a message to all connected clients across all instances.
     *
     * @param message the message to broadcast
     */
    public void broadcast(String message) {
        // Use broker for cross-instance broadcast
        broker.publishBroadcast(message);
    }

    /**
     * Broadcasts a message only to clients on this instance.
     *
     * @param message the message to broadcast locally
     */
    public void broadcastLocal(String message) {
        clientToConnection.keySet().forEach(clientId -> {
            boolean sent = sendToLocalClient(clientId, message);
            if (!sent) {
                close(clientId);
            }
        });
    }

    /**
     * Closes the SSE connection for the specified client.
     *
     * @param clientId the client ID whose connection should be closed
     */
    public void close(String clientId) {
        String connectionId = clientToConnection.remove(clientId);
        if (!StrUtil.isBlank(connectionId)) {
            removeEmitter(connectionId);
            broker.unregisterClient(clientId);
        }
    }

    /**
     * Checks if a client is connected locally.
     *
     * @param clientId the client ID
     * @return true if the client is connected on this instance
     */
    public boolean isLocalClient(String clientId) {
        return clientToConnection.containsKey(clientId);
    }

    /**
     * Checks if a client is connected (locally or on another instance).
     *
     * @param clientId the client ID
     * @return true if the client is connected somewhere
     */
    public boolean isClientConnected(String clientId) {
        if (clientToConnection.containsKey(clientId)) {
            return true;
        }
        return broker.getClientInstance(clientId) != null;
    }

    /**
     * Gets the instance ID for this connection manager.
     *
     * @return the instance ID
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * Gets the message broker.
     *
     * @return the message broker
     */
    public SseMessageBroker getBroker() {
        return broker;
    }

    /**
     * Gets the number of local connections.
     *
     * @return the number of active local connections
     */
    public int getLocalConnectionCount() {
        return emitters.size();
    }
}
