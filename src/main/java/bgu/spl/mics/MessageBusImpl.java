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
	private static class MessageBusImplHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQueues;
	private final ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubscriptions;
	private final ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubscriptions;
	private final ConcurrentHashMap<Event, Future> eventFutures;
	private final ConcurrentHashMap<Future, MicroService> futureServiceMap;
	private final ConcurrentHashMap<Class<? extends Event>, AtomicInteger> roundRobinCounters;

	private MessageBusImpl() {
		microServiceQueues = new ConcurrentHashMap<>();
		eventSubscriptions = new ConcurrentHashMap<>();
		broadcastSubscriptions = new ConcurrentHashMap<>();
		eventFutures = new ConcurrentHashMap<>();
		roundRobinCounters = new ConcurrentHashMap<>();
		futureServiceMap = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}
	    /**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: type != null && m != null
     *
     * @POST: eventSubscriptions.get(type).contains(m)
     *        && roundRobinCounters.containsKey(type)
     */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscriptions.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		eventSubscriptions.get(type).add(m);
		roundRobinCounters.putIfAbsent(type, new AtomicInteger(0));
	}

	/**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: type != null && m != null
     *
     * @POST: broadcastSubscriptions.get(type).contains(m)
     */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscriptions.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		broadcastSubscriptions.get(type).add(m);
	}

	/**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: e != null && result != null
     *
     * @POST: !eventFutures.containsKey(e)
     *        && !futureServiceMap.containsKey(future)
     */
	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = eventFutures.remove(e);
		futureServiceMap.remove(future);
		if (future != null) {
			future.resolve(result);
		}

	}
	/**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: b != null
     *
     * @POST: for each MicroService m in broadcastSubscriptions.get(b.getClass()):
     *        microServiceQueues.get(m).contains(b)
     */
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
	/**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: e != null
     *
     * @POST: if subscribers != null && !subscribers.isEmpty():
     *        eventFutures.containsKey(e)
     *        && futureServiceMap.containsKey(future)
     *        && microServiceQueues.get(m).contains(e)
     */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class<? extends Event> type = e.getClass();
		ConcurrentLinkedQueue<MicroService> subscribers = eventSubscriptions.get(type);
		if (subscribers != null && !subscribers.isEmpty()) {
			int index = roundRobinCounters.get(type).getAndIncrement() % subscribers.size();
			MicroService[] subscriberArray = subscribers.toArray(new MicroService[0]);
			MicroService m = subscriberArray[index];
			Future<T> future = new Future<>();
			eventFutures.put(e, future);
			futureServiceMap.put(future, m);
			microServiceQueues.get(m).add(e);
			return future;
		}
		return null;
	}

	/**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: m != null
     *
     * @POST: microServiceQueues.containsKey(m)
     */
	@Override
	public void register(MicroService m) {
		microServiceQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	/**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: m != null
     *
     * @POST: !microServiceQueues.containsKey(m)
     *        && for each queue in eventSubscriptions.values():
     *           !queue.contains(m)
     *        && for each queue in broadcastSubscriptions.values():
     *           !queue.contains(m)
     *        && for each entry in eventFutures.entrySet():
     *           futureServiceMap.get(entry.getValue()) != m
     */
	@Override
	public synchronized void unregister(MicroService m) {

		// Remove the microservice's queue
		microServiceQueues.remove(m);
		eventSubscriptions.values().forEach((queue) -> queue.remove(m));
		broadcastSubscriptions.values().forEach((queue) -> queue.remove(m));
		eventFutures.entrySet().removeIf(entry -> futureServiceMap.get(entry.getValue()) == m);
	}

	
	 /**
     * @inv: eventSubscriptions != null
     *       && broadcastSubscriptions != null
     *       && microServiceQueues != null
     *       && eventFutures != null
     *       && futureServiceMap != null
     *       && roundRobinCounters != null
     *
     * @PRE: m != null && timeout > 0 && unit != null
     *
     * @POST: returns a Message from the queue of m or throws TimeoutException
     */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = microServiceQueues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService not registered");
		}
		return queue.take();
	}

	public Message awaitMessage(MicroService m, long timeout, TimeUnit unit)
			throws InterruptedException, TimeoutException {
		BlockingQueue<Message> queue = microServiceQueues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService not registered");
		}
		Message message = queue.poll(timeout, unit);
		if (message == null) {
			throw new TimeoutException("Timeout while waiting for message");
		}
		return message;
	}

}
