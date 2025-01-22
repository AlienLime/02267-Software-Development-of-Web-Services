package dtu.group17;

import java.util.function.Consumer;

public interface MessageQueue {

    void publish(Event event);

    Runnable subscribe(String topic, Consumer<Event> handler);

}
