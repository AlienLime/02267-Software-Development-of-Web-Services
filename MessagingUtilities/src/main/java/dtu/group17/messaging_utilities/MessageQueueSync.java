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
		if (!handlersByTopic.containsKey(topic)) {
			handlersByTopic.put(topic, new ArrayList<Consumer<Event>>());
		}
		handlersByTopic.get(topic).add(handler);
		return () -> handlersByTopic.get(topic).remove(handler);
	}
}
