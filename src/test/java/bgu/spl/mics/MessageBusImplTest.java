package bgu.spl.mics;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageBusImplTest {

    private static final Logger logger = Logger.getLogger(MessageBusImplTest.class.getName());

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;

    @BeforeEach
    public void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new MicroService("MicroService1") {
            @Override
            protected void initialize() {
                subscribeEvent(TestEvent.class, (TestEvent e) -> {
                    complete(e, "Completed");
                });
                subscribeBroadcast(TestBroadcast.class, (TestBroadcast b) -> {
                    // Handle broadcast
                });
            }
        };
        microService2 = new MicroService("MicroService2") {
            @Override
            protected void initialize() {
                subscribeEvent(TestEvent.class, (TestEvent e) -> {
                    complete(e, "Completed");
                });
                subscribeBroadcast(TestBroadcast.class, (TestBroadcast b) -> {
                    // Handle broadcast
                });
            }
        };
        new Thread(microService1).start();
        new Thread(microService2).start();
        messageBus.register(microService1);
        messageBus.register(microService2);
    }

    @Test
    public void testSubscribeEvent() {
        logger.info("Starting testSubscribeEvent");
        messageBus.subscribeEvent(TestEvent.class, microService1);
        Future<String> future = messageBus.sendEvent(new TestEvent());
        assertNotNull(future);
    }

    @Test
    public void testSubscribeBroadcast() {
        logger.info("Starting testSubscribeBroadcast");
        messageBus.subscribeBroadcast(TestBroadcast.class, microService1);
        messageBus.sendBroadcast(new TestBroadcast());
        try {
            Message message = messageBus.awaitMessage(microService1, 1, TimeUnit.SECONDS);
            assertTrue(message instanceof TestBroadcast);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        } catch (TimeoutException e) {
            fail("awaitMessage timed out");
        }
    }

    @Test
    public void testComplete() {
        logger.info("Starting testComplete");
        messageBus.subscribeEvent(TestEvent.class, microService1);
        Event<String> event = new TestEvent();
        Future<String> future = messageBus.sendEvent(event);
        assertNotNull(future);
        messageBus.complete(event, "result");
        assertTrue(future.isDone());
        assertEquals("result", future.get());
    }

    @Test
    public void testSendBroadcast() {
        logger.info("Starting testSendBroadcast");
        messageBus.subscribeBroadcast(TestBroadcast.class, microService1);
        messageBus.sendBroadcast(new TestBroadcast());
        try {
            Message message = messageBus.awaitMessage(microService1, 1, TimeUnit.SECONDS);
            assertTrue(message instanceof TestBroadcast);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        } catch (TimeoutException e) {
            fail("awaitMessage timed out");
        }
    }

    @Test
    public void testSendEvent() {
        logger.info("Starting testSendEvent");
        messageBus.subscribeEvent(TestEvent.class, microService1);
        Future<String> future = messageBus.sendEvent(new TestEvent());
        assertNotNull(future);
    }

    @Test
    public void testRegister() {
        logger.info("Starting testRegister");
        MicroService microService = new MicroService("MicroService3") {
            @Override
            protected void initialize() {
                subscribeEvent(TestEvent.class, (TestEvent e) -> {
                    complete(e, "Completed");
                });
            }
        };
        new Thread(microService).start();
        messageBus.register(microService);
        
        // Subscribe the microservice to an event
        messageBus.subscribeEvent(TestEvent.class, microService);
        
        // Send an event to the microservice
        messageBus.sendEvent(new TestEvent());
        
        // Ensure that the microservice can receive the message
        assertDoesNotThrow(() -> messageBus.awaitMessage(microService, 1, TimeUnit.SECONDS));
    }

    @Test
    public void testUnregister() {
        logger.info("Starting testUnregister");
        messageBus.unregister(microService1);
        logger.info("Starting testUnregister2222");

        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(microService1, 1, TimeUnit.SECONDS));
    }

    @Test
    public void testAwaitMessage() {
        logger.info("Starting testAwaitMessage");
        messageBus.subscribeEvent(TestEvent.class, microService1);
        messageBus.sendEvent(new TestEvent());
        try {
            Message message = messageBus.awaitMessage(microService1, 1, TimeUnit.SECONDS);
            assertTrue(message instanceof TestEvent);
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        } catch (TimeoutException e) {
            fail("awaitMessage timed out");
        }
    }

    // TestEvent and TestBroadcast classes for testing purposes
    private static class TestEvent implements Event<String> {
    }

    private static class TestBroadcast implements Broadcast {
    }
}