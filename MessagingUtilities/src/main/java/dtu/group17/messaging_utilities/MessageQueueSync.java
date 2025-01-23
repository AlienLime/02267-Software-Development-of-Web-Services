/*
 * Author: Katja Kaj (s123456)
 * Description:
 * The MessageQueueSync class implements the MessageQueue interface and provides a separate synchronous implementation of the message queue.
 */

package dtu.group17.messaging_utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MessageQueueSync implements MessageQueue {

	private Map<String, List<Consumer<Event>>> handlersByTopic = new HashMap<>();

	private void executeHandlers(Event event) {
		var handlers = handlersByTopic.getOrDefault(event.getTopic(), new ArrayList<Consumer<Event>>());
		handlers.stream().forEach(h -> h.accept(event));
	}

	@Override
	public void publish(Event event) {
		executeHandlers(event);	
	}

	@Override
	public Runnable subscribe(String topic, Consumer<Event> handler) {
		handlersByTopic.computeIfAbsent(topic, t -> new ArrayList<>()).add(handler);
		return () -> handlersByTopic.get(topic).remove(handler);
	}

}
