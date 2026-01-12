package io.github.qwzhang01.wtool.util;

import io.github.qwzhang01.wtool.sse.SseConnectionManager;
import io.github.qwzhang01.wtool.sse.SseMessageBroker;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * Utility class for managing Server-Sent Events (SSE) connections.
 * Provides methods for creating, managing, and sending messages through SSE
 * emitters.
 * Supports client connection lifecycle management and message broadcasting.
 * <p>
 * This class now supports multi-instance deployment through
 * {@link SseConnectionManager}. For single-instance deployments, it works
 * out of the box. For multi-instance deployments, configure a message broker
 * using {@link #setConnectionManager(SseConnectionManager)}.
 * <p>
 * Example for multi-instance setup with Redis:
 * <pre>
 * RedisSseMessageBroker broker = new RedisSseMessageBroker(
 *     redisTemplate, listenerContainer, "instance-1");
 * SseConnectionManager manager = new SseConnectionManager(broker, "instance-1");
 * SseEmitterUtil.setConnectionManager(manager);
 * </pre>
 *
 * @author avinzhang
 * @see SseConnectionManager
 * @see SseMessageBroker
 */
public class SseEmitterUtil {

    /**
     * The connection manager that handles SSE connections.
     * Defaults to a local single-instance manager.
     */
    private static volatile SseConnectionManager connectionManager =
            new SseConnectionManager();

    /**
     * Private constructor to prevent instantiation.
     */
    private SseEmitterUtil() {
    }

    /**
     * Sets the connection manager for multi-instance support.
     * <p>
     * Call this method during application startup to configure
     * multi-instance SSE support with a custom broker (e.g., Redis).
     *
     * @param manager the connection manager to use
     */
    public static void setConnectionManager(SseConnectionManager manager) {
        if (manager != null) {
            connectionManager = manager;
        }
    }

    /**
     * Gets the current connection manager.
     *
     * @return the connection manager
     */
    public static SseConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Gets the mapping of client IDs to connection IDs.
     *
     * @return a map of client IDs to their connection IDs
     */
    public static Map<String, String> getClientIds() {
        return connectionManager.getClientIds();
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
    public static SseEmitter createEmitter(String clientId, String message) {
        return connectionManager.createEmitter(clientId, message);
    }

    /**
     * Sends a message to a specific client.
     * In multi-instance mode, the message will be routed to the correct
     * instance if the client is connected elsewhere.
     *
     * @param clientId the client ID to send the message to
     * @param message  the message content
     * @return true if the message was sent successfully, false otherwise
     */
    public static boolean sendToClient(String clientId, String message) {
        return connectionManager.sendToClient(clientId, message);
    }

    /**
     * Broadcasts a message to all connected clients.
     * In multi-instance mode, the message will be sent to clients
     * across all instances.
     *
     * @param message the message to broadcast
     */
    public static void broadcast(String message) {
        connectionManager.broadcast(message);
    }

    /**
     * Closes the SSE connection for the specified client.
     *
     * @param clientId the client ID whose connection should be closed
     */
    public static void close(String clientId) {
        connectionManager.close(clientId);
    }

    /**
     * Checks if a client is connected (locally or on another instance).
     *
     * @param clientId the client ID
     * @return true if the client is connected
     */
    public static boolean isClientConnected(String clientId) {
        return connectionManager.isClientConnected(clientId);
    }

    /**
     * Gets the number of local connections on this instance.
     *
     * @return the number of active local connections
     */
    public static int getLocalConnectionCount() {
        return connectionManager.getLocalConnectionCount();
    }

    /**
     * Gets the instance ID for the current connection manager.
     *
     * @return the instance ID
     */
    public static String getInstanceId() {
        return connectionManager.getInstanceId();
    }
}
