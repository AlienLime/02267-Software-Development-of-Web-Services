package dtu.group17;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Singleton
public class TransactionManagerFacade {
    private static final Logger LOG = Logger.getLogger(AccountManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<Void>> submitPaymentRequests = new HashMap<>();

    Runnable unsubscribePaymentCompleted;

    public TransactionManagerFacade() throws IOException {
        queue = new RabbitMQQueue();
        unsubscribePaymentCompleted = queue.subscribe("PaymentCompleted", this::handleCompleted);
    }

    @PreDestroy // For testing, on hot reload we remove previous subscription
    public void cleanup() {
        unsubscribePaymentCompleted.run();
    }

    public boolean submitPayment(Payment payment) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        submitPaymentRequests.put(id, future);
        Event event = new Event("PaymentRequested", Map.of("id", id, "payment", payment));
        queue.publish(event);
        LOG.info("Sent PaymentRequested event");
        future.join();
        return true;
    }

    public void handleCompleted(Event e) {
        LOG.info("Received PaymentCompleted event");
        submitPaymentRequests.remove(e.getArgument("id", UUID.class)).complete(null);
    }
}
