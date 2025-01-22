package dtu.group17;

import dtu.group17.exceptions.BankException;
import dtu.group17.exceptions.MerchantNotFoundException;
import dtu.group17.exceptions.TokenNotFoundException;
import dtu.group17.records.Payment;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static dtu.group17.HandlerUtil.completedHandler;
import static dtu.group17.HandlerUtil.errorHandler;

@Singleton
public class PaymentManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<Void>> submitPaymentRequests = new ConcurrentHashMap<>();

    private Runnable unsubscribePaymentCompleted, unsubscribePaymentMerchantNotFoundError,
            unsubscribePaymentBankError, unsubscribePaymentTokenNotFoundError; //TODO: Rename events to past tense (also methods?)

    public PaymentManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribePaymentCompleted = queue.subscribe("PaymentCompleted", e ->
                completedHandler(submitPaymentRequests, e)
        );
        unsubscribePaymentMerchantNotFoundError = queue.subscribe("RetrieveMerchantBankAccountFailed", e ->
                errorHandler(submitPaymentRequests, MerchantNotFoundException::new, e)
        );
        unsubscribePaymentBankError = queue.subscribe("PaymentFailed", e ->
                errorHandler(submitPaymentRequests, TokenNotFoundException::new, e)
        );
        unsubscribePaymentTokenNotFoundError = queue.subscribe("TokenValidationFailed", e ->
                errorHandler(submitPaymentRequests, BankException::new, e)
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
        UUID id = CorrelationId.randomCorrelationId();
        submitPaymentRequests.put(id, future);
        Event event = new Event("PaymentRequested", Map.of(
                "id", id,
                "token", payment.token(),
                "amount", payment.amount(),
                "merchantId", payment.merchantId()));
        queue.publish(event);
        future.join();
        return true;
    }

}
