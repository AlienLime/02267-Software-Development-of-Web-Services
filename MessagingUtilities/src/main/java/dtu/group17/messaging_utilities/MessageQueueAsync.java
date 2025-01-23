/*
 * Author: Katja Kaj (s123456)
 * Description:
 * The MessageQueueAsync class is an implementation of the MessageQueue interface that provides asynchronous message handling.
 * It uses a separate thread to handle the messages, which allows the main thread to continue executing without waiting for the message handlers to finish.
 */

package dtu.group17.messaging_utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class MessageQueueAsync implements MessageQueue {

	private Map<String, TopicQueue> queuesByTopic = new ConcurrentHashMap<>();
	
	public MessageQueueAsync() {
		System.out.println("Asynch Message Queue Created");
	}

	@Override
	public void publish(Event event) {
		System.out.format("[x] publish(%s)\n",event);
		try {
			getQueue(event.getTopic()).publish(event);
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	@Override
	public Runnable subscribe(String topic, Consumer<Event> handler) {
		System.out.format("[x] addHandler(%s)\n",topic);
		return getQueue(topic).addHandler(handler);
	}

	private TopicQueue getQueue(String topic) {
		if (!queuesByTopic.containsKey(topic)) {
			queuesByTopic.put(topic, new TopicQueue());
		}
		return queuesByTopic.get(topic);
	}


}

class TopicQueue {
	private final List<Consumer<Event>> handlers = new ArrayList<>();
	private final BlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
	private Thread notificationThread = null;

	TopicQueue() {
		notificationThread = new Thread(() -> {
			executeHandlers();
		});
		notificationThread.start();
	}

	public synchronized Runnable addHandler(Consumer<Event> s) {
		handlers.add(s);
		return () -> handlers.remove(s);
	}

	private void executeHandlers() {
		while (true) {
			Event event;
			try {
				event = queue.take();
				System.out.println("Processing event "+event);
			} catch (InterruptedException e) {
				throw new Error(e);
			}
			new ArrayList<Consumer<Event>>(handlers).stream().forEach(handler -> handler.accept(event));
		}
	}

	public void publish(Event m) throws InterruptedException {
		queue.put(m);
	}
}
