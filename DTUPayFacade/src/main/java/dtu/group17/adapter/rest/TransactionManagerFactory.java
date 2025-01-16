package dtu.group17.adapter.rest;

import dtu.group17.MessageQueue;
import dtu.group17.RabbitMQQueue;
import dtu.group17.TransactionManagerFacade;

import java.io.IOException;

public class TransactionManagerFactory {
    static TransactionManagerFacade facade = null;

    public synchronized TransactionManagerFacade getFacade() throws IOException {
        if (facade != null) {
            return facade;
        }

        MessageQueue queue = new RabbitMQQueue();
        return new TransactionManagerFacade(queue);
    }
}
