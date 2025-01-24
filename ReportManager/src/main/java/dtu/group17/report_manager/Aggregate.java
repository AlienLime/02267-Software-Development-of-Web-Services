package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;

import java.util.Map;
import java.util.UUID;

public class Aggregate {
    private MessageQueue queue;
    private EventStore eventStore;

    public Aggregate(MessageQueue queue, EventStore eventStore) {
        this.queue = queue;
        this.eventStore = eventStore;

        queue.subscribe("ClearRequested", this::handleClearCommand);
    }

    public void handleClearCommand(Event event) {
        eventStore.addEvent(event);
        // In a real application this would probably clear a database or something similar
        Event response = new Event("ReportsCleared", Map.of("id", event.getArgument("id", UUID.class)));
        queue.publish(response);
    }
}
