package dtu.group17;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static dtu.group17.HandlerUtil.onErrorHandler;

public class PaymentManager {
    private static final Logger LOG = Logger.getLogger(PaymentManager.class);

    MessageQueue queue = new RabbitMQQueue();
    BankService bankService = new BankServiceService().getBankServicePort();

    private ConcurrentHashMap<UUID, PaymentData> paymentDatas = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new PaymentManager();
    }

    public PaymentManager() {
        LOG.info("Starting Payment Manager...");

        queue.subscribe("PaymentRequested", this::onPaymentRequested);
        queue.subscribe("CustomerBankAccountRetrieved", this::onCustomerAccountIdRetrieved);
        queue.subscribe("MerchantBankAccountRetrieved", this::onMerchantAccountIdRetrieved);

        queue.subscribe("TokenValidationFailed", e ->
            paymentDatas.remove(e.getArgument("id", UUID.class))
        );
        queue.subscribe("RetrieveMerchantBankAccountFailed", e ->
            paymentDatas.remove(e.getArgument("id", UUID.class))
        );
    }

    private void onPaymentRequested(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        Token token = e.getArgument("token", Token.class);
        int amount = e.getArgument("amount", Integer.class);
        UUID merchantId = e.getArgument("merchantId", UUID.class);

        paymentDatas.compute(eventId, (id, data) -> {
            if (data == null) {
                data = new PaymentData(eventId);
            }

            data.setToken(Optional.of(token));
            data.setAmount(Optional.of(amount));
            data.setMerchantId(Optional.of(merchantId));

            if (data.isComplete()) {
                processPayment(data);
                data = null;
            }

            return data;
        });
    }

    public void onCustomerAccountIdRetrieved(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        UUID customerId = e.getArgument("customerId", UUID.class);
        String accountId = e.getArgument("accountId", String.class);

        paymentDatas.compute(eventId, (id, data) -> {
            if (data == null) {
                data = new PaymentData(eventId);
            }

            data.setCustomerId(Optional.of(customerId));
            data.setCustomerAccountId(Optional.of(accountId));

            if (data.isComplete()) {
                processPayment(data);
                data = null;
            }

            return data;
        });
    }

    public void onMerchantAccountIdRetrieved(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        String accountId = e.getArgument("accountId", String.class);
        paymentDatas.compute(eventId, (id, data) -> {
            if (data == null) {
                data = new PaymentData(eventId);
            }

            data.setMerchantAccountId(Optional.of(accountId));

            if (data.isComplete()) {
                processPayment(data);
                data = null;
            }

            return data;
        });
    }

    public void processPayment(PaymentData data) {
        try {
            String description = "Group 17 - transfer of " + data.getAmount().get() + " kr. from " + data.getCustomerId().get() + " to " + data.getMerchantId().get();
            bankService.transferMoneyFromTo(data.getCustomerAccountId().get(), data.getMerchantAccountId().get(), BigDecimal.valueOf(data.getAmount().get()), description);
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            LOG.error(errorMessage);
            Event event = new Event("PaymentFailed", Map.of("id", data.getId(), "message", errorMessage));
            queue.publish(event);
            return;
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", data.getId());
        eventData.put("amount", data.getAmount().get());
        eventData.put("token", data.getToken().get());
        eventData.put("customerId", data.getCustomerId().get());
        eventData.put("merchantId", data.getMerchantId().get());
        eventData.put("customerAccountId", data.getCustomerAccountId().get());
        eventData.put("merchantAccountId", data.getMerchantAccountId().get());
        Event event = new Event("PaymentCompleted", eventData);
        queue.publish(event);
    }
}
