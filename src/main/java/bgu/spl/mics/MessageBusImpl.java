package bgu.spl.mics;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for
 * unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static final MessageBusImpl instance = new MessageBusImpl();

	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQueues;
	private final ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubscriptions;
	private final ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubscriptions;
	private final ConcurrentHashMap<Event, Future> eventFutures;
	private final ConcurrentHashMap<Class<? extends Event>, AtomicInteger> roundRobinCounters;

	private MessageBusImpl() {
		microServiceQueues = new ConcurrentHashMap<>();
		eventSubscriptions = new ConcurrentHashMap<>();
		broadcastSubscriptions = new ConcurrentHashMap<>();
		eventFutures = new ConcurrentHashMap<>();
		roundRobinCounters = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscriptions.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		eventSubscriptions.get(type).add(m);
		roundRobinCounters.putIfAbsent(type, new AtomicInteger(0));
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscriptions.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		broadcastSubscriptions.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = eventFutures.remove(e);
		if (future != null) {
			future.resolve(result);
		}

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Class<? extends Broadcast> type = b.getClass();
		ConcurrentLinkedQueue<MicroService> subscribers = broadcastSubscriptions.get(type);
		if (subscribers != null) {
			for (MicroService m : subscribers) {
				microServiceQueues.get(m).add(b);
			}
		}

	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class<? extends Event> type = e.getClass();
		ConcurrentLinkedQueue<MicroService> subscribers = eventSubscriptions.get(type);
		if (subscribers != null && !subscribers.isEmpty()) {
			int index = roundRobinCounters.get(type).getAndIncrement() % subscribers.size();
			MicroService[] subscriberArray = subscribers.toArray(new MicroService[0]);
			MicroService m = subscriberArray[index];
			microServiceQueues.get(m).add(e);
			Future<T> future = new Future<>();
			eventFutures.put(e, future);
			return future;
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		microServiceQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		microServiceQueues.remove(m);
		eventSubscriptions.values().forEach((queue) -> queue.remove(m));
		broadcastSubscriptions.values().forEach((queue) -> queue.remove(m));
		eventFutures.entrySet().removeIf(entry -> entry.getValue().get() == m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = microServiceQueues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService not registered");
		}
		return queue.take();
	}

}
