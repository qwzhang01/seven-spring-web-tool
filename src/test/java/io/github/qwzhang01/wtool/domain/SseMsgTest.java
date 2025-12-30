package io.github.qwzhang01.wtool.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test cases for SseMsg class.
 * Tests cover message creation, getters/setters, and message types.
 *
 * @author avinzhang
 */
class SseMsgTest {

    @Test
    void testConstructor_ValidParameters_Success() {
        // Arrange
        String content = "Test message";
        String sender = "system";
        SseMsg.MessageType type = SseMsg.MessageType.TEXT;

        // Act
        SseMsg message = new SseMsg(content, sender, type);

        // Assert
        assertNotNull(message);
        assertEquals(content, message.getContent());
        assertEquals(sender, message.getSender());
        assertEquals(type, message.getType());
    }

    @Test
    void testConstructor_GeneratesUniqueId() {
        // Act
        SseMsg message1 = new SseMsg("Message 1", "user1",
                SseMsg.MessageType.TEXT);
        SseMsg message2 = new SseMsg("Message 2", "user2",
                SseMsg.MessageType.TEXT);

        // Assert
        assertNotNull(message1.getId());
        assertNotNull(message2.getId());
        assertNotEquals(message1.getId(), message2.getId());
    }

    @Test
    void testConstructor_SetsTimestampAutomatically() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        SseMsg message = new SseMsg("Test", "system", SseMsg.MessageType.TEXT);

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertNotNull(message.getTimestamp());
        assertTrue(!message.getTimestamp().isBefore(before));
        assertTrue(!message.getTimestamp().isAfter(after));
    }

    @Test
    void testConstructor_IdIsValidUuid() {
        // Act
        SseMsg message = new SseMsg("Test", "system", SseMsg.MessageType.TEXT);

        // Assert
        String id = message.getId();
        assertNotNull(id);
        assertEquals(32, id.length()); // UUID without hyphens
        assertTrue(id.matches("^[0-9a-f]{32}$")); // Only hex characters
    }

    @Test
    void testSetId_UpdatesId() {
        // Arrange
        SseMsg message = new SseMsg("Test", "system", SseMsg.MessageType.TEXT);
        String newId = "newtestid123";

        // Act
        message.setId(newId);

        // Assert
        assertEquals(newId, message.getId());
    }

    @Test
    void testSetContent_UpdatesContent() {
        // Arrange
        SseMsg message = new SseMsg("Initial content", "system",
                SseMsg.MessageType.TEXT);
        String newContent = "Updated content";

        // Act
        message.setContent(newContent);

        // Assert
        assertEquals(newContent, message.getContent());
    }

    @Test
    void testSetSender_UpdatesSender() {
        // Arrange
        SseMsg message = new SseMsg("Test", "system", SseMsg.MessageType.TEXT);
        String newSender = "user123";

        // Act
        message.setSender(newSender);

        // Assert
        assertEquals(newSender, message.getSender());
    }

    @Test
    void testSetTimestamp_UpdatesTimestamp() {
        // Arrange
        SseMsg message = new SseMsg("Test", "system", SseMsg.MessageType.TEXT);
        LocalDateTime newTimestamp = LocalDateTime.of(2025, 12, 25, 10, 30);

        // Act
        message.setTimestamp(newTimestamp);

        // Assert
        assertEquals(newTimestamp, message.getTimestamp());
    }

    @Test
    void testSetType_UpdatesType() {
        // Arrange
        SseMsg message = new SseMsg("Test", "system", SseMsg.MessageType.TEXT);

        // Act
        message.setType(SseMsg.MessageType.ALERT);

        // Assert
        assertEquals(SseMsg.MessageType.ALERT, message.getType());
    }

    @Test
    void testMessageType_Text() {
        // Act
        SseMsg message = new SseMsg("Text message", "user",
                SseMsg.MessageType.TEXT);

        // Assert
        assertEquals(SseMsg.MessageType.TEXT, message.getType());
    }

    @Test
    void testMessageType_Notification() {
        // Act
        SseMsg message = new SseMsg("Notification message", "user",
                SseMsg.MessageType.NOTIFICATION);

        // Assert
        assertEquals(SseMsg.MessageType.NOTIFICATION, message.getType());
    }

    @Test
    void testMessageType_Alert() {
        // Act
        SseMsg message = new SseMsg("Alert message", "user",
                SseMsg.MessageType.ALERT);

        // Assert
        assertEquals(SseMsg.MessageType.ALERT, message.getType());
    }

    @Test
    void testMessageType_System() {
        // Act
        SseMsg message = new SseMsg("System message", "system",
                SseMsg.MessageType.SYSTEM);

        // Assert
        assertEquals(SseMsg.MessageType.SYSTEM, message.getType());
    }

    @Test
    void testConstructor_NullContent_Allowed() {
        // Act
        SseMsg message = new SseMsg(null, "system", SseMsg.MessageType.TEXT);

        // Assert
        assertNull(message.getContent());
        assertNotNull(message.getId());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void testConstructor_EmptyContent_Allowed() {
        // Act
        SseMsg message = new SseMsg("", "system", SseMsg.MessageType.TEXT);

        // Assert
        assertEquals("", message.getContent());
    }

    @Test
    void testConstructor_NullSender_Allowed() {
        // Act
        SseMsg message = new SseMsg("Test", null, SseMsg.MessageType.TEXT);

        // Assert
        assertNull(message.getSender());
    }

    @Test
    void testConstructor_EmptySender_Allowed() {
        // Act
        SseMsg message = new SseMsg("Test", "", SseMsg.MessageType.TEXT);

        // Assert
        assertEquals("", message.getSender());
    }

    @Test
    void testConstructor_LongContent_Success() {
        // Arrange
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longContent.append("Long content ");
        }

        // Act
        SseMsg message = new SseMsg(longContent.toString(), "system",
                SseMsg.MessageType.TEXT);

        // Assert
        assertEquals(longContent.toString(), message.getContent());
    }

    @Test
    void testConstructor_SpecialCharactersInContent_Success() {
        // Arrange
        String content = "Special chars: @#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";

        // Act
        SseMsg message = new SseMsg(content, "system", SseMsg.MessageType.TEXT);

        // Assert
        assertEquals(content, message.getContent());
    }

    @Test
    void testConstructor_UnicodeContent_Success() {
        // Arrange
        String content = "Unicode: 你好世界 🎉🎊 مرحبا العالم";

        // Act
        SseMsg message = new SseMsg(content, "system", SseMsg.MessageType.TEXT);

        // Assert
        assertEquals(content, message.getContent());
    }

    @Test
    void testConstructor_MultilineContent_Success() {
        // Arrange
        String content = "Line 1\nLine 2\nLine 3";

        // Act
        SseMsg message = new SseMsg(content, "system", SseMsg.MessageType.TEXT);

        // Assert
        assertEquals(content, message.getContent());
    }

    @Test
    void testGetters_AllFieldsAccessible() {
        // Arrange
        String content = "Test content";
        String sender = "testUser";
        SseMsg.MessageType type = SseMsg.MessageType.NOTIFICATION;

        // Act
        SseMsg message = new SseMsg(content, sender, type);

        // Assert
        assertNotNull(message.getId());
        assertEquals(content, message.getContent());
        assertEquals(sender, message.getSender());
        assertNotNull(message.getTimestamp());
        assertEquals(type, message.getType());
    }

    @Test
    void testSetters_AllFieldsModifiable() {
        // Arrange
        SseMsg message = new SseMsg("Initial", "system",
                SseMsg.MessageType.TEXT);

        String newId = "customid123";
        String newContent = "Updated content";
        String newSender = "updatedUser";
        LocalDateTime newTimestamp = LocalDateTime.of(2025, 1, 1, 12, 0);
        SseMsg.MessageType newType = SseMsg.MessageType.ALERT;

        // Act
        message.setId(newId);
        message.setContent(newContent);
        message.setSender(newSender);
        message.setTimestamp(newTimestamp);
        message.setType(newType);

        // Assert
        assertEquals(newId, message.getId());
        assertEquals(newContent, message.getContent());
        assertEquals(newSender, message.getSender());
        assertEquals(newTimestamp, message.getTimestamp());
        assertEquals(newType, message.getType());
    }

    @Test
    void testMessageType_AllTypesAvailable() {
        // Act & Assert
        assertNotNull(SseMsg.MessageType.TEXT);
        assertNotNull(SseMsg.MessageType.NOTIFICATION);
        assertNotNull(SseMsg.MessageType.ALERT);
        assertNotNull(SseMsg.MessageType.SYSTEM);
    }

    @Test
    void testMessageType_EnumValues() {
        // Act
        SseMsg.MessageType[] types = SseMsg.MessageType.values();

        // Assert
        assertEquals(4, types.length);
        assertEquals(SseMsg.MessageType.TEXT, types[0]);
        assertEquals(SseMsg.MessageType.NOTIFICATION, types[1]);
        assertEquals(SseMsg.MessageType.ALERT, types[2]);
        assertEquals(SseMsg.MessageType.SYSTEM, types[3]);
    }

    @Test
    void testConstructor_MultipleMessages_IndependentTimestamps() throws InterruptedException {
        // Act
        SseMsg message1 = new SseMsg("Message 1", "user1",
                SseMsg.MessageType.TEXT);
        Thread.sleep(10); // Small delay to ensure different timestamps
        SseMsg message2 = new SseMsg("Message 2", "user2",
                SseMsg.MessageType.TEXT);

        // Assert
        assertTrue(message2.getTimestamp().isAfter(message1.getTimestamp()) ||
                message2.getTimestamp().isEqual(message1.getTimestamp()));
    }
}
