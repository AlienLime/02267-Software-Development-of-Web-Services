package dtu.group17;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitMQQueue implements MessageQueue {
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final String EXCHANGE_NAME = "eventsExchange";
    private static final String EXCHANGE_TYPE = "topic";

    private Channel channel;
    private String hostname;

    public RabbitMQQueue() {
        this(System.getenv().getOrDefault("RABBITMQ_HOSTNAME", DEFAULT_HOSTNAME));
    }

    public RabbitMQQueue(String hostname) {
        this.hostname = hostname;
        channel = createChannel();
    }

    private Channel createChannel() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
            return channel;
        } catch (IOException | TimeoutException e) {
            throw new Error(e);
        }
    }

    @Override
    public void publish(Event event) {
        System.out.format("[x] publish(%s)\n", event);
        String message = new Gson().toJson(event);
        try {
            channel.basicPublish(EXCHANGE_NAME, event.getTopic(), null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    public Runnable subscribe(String topic, Consumer<Event> handler) {
        System.out.format("[x] subscribe(%s)\n", topic);
        Channel channel = createChannel();
        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, topic);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                Event event = new Gson().fromJson(message, Event.class);
                System.out.format("[x] executingHandler(%s)\n", event);
                handler.accept(event);
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            return () -> {
                try {
                    channel.queueUnbind(queueName, EXCHANGE_NAME, topic);
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    throw new Error(e);
                }
            };
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}
