package io.github.qwzhang01.wtool.sse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Local in-memory implementation of {@link SseMessageBroker}.
 * <p>
 * This implementation stores all client mappings in local memory
 * and is suitable for single-instance deployments only.
 * <p>
 * For multi-instance deployments, use {@link RedisSseMessageBroker} instead.
 *
 * @author avinzhang
 */
public class LocalSseMessageBroker implements SseMessageBroker {

    private final Map<String, String> clientToInstance = new ConcurrentHashMap<>();
    private BiConsumer<String, String> messageConsumer;
    private Consumer<String> broadcastConsumer;

    public LocalSseMessageBroker() {
    }

    @Override
    public void registerClient(String clientId, String instanceId) {
        clientToInstance.put(clientId, instanceId);
    }

    @Override
    public void unregisterClient(String clientId) {
        clientToInstance.remove(clientId);
    }

    @Override
    public String getClientInstance(String clientId) {
        return clientToInstance.get(clientId);
    }

    @Override
    public void publishToClient(String clientId, String message) {
        if (messageConsumer != null && clientToInstance.containsKey(clientId)) {
            messageConsumer.accept(clientId, message);
        }
    }

    @Override
    public void publishBroadcast(String message) {
        if (broadcastConsumer != null) {
            broadcastConsumer.accept(message);
        }
    }

    @Override
    public void subscribe(String instanceId, BiConsumer<String, String> consumer) {
        this.messageConsumer = consumer;
    }

    @Override
    public void subscribeBroadcast(Consumer<String> consumer) {
        this.broadcastConsumer = consumer;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
