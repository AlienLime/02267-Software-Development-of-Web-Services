package dtu.group17;

import dtu.group17.exceptions.BankException;
import dtu.group17.exceptions.MerchantNotFoundException;
import dtu.group17.exceptions.TokenNotFoundException;
import dtu.group17.records.Payment;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static dtu.group17.HandlerUtil.onErrorHandler;

@Singleton
public class PaymentManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<Void>> submitPaymentRequests = new HashMap<>();

    private Runnable unsubscribePaymentCompleted, unsubscribePaymentMerchantNotFoundError,
            unsubscribePaymentBankError, unsubscribePaymentTokenNotFoundError; //TODO: Rename events to past tense (also methods?)

    public PaymentManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribePaymentCompleted = queue.subscribe("PaymentCompleted", this::handleCompleted);
        unsubscribePaymentMerchantNotFoundError = queue.subscribe("PaymentMerchantNotFoundError", e ->
                onErrorHandler(submitPaymentRequests, MerchantNotFoundException::new, e)
        );
        unsubscribePaymentBankError = queue.subscribe("PaymentBankError", e ->
                onErrorHandler(submitPaymentRequests, TokenNotFoundException::new, e)
        );
        unsubscribePaymentTokenNotFoundError = queue.subscribe("PaymentTokenNotFoundError", e ->
                onErrorHandler(submitPaymentRequests, BankException::new, e)
        );
    }

    @PreDestroy // For testing, on hot reload we remove the previous subscription
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
        future.join();
        return true;
    }

    public void handleCompleted(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        submitPaymentRequests.remove(eventId).complete(null);
    }

}
