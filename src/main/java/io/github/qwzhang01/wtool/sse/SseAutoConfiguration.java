package io.github.qwzhang01.wtool.sse;

import io.github.qwzhang01.wtool.util.SseEmitterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Auto-configuration for SSE multi-instance support.
 * <p>
 * This configuration automatically sets up the appropriate
 * {@link SseMessageBroker} based on available dependencies:
 * <ul>
 *     <li>If Redis is available and enabled, uses
 *     {@link RedisSseMessageBroker}</li>
 *     <li>Otherwise, falls back to {@link LocalSseMessageBroker}</li>
 * </ul>
 * <p>
 * Configuration properties:
 * <pre>
 * # Enable/disable Redis-based SSE (default: true if Redis is available)
 * sse.redis.enabled=true
 *
 * # Custom instance ID (default: auto-generated)
 * sse.instance.id=my-instance-1
 * </pre>
 *
 * @author avinzhang
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@ConditionalOnClass(SseEmitterUtil.class)
public class SseAutoConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(SseAutoConfiguration.class);

    @Value("${sse.instance.id:}")
    private String configuredInstanceId;

    /**
     * Generates a unique instance ID for this application instance.
     */
    private String generateInstanceId() {
        if (configuredInstanceId != null && !configuredInstanceId.isBlank()) {
            return configuredInstanceId;
        }

        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return hostname + "-" + UUID.randomUUID().toString().substring(0, 8);
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Redis-specific SSE configuration.
     * Only activated when Redis is available.
     */
    @Configuration
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnProperty(name = "sse.redis.enabled", havingValue = "true",
            matchIfMissing = true)
    static class RedisSseConfiguration {

        private static final Logger log =
                LoggerFactory.getLogger(RedisSseConfiguration.class);

        @Value("${sse.instance.id:}")
        private String configuredInstanceId;

        private String generateInstanceId() {
            if (configuredInstanceId != null && !configuredInstanceId.isBlank()) {
                return configuredInstanceId;
            }
            try {
                String hostname = InetAddress.getLocalHost().getHostName();
                return hostname + "-" + UUID.randomUUID().toString().substring(0, 8);
            } catch (Exception e) {
                return UUID.randomUUID().toString();
            }
        }

        /**
         * Creates a Redis message listener container for SSE pub/sub.
         */
        @Bean
        @ConditionalOnMissingBean(RedisMessageListenerContainer.class)
        public RedisMessageListenerContainer sseRedisMessageListenerContainer(
                RedisConnectionFactory connectionFactory) {
            log.info("Creating Redis message listener container for SSE");
            RedisMessageListenerContainer container =
                    new RedisMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            return container;
        }

        /**
         * Creates a Redis-based SSE message broker.
         */
        @Bean
        public SseMessageBroker redisSseMessageBroker(
                StringRedisTemplate redisTemplate,
                RedisMessageListenerContainer listenerContainer) {
            String instanceId = generateInstanceId();
            log.info("Creating Redis SSE message broker with instance ID: {}",
                    instanceId);
            return new RedisSseMessageBroker(redisTemplate, listenerContainer,
                    instanceId);
        }
    }

    /**
     * Creates a local SSE message broker as fallback.
     */
    @Bean
    public SseMessageBroker localSseMessageBroker() {
        log.info("Creating local SSE message broker (single-instance mode)");
        return new LocalSseMessageBroker();
    }

    /**
     * Creates the SSE connection manager.
     */
    @Bean
    @ConditionalOnMissingBean(SseConnectionManager.class)
    public SseConnectionManager sseConnectionManager(SseMessageBroker localSseMessageBroker,
                                                     SseMessageBroker redisSseMessageBroker) {
        String instanceId = generateInstanceId();
        SseConnectionManager manager = new SseConnectionManager(redisSseMessageBroker != null ? redisSseMessageBroker : localSseMessageBroker, instanceId);

        // Configure the static utility class
        SseEmitterUtil.setConnectionManager(manager);

        log.info("SSE Connection Manager initialized with broker: {}",
                manager.getBroker().getClass().getSimpleName());
        return manager;
    }
}
