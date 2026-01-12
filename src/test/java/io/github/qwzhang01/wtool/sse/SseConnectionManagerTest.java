package io.github.qwzhang01.wtool.sse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for SseConnectionManager.
 *
 * @author avinzhang
 */
class SseConnectionManagerTest {

    private SseConnectionManager manager;

    @BeforeEach
    void setUp() {
        manager = new SseConnectionManager();
    }

    @AfterEach
    void tearDown() {
        Set<String> clients = manager.getLocalClientIds();
        clients.forEach(manager::close);
    }

    @Test
    void testCreateEmitter_NewClient_Success() {
        String clientId = "client1";
        String message = "Connection established";

        SseEmitter emitter = manager.createEmitter(clientId, message);

        assertNotNull(emitter);
        assertTrue(manager.isLocalClient(clientId));
        assertTrue(manager.isClientConnected(clientId));
    }

    @Test
    void testCreateEmitter_ExistingClient_ReplacesOldConnection() {
        String clientId = "client1";
        SseEmitter first = manager.createEmitter(clientId, "First");
        SseEmitter second = manager.createEmitter(clientId, "Second");

        assertNotNull(second);
        assertNotEquals(first, second);
        assertEquals(1, manager.getLocalConnectionCount());
    }

    @Test
    void testSendToClient_LocalClient_Success() {
        String clientId = "client1";
        manager.createEmitter(clientId, "Initial");

        boolean result = manager.sendToClient(clientId, "Test message");

        assertTrue(result);
    }

    @Test
    void testSendToClient_NonExistent_ReturnsFalse() {
        boolean result = manager.sendToClient("nonexistent", "Test");

        assertFalse(result);
    }

    @Test
    void testClose_ExistingClient_Success() {
        String clientId = "client1";
        manager.createEmitter(clientId, "Initial");
        assertTrue(manager.isLocalClient(clientId));

        manager.close(clientId);

        assertFalse(manager.isLocalClient(clientId));
    }

    @Test
    void testBroadcast_NoException() {
        manager.createEmitter("client1", "Message 1");
        manager.createEmitter("client2", "Message 2");

        assertDoesNotThrow(() -> manager.broadcast("Broadcast"));
    }

    @Test
    void testBroadcastLocal_SendsToAllLocalClients() {
        manager.createEmitter("client1", "Message 1");
        manager.createEmitter("client2", "Message 2");

        assertDoesNotThrow(() -> manager.broadcastLocal("Local broadcast"));
    }

    @Test
    void testGetLocalClientIds_ReturnsCorrectSet() {
        manager.createEmitter("client1", "Message 1");
        manager.createEmitter("client2", "Message 2");

        Set<String> clientIds = manager.getLocalClientIds();

        assertEquals(2, clientIds.size());
        assertTrue(clientIds.contains("client1"));
        assertTrue(clientIds.contains("client2"));
    }

    @Test
    void testGetClientIds_ReturnsMap() {
        manager.createEmitter("client1", "Message 1");

        Map<String, String> clientIds = manager.getClientIds();

        assertNotNull(clientIds);
        assertTrue(clientIds.containsKey("client1"));
    }

    @Test
    void testGetInstanceId_NotNull() {
        String instanceId = manager.getInstanceId();

        assertNotNull(instanceId);
        assertFalse(instanceId.isBlank());
    }

    @Test
    void testGetBroker_NotNull() {
        SseMessageBroker broker = manager.getBroker();

        assertNotNull(broker);
        assertTrue(broker instanceof LocalSseMessageBroker);
    }

    @Test
    void testGetLocalConnectionCount() {
        assertEquals(0, manager.getLocalConnectionCount());

        manager.createEmitter("client1", "Message 1");
        assertEquals(1, manager.getLocalConnectionCount());

        manager.createEmitter("client2", "Message 2");
        assertEquals(2, manager.getLocalConnectionCount());

        manager.close("client1");
        assertEquals(1, manager.getLocalConnectionCount());
    }

    @Test
    void testEmitterTimeout_Configuration() {
        SseEmitter emitter = manager.createEmitter("testClient", "Test");

        assertNotNull(emitter);
        Long timeout = emitter.getTimeout();
        assertNotNull(timeout);
        assertEquals(30 * 60 * 1000L, timeout);
    }
}
