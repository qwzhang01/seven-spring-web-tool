package io.github.qwzhang01.wtool.sse;

import java.util.function.BiConsumer;

/**
 * Interface for SSE message broker that enables message distribution
 * across multiple application instances.
 * <p>
 * Implementations can use different backends such as local memory,
 * Redis, or other message queues to support multi-instance deployments.
 *
 * @author avinzhang
 */
public interface SseMessageBroker {

    /**
     * Registers a client connection with the broker.
     *
     * @param clientId   the unique client identifier
     * @param instanceId the current instance identifier
     */
    void registerClient(String clientId, String instanceId);

    /**
     * Unregisters a client connection from the broker.
     *
     * @param clientId the unique client identifier
     */
    void unregisterClient(String clientId);

    /**
     * Gets the instance ID where the specified client is connected.
     *
     * @param clientId the unique client identifier
     * @return the instance ID, or null if client is not connected
     */
    String getClientInstance(String clientId);

    /**
     * Publishes a message to a specific client.
     * The broker will route the message to the correct instance.
     *
     * @param clientId the target client identifier
     * @param message  the message content
     */
    void publishToClient(String clientId, String message);

    /**
     * Publishes a message to all connected clients.
     *
     * @param message the message content
     */
    void publishBroadcast(String message);

    /**
     * Subscribes to messages for the current instance.
     * The consumer will be called when a message arrives for a local client.
     *
     * @param instanceId the current instance identifier
     * @param consumer   the message consumer (clientId, message)
     */
    void subscribe(String instanceId, BiConsumer<String, String> consumer);

    /**
     * Subscribes to broadcast messages.
     * The consumer will be called for all broadcast messages.
     *
     * @param consumer the message consumer (receives message only)
     */
    void subscribeBroadcast(java.util.function.Consumer<String> consumer);

    /**
     * Checks if the broker is available and ready.
     *
     * @return true if the broker is ready
     */
    boolean isAvailable();
}
