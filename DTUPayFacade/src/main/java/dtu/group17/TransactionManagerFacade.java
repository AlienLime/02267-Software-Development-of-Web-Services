package dtu.group17;

import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TransactionManagerFacade {
    private static final Logger LOG = Logger.getLogger(AccountManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<Void>> submitPaymentRequests = new HashMap<>();

    public TransactionManagerFacade(MessageQueue queue) throws IOException {
        this.queue = queue;
        queue.subscribe("PaymentCompleted", this::handleCompleted);
    }

    public void submitPayment(Payment payment) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        submitPaymentRequests.put(id, future);
        Event event = new Event("PaymentRequested", Map.of("id", id, "payment", payment));
        queue.publish(event);
        LOG.info("Sent PaymentRequested event");
        future.join();
    }

    public void handleCompleted(Event e) {
        LOG.info("Received PaymentCompleted event");
        submitPaymentRequests.remove(e.getArgument("id", UUID.class)).complete(null);
    }
}
