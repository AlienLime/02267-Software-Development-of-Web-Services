package dtu.group17.adapter.rest;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import dtu.group17.AccountManagerFacade;
import dtu.group17.MessageQueue;
import dtu.group17.RabbitMQQueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AccountManagerFactory {
	static AccountManagerFacade facade = null;

	public synchronized AccountManagerFacade getFacade() throws IOException, TimeoutException {
		if (facade != null) {
			return facade;
		}

		MessageQueue queue = new RabbitMQQueue();
		return new AccountManagerFacade(queue);
	}
}
