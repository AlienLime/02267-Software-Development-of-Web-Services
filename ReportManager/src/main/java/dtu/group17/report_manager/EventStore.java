/*
 * Author: Emil Wraae Carlsen (s204458)
 * Description:
 * Store for events.
 * Events are stored in a simple list.
 */

package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventStore {
    private List<Event> events = Collections.synchronizedList(new ArrayList<>());

    public void addEvent(Event e) {
        events.add(e);
    }
}
