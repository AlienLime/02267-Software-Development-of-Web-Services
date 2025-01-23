package behaviourtests;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Only works when using RabbitMq")
public class TestMessageQueueNeedingRabbitMq extends TestUtilities {

	@Test
	public void testTopicMatching() {
		var q = new RabbitMQQueue();
		var s = new HashSet<String>();
		q.subscribe("one.*", e -> {
			s.add(e.getTopic());
		});
		q.publish(new Event("one.one"));
		q.publish(new Event("one.two"));
		sleep(100);
		var expected = new HashSet<String>();
		expected.add("one.one");
		expected.add("one.two");
		assertEquals(expected, s);
	}

	@Test
	public void testDeserializationOfLists() throws InterruptedException, ExecutionException {
		var q = new RabbitMQQueue();
		bodyTestDeserializationOfLists(q);
	}

	@Test
	public void testGsonDeserializationWithRecordsRabbitMq() throws InterruptedException, ExecutionException {
		var q = new RabbitMQQueue();
		bodyTestDeserializationGsonRecords(q);
	}

}
