/*
 * Author: Emil Wraae Carlsen (s204458)
 * Description:
 * Aggregate of the event sourcing pattern for report generation.
 */

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

    /**
     * Handles the ClearRequested command.
     * Adds the event to the event store and publishes a ReportsCleared event.
     * @param event The ClearRequested event.
     * @author Emil Wraae Carlsen (s204458)
     */
    public void handleClearCommand(Event event) {
        eventStore.addEvent(event);
        // In a real application this would probably clear a database or something similar
        Event response = new Event("ReportsCleared", Map.of("id", event.getArgument("id", UUID.class)));
        queue.publish(response);
    }
}
