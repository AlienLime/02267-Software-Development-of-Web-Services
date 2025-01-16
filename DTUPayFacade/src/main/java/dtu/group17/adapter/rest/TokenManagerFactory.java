package dtu.group17.adapter.rest;

import dtu.group17.MessageQueue;
import dtu.group17.RabbitMQQueue;
import dtu.group17.TokenManagerFacade;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TokenManagerFactory {
    static TokenManagerFacade facade = null;

    public synchronized TokenManagerFacade getFacade() throws IOException {
        if (facade != null) {
            return facade;
        }

        MessageQueue queue = new RabbitMQQueue();
        return new TokenManagerFacade(queue);
    }
}
