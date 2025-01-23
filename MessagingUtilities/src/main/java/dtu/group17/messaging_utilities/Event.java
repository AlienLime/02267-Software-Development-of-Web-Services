/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Event class for messaging utilities
 */

package dtu.group17.messaging_utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Event implements Serializable {
    private static final long serialVersionUID = 8761618126034081891L;
    private String topic; // Type
    private Map<String, Object> arguments;

    public Event() {}

    public Event(String topic, Map<String, Object> arguments) {
        this.topic = topic;
        this.arguments = arguments;
    }

    public Event(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    /**
     * Get argument by key and keeps type after serialization
     * @author Katja
     */
    public <T> T getArgument(String key, Class<T> cls) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(arguments.get(key));
        return gson.fromJson(jsonString, cls);
    }

    /**
     * Get argument by key and keeps type after serialization with TypeToken
     * @author Katja
     */
    public <T> T getArgument(String key, TypeToken<T> typeToken) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(arguments.get(key));
        return gson.fromJson(jsonString, typeToken);
    }


    /*
        * Overriden equals method to check if two events are equal
        * @author Katja
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(topic, event.topic) && Objects.equals(arguments, event.arguments);
    }

    /*
        * Overriden hashCode method to generate hash code for event object
        * @return hash code for event object based on topic and arguments
        * @author Katja
     */
    @Override
    public int hashCode() {
        return Objects.hash(topic, arguments);
    }

    /*
        * Overriden toString method to generate string representation of event object
        * @return string representation of event object with topic and arguments
        * @author Katja
     */
    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
