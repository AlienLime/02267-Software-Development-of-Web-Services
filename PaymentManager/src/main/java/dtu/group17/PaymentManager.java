package dtu.group17;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static dtu.group17.HandlerUtil.onErrorHandler;

public class PaymentManager {
    private static final Logger LOG = Logger.getLogger(PaymentManager.class);

    MessageQueue queue = new RabbitMQQueue();
    BankService bankService = new BankServiceService().getBankServicePort();

    private Map<UUID, CompletableFuture<UUID>> customerIdRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<String>> customerAccountIdRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<String>> merchantAccountIdRequests = new HashMap<>();

    public static void main(String[] args) {
        new PaymentManager();
    }

    public PaymentManager() {
        LOG.info("Starting Payment Manager...");

        queue.subscribe("PaymentRequested", this::onPaymentRequested);

        queue.subscribe("CustomerIdFromTokenAnswer", this::onCustomerIdFromTokenAnswer); //TODO: Event to past tense

        queue.subscribe("AccountIdFromCustomerIdAnswer", e -> //TODO: Event to past tense
                onAccountIdFromUserIdAnswer(customerAccountIdRequests, e)
        );
        queue.subscribe("AccountIdFromMerchantIdAnswer", e -> //TODO: Event to past tense
                onAccountIdFromUserIdAnswer(merchantAccountIdRequests, e)
        );

        queue.subscribe("AccountIdFromMerchantIdError", e -> //TODO: Event to past tense
                onErrorHandler(merchantAccountIdRequests, Exception::new, e)
        );
        queue.subscribe("CustomerIdFromTokenError", e -> //TODO: Event to past tense
                onErrorHandler(customerIdRequests, Exception::new, e)
        );
    }

    public void onPaymentRequested(Event e) {
        Payment payment = e.getArgument("payment", Payment.class);

        // Retrieve customer id from token manager
        // TODO: Refactor to maybe use rpc?
        // TODO: Reorder to minimize blocking
        CompletableFuture<UUID> customerIdRequest = new CompletableFuture<>();
        UUID customerIdCorrelationId = UUID.randomUUID();
        customerIdRequests.put(customerIdCorrelationId, customerIdRequest);
        Event customerIdRequestEvent = new Event("CustomerIdFromTokenRequest", Map.of("id", customerIdCorrelationId, "token", payment.token()));
        queue.publish(customerIdRequestEvent);
        UUID customerId = customerIdRequest.orTimeout(3, TimeUnit.SECONDS).exceptionally(ex -> {
            String errorMessage = ex.getMessage();
            LOG.error(errorMessage);
            Event event = new Event("PaymentTokenNotFoundError", Map.of("id", e.getArgument("id", UUID.class), "message", errorMessage));
            queue.publish(event);
            return null;
        }).join();
        if (customerId == null) {
            return;
        }

        // Retrieve account ids from account manager
        CompletableFuture<String> customerAccountIdRequest = new CompletableFuture<>();
        UUID customerAccountIdCorrelationId = UUID.randomUUID();
        customerAccountIdRequests.put(customerAccountIdCorrelationId, customerAccountIdRequest);
        Event customerAccountIdRequestEvent = new Event("AccountIdFromCustomerIdRequest", Map.of("id", customerAccountIdCorrelationId, "customerId", customerId));
        queue.publish(customerAccountIdRequestEvent);

        CompletableFuture<String> merchantAccountIdRequest = new CompletableFuture<>();
        UUID merchantAccountIdCorrelationId = UUID.randomUUID();
        merchantAccountIdRequests.put(merchantAccountIdCorrelationId, merchantAccountIdRequest);
        Event merchantAccountIdRequestEvent = new Event("AccountIdFromMerchantIdRequest", Map.of("id", merchantAccountIdCorrelationId, "merchantId", payment.merchantId()));
        queue.publish(merchantAccountIdRequestEvent);

        String customerAccountId = customerAccountIdRequest.orTimeout(3, TimeUnit.SECONDS).join();
        String merchantAccountId = merchantAccountIdRequest.orTimeout(3, TimeUnit.SECONDS).exceptionally(ex -> {
            String errorMessage = ex.getMessage();
            LOG.error(errorMessage);
            Event event = new Event("PaymentMerchantNotFoundError", Map.of("id", e.getArgument("id", UUID.class), "message", errorMessage));
            queue.publish(event);
            return null;
        }).join();
        if (merchantAccountId == null) {
            return;
        }

        try {
            String description = "Group 17 - transfer of " + payment.amount() + " kr. from " + customerId + " to " + payment.merchantId();
            bankService.transferMoneyFromTo(customerAccountId, merchantAccountId, BigDecimal.valueOf(payment.amount()), description);
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            LOG.error(errorMessage);
            Event event = new Event("PaymentBankError", Map.of("id", e.getArgument("id", UUID.class), "message", errorMessage));
            queue.publish(event);
            return;
        }

        Event event = new Event("PaymentCompleted", Map.of(
                "id", e.getArgument("id", UUID.class),
                "payment", payment,
                "customerId", customerId));
        queue.publish(event);
    }

    public void onCustomerIdFromTokenAnswer(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        UUID customerId = e.getArgument("customerId", UUID.class);
        customerIdRequests.remove(eventId).complete(customerId);
    }

    public void onAccountIdFromUserIdAnswer(Map<UUID, CompletableFuture<String>> accountIdRequests, Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        String accountId = e.getArgument("accountId", String.class);
        accountIdRequests.remove(eventId).complete(accountId);
    }

}
