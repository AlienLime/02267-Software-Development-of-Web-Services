package dtu.group17;

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

    public <T> T getArgument(String key, Class<T> cls) { // To keep type after serialization
        Gson gson = new Gson();
        String jsonString = gson.toJson(arguments.get(key));
        return gson.fromJson(jsonString, cls);
    }

    public <T> T getArgument(String key, TypeToken<T> typeToken) { // To keep type after serialization
        Gson gson = new Gson();
        String jsonString = gson.toJson(arguments.get(key));
        return gson.fromJson(jsonString, typeToken);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(topic, event.topic) && Objects.equals(arguments, event.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, arguments);
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
