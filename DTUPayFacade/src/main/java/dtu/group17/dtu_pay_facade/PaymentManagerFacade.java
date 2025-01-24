/*
 * Author: Katja Kaj (s123456)
 * Description:
 * This file contains the PaymentManagerFacade class, which is a facade for the payment manager and thus contains no business logic.
 * It is responsible for handling the communication with the payment manager and the messaging system.
 */

package dtu.group17.dtu_pay_facade;

import dtu.group17.dtu_pay_facade.exceptions.CustomerNotFoundException;
import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import dtu.group17.dtu_pay_facade.exceptions.BankException;
import dtu.group17.dtu_pay_facade.exceptions.MerchantNotFoundException;
import dtu.group17.dtu_pay_facade.exceptions.TokenNotFoundException;
import dtu.group17.dtu_pay_facade.records.Payment;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static dtu.group17.dtu_pay_facade.HandlerUtil.completedHandler;
import static dtu.group17.dtu_pay_facade.HandlerUtil.errorHandler;

@Singleton
public class PaymentManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<Void>> submitPaymentRequests = new ConcurrentHashMap<>();

    private Runnable unsubscribePaymentCompleted, unsubscribePaymentCustomerNotFoundError,
            unsubscribePaymentMerchantNotFoundError, unsubscribePaymentBankError,
            unsubscribePaymentTokenNotFoundError;

    public PaymentManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribePaymentCompleted = queue.subscribe("PaymentCompleted", e ->
                completedHandler(submitPaymentRequests, e)
        );
        unsubscribePaymentCustomerNotFoundError = queue.subscribe("RetrieveCustomerBankAccountFailed", e ->
                errorHandler(submitPaymentRequests, CustomerNotFoundException::new, e)
        );
        unsubscribePaymentMerchantNotFoundError = queue.subscribe("RetrieveMerchantBankAccountFailed", e ->
                errorHandler(submitPaymentRequests, MerchantNotFoundException::new, e)
        );
        unsubscribePaymentBankError = queue.subscribe("PaymentFailed", e ->
                errorHandler(submitPaymentRequests, BankException::new, e)
        );
        unsubscribePaymentTokenNotFoundError = queue.subscribe("TokenValidationFailed", e ->
                errorHandler(submitPaymentRequests, TokenNotFoundException::new, e)
        );
    }

    /**
     * For testing, on hot reload we remove the previous subscription
     * @author Katja
     */
    @PreDestroy
    public void cleanup() {
        unsubscribePaymentCompleted.run();
        unsubscribePaymentCustomerNotFoundError.run();
        unsubscribePaymentMerchantNotFoundError.run();
        unsubscribePaymentBankError.run();
        unsubscribePaymentTokenNotFoundError.run();
    }

    /**
     * Publishes a PaymentRequested event to the message queue
     * @param payment The payment to be submitted
     * @return true if the payment was successfully submitted
     * @throws MerchantNotFoundException if the merchant was not found
     * @throws TokenNotFoundException if the token was not found
     * @author Katja
     */
    public boolean submitPayment(UUID merchantId, Payment payment) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        submitPaymentRequests.put(id, future);
        Event event = new Event("PaymentRequested", Map.of(
                "id", id,
                "token", payment.token(),
                "amount", payment.amount(),
                "merchantId", merchantId));
        queue.publish(event);
        future.join();
        return true;
    }

}
