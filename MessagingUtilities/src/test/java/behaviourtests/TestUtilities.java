package behaviourtests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;
import dtu.group17.Event;
import dtu.group17.MessageQueue;

public class TestUtilities {

	protected void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
		}
	}

	protected void bodyTestDeserializationGsonRecords(MessageQueue q) throws InterruptedException, ExecutionException {
		CompletableFuture<Person> actual = new CompletableFuture<Person>();
		q.subscribe("person", e -> {
			actual.complete(e.getArgument("0", Person.class));
		});
		Person expected = new Person("some name", 321);
		q.publish(new Event("person", Map.of("0", expected)));
		assertEquals(expected, actual.orTimeout(1, TimeUnit.SECONDS).get());
	}

	protected void bodyTestDeserializationOfLists(MessageQueue q) throws InterruptedException, ExecutionException {
		CompletableFuture<List<String>> actual = new CompletableFuture<List<String>>();
		q.subscribe("list", e -> {
			actual.complete(e.getArgument("0", new TypeToken<List<String>>(){}));
		});
		List<String> expected = new ArrayList<>();
		expected.add("1");
		expected.add("2");
		q.publish(new Event("list", Map.of("0", expected)));
		actual.join();
		assertEquals(expected, actual.get());
	}
}
