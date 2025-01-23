/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Implements the MessageQueue interface, which is used to publish and subscribe to events.
 */

package dtu.group17.messaging_utilities;

import java.util.function.Consumer;

public interface MessageQueue {

    void publish(Event event);

    Runnable subscribe(String topic, Consumer<Event> handler);

}
