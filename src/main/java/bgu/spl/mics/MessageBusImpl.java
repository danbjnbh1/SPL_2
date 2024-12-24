package bgu.spl.mics;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = new MessageBusImpl();
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQueues;
    private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubscribers;
    private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubscribers;
    private ConcurrentHashMap<Event<?>, Future<?>> eventFutures;
	private final ConcurrentHashMap<Class<? extends Event>, AtomicInteger> roundRobinCounters;

	private MessageBusImpl() {
        microServiceQueues = new ConcurrentHashMap<>();
        eventSubscribers = new ConcurrentHashMap<>();
        broadcastSubscribers = new ConcurrentHashMap<>();
        eventFutures = new ConcurrentHashMap<>();
        roundRobinCounters = new ConcurrentHashMap<>();
    }

    public static synchronized MessageBusImpl getInstance() {
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
   		eventSubscribers.putIfAbsent(type, new ConcurrentLinkedQueue<>());
    	eventSubscribers.get(type).add(m);
		roundRobinCounters.putIfAbsent(type, new AtomicInteger(0));
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.putIfAbsent(type, new ConcurrentLinkedQueue<>());
    	broadcastSubscribers.get(type).add(m);
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
		ConcurrentLinkedQueue<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
		if (subscribers != null) {
			for (MicroService subscriber : subscribers) {
				BlockingQueue<Message> queue = microServiceQueues.get(subscriber);
				if (queue != null) {
					queue.add(b);
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> subscribers = eventSubscribers.get(e.getClass());
    	if (subscribers != null && !subscribers.isEmpty()) {
			MicroService m;
			synchronized (subscribers) {
				m = subscribers.poll();
				if (m != null) {
					subscribers.add(m);
				}
        	}
        if (m != null) {
            BlockingQueue<Message> queue = microServiceQueues.get(m);
            if (queue != null) {
                Future<T> future = new Future<>();
                eventFutures.put(e, future);
                queue.add(e);
                return future;
            }
        }
    }
		return null;
	}

	@Override
	public void register(MicroService m) {
    	microServiceQueues.putIfAbsent(m, new LinkedBlockingQueue<>());

	}

	@Override
	public void unregister(MicroService m) {
		// Remove the microservice's message queue
		microServiceQueues.remove(m);

		// Remove the microservice from all event subscribers
		eventSubscribers.values().forEach(queue -> queue.remove(m));

		// Remove the microservice from all broadcast subscribers
		broadcastSubscribers.values().forEach(list -> list.remove(m));

		// Remove any futures associated with events sent by this microservice
		eventFutures.entrySet().removeIf(entry -> entry.getValue() && entry.getValue().get() == m);		
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
