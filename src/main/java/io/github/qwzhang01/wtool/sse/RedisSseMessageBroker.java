package io.github.qwzhang01.wtool.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Redis-based implementation of {@link SseMessageBroker}.
 * <p>
 * This implementation uses Redis for:
 * <ul>
 *     <li>Storing client-to-instance mappings</li>
 *     <li>Publishing messages via Redis Pub/Sub</li>
 *     <li>Broadcasting messages to all instances</li>
 * </ul>
 * <p>
 * This enables SSE connections to work across multiple application instances.
 *
 * @author avinzhang
 */
public class RedisSseMessageBroker implements SseMessageBroker {

    private static final Logger log =
            LoggerFactory.getLogger(RedisSseMessageBroker.class);

    private static final String CLIENT_KEY_PREFIX = "sse:client:";
    private static final String CHANNEL_PREFIX = "sse:channel:";
    private static final String BROADCAST_CHANNEL = "sse:broadcast";
    private static final String MESSAGE_SEPARATOR = "::";
    private static final long CLIENT_EXPIRE_SECONDS = 3600; // 1 hour

    private final StringRedisTemplate redisTemplate;
    private final RedisMessageListenerContainer listenerContainer;
    private final String instanceId;

    private BiConsumer<String, String> messageConsumer;
    private Consumer<String> broadcastConsumer;

    /**
     * Creates a new Redis SSE message broker.
     *
     * @param redisTemplate     the Redis template for operations
     * @param listenerContainer the Redis message listener container
     * @param instanceId        unique identifier for this instance
     */
    public RedisSseMessageBroker(StringRedisTemplate redisTemplate,
                                  RedisMessageListenerContainer listenerContainer,
                                  String instanceId) {
        this.redisTemplate = redisTemplate;
        this.listenerContainer = listenerContainer;
        this.instanceId = instanceId;
    }

    @Override
    public void registerClient(String clientId, String instanceId) {
        String key = CLIENT_KEY_PREFIX + clientId;
        redisTemplate.opsForValue().set(key, instanceId, CLIENT_EXPIRE_SECONDS,
                TimeUnit.SECONDS);
        log.debug("Registered client {} on instance {}", clientId, instanceId);
    }

    @Override
    public void unregisterClient(String clientId) {
        String key = CLIENT_KEY_PREFIX + clientId;
        redisTemplate.delete(key);
        log.debug("Unregistered client {}", clientId);
    }

    @Override
    public String getClientInstance(String clientId) {
        String key = CLIENT_KEY_PREFIX + clientId;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void publishToClient(String clientId, String message) {
        String targetInstance = getClientInstance(clientId);
        if (targetInstance == null) {
            log.warn("Client {} not found, cannot publish message", clientId);
            return;
        }

        String channel = CHANNEL_PREFIX + targetInstance;
        String payload = clientId + MESSAGE_SEPARATOR + message;
        redisTemplate.convertAndSend(channel, payload);
        log.debug("Published message to client {} via channel {}", clientId,
                channel);
    }

    @Override
    public void publishBroadcast(String message) {
        redisTemplate.convertAndSend(BROADCAST_CHANNEL, message);
        log.debug("Published broadcast message");
    }

    @Override
    public void subscribe(String instanceId, BiConsumer<String, String> consumer) {
        this.messageConsumer = consumer;
        String channel = CHANNEL_PREFIX + instanceId;

        listenerContainer.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                String payload = new String(message.getBody(),
                        StandardCharsets.UTF_8);
                int separatorIndex = payload.indexOf(MESSAGE_SEPARATOR);
                if (separatorIndex > 0) {
                    String clientId = payload.substring(0, separatorIndex);
                    String msg = payload.substring(separatorIndex +
                            MESSAGE_SEPARATOR.length());
                    if (messageConsumer != null) {
                        messageConsumer.accept(clientId, msg);
                    }
                }
            }
        }, new ChannelTopic(channel));

        log.info("Subscribed to channel {} for instance {}", channel, instanceId);
    }

    @Override
    public void subscribeBroadcast(Consumer<String> consumer) {
        this.broadcastConsumer = consumer;

        listenerContainer.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                String msg = new String(message.getBody(), StandardCharsets.UTF_8);
                if (broadcastConsumer != null) {
                    broadcastConsumer.accept(msg);
                }
            }
        }, new ChannelTopic(BROADCAST_CHANNEL));

        log.info("Subscribed to broadcast channel");
    }

    @Override
    public boolean isAvailable() {
        try {
            redisTemplate.hasKey("sse:health:check");
            return true;
        } catch (Exception e) {
            log.warn("Redis is not available", e);
            return false;
        }
    }

    /**
     * Refreshes the client registration TTL.
     * Should be called periodically to keep the client registration alive.
     *
     * @param clientId the client identifier
     */
    public void refreshClient(String clientId) {
        String key = CLIENT_KEY_PREFIX + clientId;
        redisTemplate.expire(key, CLIENT_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Gets the instance ID for this broker.
     *
     * @return the instance ID
     */
    public String getInstanceId() {
        return instanceId;
    }
}
