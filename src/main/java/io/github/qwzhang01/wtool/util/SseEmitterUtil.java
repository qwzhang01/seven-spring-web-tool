package io.github.qwzhang01.wtool.util;

import io.github.qwzhang01.wtool.domain.SseMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.qwzhang01.wtool.util.StrUtil.uuidStr;


/**
 * Utility class for managing Server-Sent Events (SSE) connections.
 * Provides methods for creating, managing, and sending messages through SSE
 * emitters.
 * Supports client connection lifecycle management and message broadcasting.
 *
 * @author avinzhang
 */
public class SseEmitterUtil {
    private static final Logger log =
            LoggerFactory.getLogger(SseEmitterUtil.class);

    /**
     * Default timeout for SSE connections: 30 minutes
     */
    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L;
    /**
     * Maps client IDs to their corresponding connection IDs
     */
    private static final ConcurrentHashMap<String, String> CLIENT =
            new ConcurrentHashMap<>();
    /**
     * Stores all active SSE emitter connections
     */
    private static final ConcurrentHashMap<String, SseEmitter> EMITTERS =
            new ConcurrentHashMap<>();

    /**
     * Gets the mapping of client IDs to connection IDs.
     *
     * @return a map of client IDs to their connection IDs
     */
    public static Map<String, String> getClientIds() {
        return CLIENT;
    }

    /**
     * Creates a new SSE emitter connection with timeout configuration.
     * If a connection already exists for the client, it will be closed first.
     *
     * @param clientId the unique identifier for the client
     * @param message  the initial message to send upon connection
     * @return a new SseEmitter instance configured with timeout and event
     * handlers
     */
    public static SseEmitter createEmitter(String clientId, String message) {
        String oldClientId = CLIENT.remove(clientId);

        if (!StrUtil.isBlank(oldClientId)) {
            // 移除旧的连接
            removeEmitter(oldClientId);
        }
        String uuid = uuidStr();
        CLIENT.put(clientId, uuid);

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        EMITTERS.put(uuid, emitter);

        // 完成时移除
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for client: {}", uuid);
            removeEmitter(uuid);
        });

        // 超时时移除
        emitter.onTimeout(() -> {
            log.info("SSE connection timeout for client: {}", uuid);
            removeEmitter(uuid);
        });

        // 错误时移除
        emitter.onError(throwable -> {
            log.error("SSE uuid error for client: {}", uuid, throwable);
            removeEmitter(uuid);
        });

        // 发送连接成功消息
        sendToClient(clientId, message);
        return emitter;
    }

    /**
     * Removes an emitter from the active connections and completes it.
     *
     * @param clientId the connection ID of the emitter to remove
     */
    private static void removeEmitter(String clientId) {
        SseEmitter emitter = EMITTERS.remove(clientId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    /**
     * Sends a message to a specific client.
     *
     * @param clientId the client ID to send the message to
     * @param message  the message content
     * @return true if the message was sent successfully, false otherwise
     */
    public static boolean sendToClient(String clientId, String message) {
        clientId = CLIENT.get(clientId);
        if (StrUtil.isBlank(clientId)) {
            return false;
        }

        SseEmitter emitter = EMITTERS.get(clientId);
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
                removeEmitter(clientId);
                return false;
            }
        }
        return false;
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message the message to broadcast
     */
    public static void broadcast(String message) {
        CLIENT.keySet().forEach(clientId -> {
            boolean send = sendToClient(clientId, message);
            if (!send) {
                close(clientId);
            }
        });
    }

    /**
     * Closes the SSE connection for the specified client.
     *
     * @param clientId the client ID whose connection should be closed
     */
    public static void close(String clientId) {
        clientId = CLIENT.remove(clientId);
        if (!StrUtil.isBlank(clientId)) {
            removeEmitter(clientId);
        }
    }
}
