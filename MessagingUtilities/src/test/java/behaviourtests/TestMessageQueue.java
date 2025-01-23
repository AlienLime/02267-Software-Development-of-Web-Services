package behaviourtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;

@Disabled("Only works when using RabbitMq")
public class TestMessageQueue extends TestUtilities {

	@Test
	public void testPublishSubscribe() {
		var q = new RabbitMQQueue();
		var done = new Object() {
			boolean value = false;
		};
		q.subscribe("event", e -> {
			done.value = true;
		});
		q.publish(new Event("event"));
		sleep(100);
		assertTrue(done.value);
	}

	@Test
	public void testHandlerExecutedTwice() {
		var q = new RabbitMQQueue();
		final var i = new Object() {
			public int value = 0;
		};
		q.subscribe("event", e -> {
			i.value++;
		});
		q.publish(new Event("event"));
		q.publish(new Event("event"));
		sleep(100);
		assertEquals(2, i.value);
	}

	@Test
	public void testPublishWithTwoHandlers() {
		var q = new RabbitMQQueue();
		var done1 = new Object() {
			boolean value = false;
		};
		var done2 = new Object() {
			boolean value = false;
		};
		q.subscribe("event", e -> {
			done1.value = true;
		});
		q.subscribe("event", e -> {
			done2.value = true;
		});
		q.publish(new Event("event"));
		sleep(100);
		assertTrue(done1.value);
		assertTrue(done2.value);
	}

	/*
	 * One handler completes a CompletableFuture waited for in another handler. That
	 * handler initiates the first handler by publishing an event.
	 */
	@Test
	public void testNoDeadlock() {
		var cf = new CompletableFuture<Boolean>();
		var done = new CompletableFuture<Boolean>();
		var q = new RabbitMQQueue();
		q.subscribe("one", e -> {
			cf.join();
			done.complete(true); // We have reached passed the blocking join.
		});
		q.subscribe("two", e -> {
			cf.complete(true);
		});
		q.publish(new Event("two"));
		q.publish(new Event("one"));
		sleep(100);
		assertTrue(done.join()); // Check that the handler for topic "one" terminated.
		assertTrue(cf.isDone()); // Check that the CompletableFuture is completed in the
		// handler for topic "two".
	}

	@Test
	public void testDeserializationOfListsInProcessQueue() throws InterruptedException, ExecutionException {
		var q = new RabbitMQQueue();
		bodyTestDeserializationOfLists(q);
	}

	/* Test deserialization using Gson and Java records.
	 * Gson v 2.8.6 cannot handle records as it requires
	 * setters for the fields, Gson v 2.11.0 can, because it can
	 * assign the arguments for the constructor based on the
	 * record type.
	 */
	@Test
	public void testGsonDeserializationWithRecords() throws InterruptedException, ExecutionException {
		var q = new RabbitMQQueue();
		bodyTestDeserializationGsonRecords(q);
	}

	protected void bodyTestDeserializationGsonRecords(MessageQueue q)
			throws InterruptedException, ExecutionException {
		CompletableFuture<Person> actual = new CompletableFuture<Person>();
		q.subscribe("person", e -> {
			actual.complete(e.getArgument("person", Person.class));
		});
		Person expected = new Person("some name", 321);
		q.publish(new Event("person", Map.of("person", expected)));
		assertEquals(expected,actual.orTimeout(1, TimeUnit.SECONDS).get());
	}
}
