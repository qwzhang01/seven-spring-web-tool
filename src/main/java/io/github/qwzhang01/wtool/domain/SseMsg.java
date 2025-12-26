package io.github.qwzhang01.wtool.domain;

import java.time.LocalDateTime;

import static io.github.qwzhang01.wtool.util.StrUtil.uuidStr;

/**
 * Server-Sent Events (SSE) message entity.
 * Represents a message that can be sent through SSE connection,
 * including message content, sender information, timestamp, and message type.
 *
 * @author avinzhang
 */
public class SseMsg {
    private String id;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private MessageType type;

    /**
     * Constructs a new SSE message with the specified content, sender, and type.
     * The message ID and timestamp are automatically generated.
     *
     * @param content the message content
     * @param sender  the sender identifier
     * @param type    the type of the message
     */
    public SseMsg(String content, String sender, MessageType type) {
        this.id = uuidStr();
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * Enumeration of message types for SSE messages.
     */
    public static enum MessageType {
        /** Regular text message */
        TEXT,
        /** Notification message */
        NOTIFICATION,
        /** Alert message requiring attention */
        ALERT,
        /** System-generated message */
        SYSTEM
    }
}