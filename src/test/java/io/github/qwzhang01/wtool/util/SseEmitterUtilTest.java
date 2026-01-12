package io.github.qwzhang01.wtool.util;

import io.github.qwzhang01.wtool.sse.SseConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test cases for SseEmitterUtil class.
 * Tests cover SSE connection creation, message sending, broadcasting, and
 * connection management.
 *
 * @author avinzhang
 */
class SseEmitterUtilTest {

    @BeforeEach
    void setUp() {
        // Reset connection manager before each test
        SseEmitterUtil.setConnectionManager(new SseConnectionManager());
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        Map<String, String> clients = SseEmitterUtil.getClientIds();
        clients.keySet().forEach(SseEmitterUtil::close);
    }

    @Test
    void testCreateEmitter_NewClient_Success() {
        // Arrange
        String clientId = "client1";
        String message = "Connection established";

        // Act
        SseEmitter emitter = SseEmitterUtil.createEmitter(clientId, message);

        // Assert
        assertNotNull(emitter);
        assertTrue(SseEmitterUtil.getClientIds().containsKey(clientId));
    }

    @Test
    void testCreateEmitter_ExistingClient_ReplacesOldConnection() {
        // Arrange
        String clientId = "client1";
        SseEmitter firstEmitter = SseEmitterUtil.createEmitter(clientId,
                "First connection");

        // Act - Create new connection with same clientId
        SseEmitter secondEmitter = SseEmitterUtil.createEmitter(clientId,
                "Second connection");

        // Assert
        assertNotNull(secondEmitter);
        assertNotEquals(firstEmitter, secondEmitter);
        assertTrue(SseEmitterUtil.getClientIds().containsKey(clientId));
        assertEquals(1, SseEmitterUtil.getClientIds().size());
    }

    @Test
    void testCreateEmitter_MultipleClients_Success() {
        // Act
        SseEmitter emitter1 = SseEmitterUtil.createEmitter("client1",
                "Message 1");
        SseEmitter emitter2 = SseEmitterUtil.createEmitter("client2",
                "Message 2");
        SseEmitter emitter3 = SseEmitterUtil.createEmitter("client3",
                "Message 3");

        // Assert
        assertNotNull(emitter1);
        assertNotNull(emitter2);
        assertNotNull(emitter3);
        assertEquals(3, SseEmitterUtil.getClientIds().size());
    }

    @Test
    void testSendToClient_NonExistentClient_ReturnsFalse() {
        // Act
        boolean result = SseEmitterUtil.sendToClient("nonexistent", "Test " +
                "message");

        // Assert
        assertFalse(result);
    }

    @Test
    void testSendToClient_ExistingClient_ReturnsTrue() {
        // Arrange
        String clientId = "client1";
        SseEmitterUtil.createEmitter(clientId, "Initial message");

        // Act
        boolean result = SseEmitterUtil.sendToClient(clientId, "Test message");

        // Assert
        assertTrue(result);
    }

    @Test
    void testSendToClient_EmptyClientId_ReturnsFalse() {
        // Act
        boolean result = SseEmitterUtil.sendToClient("", "Test message");

        // Assert
        assertFalse(result);
    }

    @Test
    void testSendToClient_BlankClientId_ReturnsFalse() {
        // Act
        boolean result = SseEmitterUtil.sendToClient("   ", "Test message");

        // Assert
        assertFalse(result);
    }

    @Test
    void testBroadcast_NoClients_NoException() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> SseEmitterUtil.broadcast("Test broadcast"));
    }

    @Test
    void testBroadcast_MultipleClients_Success() {
        // Arrange
        SseEmitterUtil.createEmitter("client1", "Message 1");
        SseEmitterUtil.createEmitter("client2", "Message 2");
        SseEmitterUtil.createEmitter("client3", "Message 3");

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> SseEmitterUtil.broadcast("Broadcast to all"));
    }

    @Test
    void testClose_ExistingClient_Success() {
        // Arrange
        String clientId = "client1";
        SseEmitterUtil.createEmitter(clientId, "Initial message");
        assertTrue(SseEmitterUtil.getClientIds().containsKey(clientId));

        // Act
        SseEmitterUtil.close(clientId);

        // Assert
        assertFalse(SseEmitterUtil.getClientIds().containsKey(clientId));
    }

    @Test
    void testClose_NonExistentClient_NoException() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> SseEmitterUtil.close("nonexistent"));
    }

    @Test
    void testClose_EmptyClientId_NoException() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> SseEmitterUtil.close(""));
    }

    @Test
    void testGetClientIds_InitiallyEmpty() {
        // Act
        Map<String, String> clientIds = SseEmitterUtil.getClientIds();

        // Assert
        assertNotNull(clientIds);
        assertTrue(clientIds.isEmpty());
    }

    @Test
    void testGetClientIds_AfterAddingClients_ReturnsCorrectSize() {
        // Arrange
        SseEmitterUtil.createEmitter("client1", "Message 1");
        SseEmitterUtil.createEmitter("client2", "Message 2");

        // Act
        Map<String, String> clientIds = SseEmitterUtil.getClientIds();

        // Assert
        assertNotNull(clientIds);
        assertEquals(2, clientIds.size());
        assertTrue(clientIds.containsKey("client1"));
        assertTrue(clientIds.containsKey("client2"));
    }

    @Test
    void testGetClientIds_ConnectionIdsAreUnique() {
        // Arrange
        SseEmitterUtil.createEmitter("client1", "Message 1");
        SseEmitterUtil.createEmitter("client2", "Message 2");
        SseEmitterUtil.createEmitter("client3", "Message 3");

        // Act
        Map<String, String> clientIds = SseEmitterUtil.getClientIds();

        // Assert
        assertEquals(3, clientIds.size());
        long uniqueConnectionIds =
                clientIds.values().stream().distinct().count();
        assertEquals(3, uniqueConnectionIds); // All connection IDs should be
        // unique
    }

    @Test
    void testEmitterLifecycle_CreateSendClose() {
        // Arrange
        String clientId = "testClient";

        // Act & Assert - Create
        SseEmitter emitter = SseEmitterUtil.createEmitter(clientId, "Hello");
        assertNotNull(emitter);
        assertTrue(SseEmitterUtil.getClientIds().containsKey(clientId));

        // Act & Assert - Send
        boolean sendResult = SseEmitterUtil.sendToClient(clientId, "Test " +
                "message");
        assertTrue(sendResult);

        // Act & Assert - Close
        SseEmitterUtil.close(clientId);
        assertFalse(SseEmitterUtil.getClientIds().containsKey(clientId));
    }

    @Test
    void testConcurrentConnections_MultipleDifferentClients() {
        // Arrange & Act - Create multiple connections
        for (int i = 0; i < 10; i++) {
            SseEmitterUtil.createEmitter("client" + i, "Message " + i);
        }

        // Assert
        assertEquals(10, SseEmitterUtil.getClientIds().size());

        // Act - Send to each client
        for (int i = 0; i < 10; i++) {
            boolean result = SseEmitterUtil.sendToClient("client" + i,
                    "Update " + i);
            assertTrue(result);
        }

        // Act - Close all connections
        for (int i = 0; i < 10; i++) {
            SseEmitterUtil.close("client" + i);
        }

        // Assert
        assertTrue(SseEmitterUtil.getClientIds().isEmpty());
    }

    @Test
    void testSendToClient_AfterClose_ReturnsFalse() {
        // Arrange
        String clientId = "client1";
        SseEmitterUtil.createEmitter(clientId, "Initial");

        // Act - Close connection
        SseEmitterUtil.close(clientId);

        // Act - Try to send to closed connection
        boolean result = SseEmitterUtil.sendToClient(clientId, "Test");

        // Assert
        assertFalse(result);
    }

    @Test
    void testBroadcast_WithClosedConnections_HandlesGracefully() {
        // Arrange
        SseEmitterUtil.createEmitter("client1", "Message 1");
        SseEmitterUtil.createEmitter("client2", "Message 2");
        SseEmitterUtil.close("client1"); // Close one connection

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> SseEmitterUtil.broadcast("Broadcast message"));
    }

    @Test
    void testEmitterTimeout_Configuration() {
        // Act
        SseEmitter emitter = SseEmitterUtil.createEmitter("testClient", "Test");

        // Assert - Check that timeout is set (30 minutes = 1,800,000
        // milliseconds)
        assertNotNull(emitter);
        Long timeout = emitter.getTimeout();
        assertNotNull(timeout);
        assertEquals(30 * 60 * 1000L, timeout);
    }
}
