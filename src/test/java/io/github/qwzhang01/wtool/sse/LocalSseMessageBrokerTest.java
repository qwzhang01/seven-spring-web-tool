package io.github.qwzhang01.wtool.sse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for LocalSseMessageBroker.
 *
 * @author avinzhang
 */
class LocalSseMessageBrokerTest {

    private LocalSseMessageBroker broker;

    @BeforeEach
    void setUp() {
        broker = new LocalSseMessageBroker();
    }

    @Test
    void testIsAvailable_ReturnsTrue() {
        assertTrue(broker.isAvailable());
    }

    @Test
    void testRegisterClient_Success() {
        broker.registerClient("client1", "instance1");

        String instance = broker.getClientInstance("client1");
        assertEquals("instance1", instance);
    }

    @Test
    void testUnregisterClient_Success() {
        broker.registerClient("client1", "instance1");
        broker.unregisterClient("client1");

        assertNull(broker.getClientInstance("client1"));
    }

    @Test
    void testGetClientInstance_NonExistent_ReturnsNull() {
        assertNull(broker.getClientInstance("nonexistent"));
    }

    @Test
    void testPublishToClient_WithSubscriber_DeliverMessage() {
        AtomicReference<String> receivedClientId = new AtomicReference<>();
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        broker.subscribe("instance1", (clientId, message) -> {
            receivedClientId.set(clientId);
            receivedMessage.set(message);
        });

        broker.registerClient("client1", "instance1");
        broker.publishToClient("client1", "Hello");

        assertEquals("client1", receivedClientId.get());
        assertEquals("Hello", receivedMessage.get());
    }

    @Test
    void testPublishToClient_NoSubscriber_NoException() {
        broker.registerClient("client1", "instance1");

        assertDoesNotThrow(() -> broker.publishToClient("client1", "Hello"));
    }

    @Test
    void testPublishToClient_NonExistentClient_NoDelivery() {
        AtomicBoolean called = new AtomicBoolean(false);

        broker.subscribe("instance1", (clientId, message) -> called.set(true));
        broker.publishToClient("nonexistent", "Hello");

        assertFalse(called.get());
    }

    @Test
    void testPublishBroadcast_WithSubscriber_DeliverMessage() {
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        broker.subscribeBroadcast(receivedMessage::set);
        broker.publishBroadcast("Broadcast message");

        assertEquals("Broadcast message", receivedMessage.get());
    }

    @Test
    void testPublishBroadcast_NoSubscriber_NoException() {
        assertDoesNotThrow(() -> broker.publishBroadcast("Broadcast"));
    }

    @Test
    void testMultipleClients_RegisterAndRetrieve() {
        broker.registerClient("client1", "instance1");
        broker.registerClient("client2", "instance2");
        broker.registerClient("client3", "instance1");

        assertEquals("instance1", broker.getClientInstance("client1"));
        assertEquals("instance2", broker.getClientInstance("client2"));
        assertEquals("instance1", broker.getClientInstance("client3"));
    }
}
