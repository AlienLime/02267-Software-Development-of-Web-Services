package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;

import java.util.ArrayList;
import java.util.List;

public class EventStore {
    private List<Event> events = new ArrayList<>();

    public void addEvent(Event e) {
        events.add(e);
    }
}
