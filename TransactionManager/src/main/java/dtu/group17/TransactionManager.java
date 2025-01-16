package dtu.group17;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TransactionManager {
    private static final Logger LOG = Logger.getLogger(TransactionManager.class);

    MessageQueue queue = new RabbitMQQueue();
    BankService bankService = new BankServiceService().getBankServicePort();

    private Map<UUID, CompletableFuture<String>> customerIdRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<String>> customerAccountIdRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<String>> merchantAccountIdRequests = new HashMap<>();

    public static void main(String[] args) {
        new TransactionManager();
    }

    public TransactionManager() {
        LOG.info("Starting Transaction Manager...");
        queue.subscribe("PaymentRequested", this::onPaymentRequested);
        queue.subscribe("CustomerIdFromTokenAnswer", this::onCustomerIdFromTokenAnswer);
        queue.subscribe("AccountIdFromCustomerIdRequest", this::onCustomerAccountIdFromCustomerIdAnswer);
        queue.subscribe("AccountIdFromMerchantIdRequest", this::onMerchantAccountIdFromMerchantIdAnswer);
    }

    public void onPaymentRequested(Event e) {
        LOG.info("Received PaymentRequested event");
        Payment payment = e.getArgument("payment", Payment.class);

        // Retrieve customer id from token manager
        // TODO: Refactor to maybe use rpc?
        // TODO: Reorder to minimize blocking
        CompletableFuture<String> customerIdRequest = new CompletableFuture<>();
        UUID customerIdCorrelationId = UUID.randomUUID();
        customerIdRequests.put(customerIdCorrelationId, customerIdRequest);
        Event customerIdRequestEvent = new Event("CustomerIdFromTokenRequest", Map.of("id", customerIdCorrelationId, "token", payment.token()));
        queue.publish(customerIdRequestEvent);
        String customerId = customerIdRequest.join();

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
        
        String customerAccountId = customerAccountIdRequest.join();
        String merchantAccountId = merchantAccountIdRequest.join();

        try {
            String description = "Group 17 - transfer of " + payment.amount() + " kr. from " + customerId + " to " + payment.merchantId();
            bankService.transferMoneyFromTo(customerAccountId, merchantAccountId, BigDecimal.valueOf(payment.amount()), description);
        } catch (Exception ex) {
            throw new Error(ex);
        }

        Event event = new Event("PaymentSubmitted", Map.of("id", e.getArgument("id", UUID.class)));
         queue.publish(event);
         LOG.info("Sent PaymentSubmitted event");
    }

    public void onCustomerIdFromTokenAnswer(Event e) {
        customerIdRequests.remove(e.getArgument("id", UUID.class)).complete(e.getArgument("customerId", String.class));
    }

    public void onCustomerAccountIdFromCustomerIdAnswer(Event e) {
        customerAccountIdRequests.remove(e.getArgument("id", UUID.class)).complete(e.getArgument("accountId", String.class));
    }

    public void onMerchantAccountIdFromMerchantIdAnswer(Event e) {
        merchantAccountIdRequests.remove(e.getArgument("id", UUID.class)).complete(e.getArgument("accountId", String.class));
    }
}
