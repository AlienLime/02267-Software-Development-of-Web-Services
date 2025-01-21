package dtu.group17;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class PaymentManagerFacade {
    private static final Logger LOG = Logger.getLogger(PaymentManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<Void>> submitPaymentRequests = new HashMap<>();

    Runnable unsubscribePaymentCompleted, unsubscribePaymentMerchantNotFoundError, unsubscribePaymentBankError, unsubscribePaymentTokenNotFoundError;

    public PaymentManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribePaymentCompleted = queue.subscribe("PaymentCompleted", this::handleCompleted);
        unsubscribePaymentMerchantNotFoundError = queue.subscribe("PaymentMerchantNotFoundError", this::handlePaymentMerchantNotFoundError);
        unsubscribePaymentBankError = queue.subscribe("PaymentBankError", this::handlePaymentBankError);
        unsubscribePaymentTokenNotFoundError = queue.subscribe("PaymentTokenNotFoundError", this::handlePaymentTokenNotFoundError);
    }

    @PreDestroy // For testing, on hot reload we remove previous subscription
    public void cleanup() {
        unsubscribePaymentCompleted.run();
        unsubscribePaymentMerchantNotFoundError.run();
        unsubscribePaymentBankError.run();
        unsubscribePaymentTokenNotFoundError.run();
    }

    public boolean submitPayment(Payment payment) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        submitPaymentRequests.put(id, future);
        Event event = new Event("PaymentRequested", Map.of("id", id, "payment", payment));
        queue.publish(event);
        LOG.info("Sent PaymentRequested event");
        future.orTimeout(3, TimeUnit.SECONDS).join();
        return true;
    }

    public void handleCompleted(Event e) {
        LOG.info("Received PaymentCompleted event");
        submitPaymentRequests.remove(e.getArgument("id", UUID.class)).complete(null);
    }

    public void handlePaymentMerchantNotFoundError(Event e) {
        LOG.info("Received PaymentMerchantNotFoundError event");
        submitPaymentRequests.remove(e.getArgument("id", UUID.class)).completeExceptionally(new MerchantNotFoundException(e.getArgument("message", String.class)));
    }

    public void handlePaymentTokenNotFoundError(Event e) {
        LOG.info("Received PaymentTokenNotFoundError event");
        submitPaymentRequests.remove(e.getArgument("id", UUID.class)).completeExceptionally(new TokenNotFoundException(e.getArgument("message", String.class)));
    }

    public void handlePaymentBankError(Event e) {
        LOG.info("Received PaymentBankError event");
        submitPaymentRequests.remove(e.getArgument("id", UUID.class)).completeExceptionally(new BankException(e.getArgument("message", String.class)));
    }
}
