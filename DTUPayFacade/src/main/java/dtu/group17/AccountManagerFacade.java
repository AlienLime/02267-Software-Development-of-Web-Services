package dtu.group17;

import dtu.group17.exceptions.CustomerNotFoundException;
import dtu.group17.exceptions.MerchantNotFoundException;
import dtu.group17.records.Customer;
import dtu.group17.records.Merchant;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static dtu.group17.HandlerUtil.completedHandler;
import static dtu.group17.HandlerUtil.errorHandler;

@Singleton
public class AccountManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<Customer>> registeredCustomers = new ConcurrentHashMap<>();
    private Map<UUID, CompletableFuture<Merchant>> registeredMerchants = new ConcurrentHashMap<>();

    private Map<UUID, CompletableFuture<Void>> deregisteredCustomers = new ConcurrentHashMap<>();
    private Map<UUID, CompletableFuture<Void>> deregisteredMerchants = new ConcurrentHashMap<>();

    private Runnable unsubscribeCustomerRegistered, unsubscribeMerchantRegistered,
            unsubscribeCustomerDeregistered, unsubscribeMerchantDeregistered,
            unsubscribeDeregisterCustomerFailed, unsubscribeDeregisterMerchantFailed;

    public AccountManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribeCustomerRegistered = queue.subscribe("CustomerRegistered", this::confirmCustomerRegistration);
        unsubscribeMerchantRegistered = queue.subscribe("MerchantRegistered", this::confirmMerchantRegistration);

        unsubscribeCustomerDeregistered = queue.subscribe("CustomerDeregistered", e ->
                completedHandler(deregisteredCustomers, e)
        );
        unsubscribeDeregisterCustomerFailed = queue.subscribe("DeregisterCustomerFailed", e ->
                errorHandler(deregisteredCustomers, CustomerNotFoundException::new, e)
        );

        unsubscribeMerchantDeregistered = queue.subscribe("MerchantDeregistered", e ->
                completedHandler(deregisteredMerchants, e)
        );
        unsubscribeDeregisterMerchantFailed = queue.subscribe("DeregisterMerchantFailed", e ->
                errorHandler(deregisteredMerchants, MerchantNotFoundException::new, e)
        );
    }

    @PreDestroy // For testing, on hot reload we the remove previous subscription
    public void close() {
        unsubscribeCustomerRegistered.run();
        unsubscribeMerchantRegistered.run();
        unsubscribeCustomerDeregistered.run();
        unsubscribeDeregisterCustomerFailed.run();
        unsubscribeMerchantDeregistered.run();
        unsubscribeDeregisterMerchantFailed.run();
    }

    public Customer registerCustomer(Customer customer, String bankAccountId) {
        CompletableFuture<Customer> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        registeredCustomers.put(id, future);
        Event event = new Event("CustomerRegistrationRequested", Map.of("id", id, "customer", customer, "bankAccountId", bankAccountId));
        queue.publish(event);
        return future.join();
    }

    public boolean deregisterCustomer(UUID customerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        deregisteredCustomers.put(id, future);
        Event event = new Event("CustomerDeregistrationRequested", Map.of("id", id, "customerId", customerId));
        queue.publish(event);
        future.join();
        return true;
    }

    public Merchant registerMerchant(Merchant merchant, String bankAccountId) {
        CompletableFuture<Merchant> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        registeredMerchants.put(id, future);
        Event event = new Event("MerchantRegistrationRequested", Map.of("id", id, "merchant", merchant, "bankAccountId", bankAccountId));
        queue.publish(event);
        return future.join();
    }

    public boolean deregisterMerchant(UUID merchantId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        deregisteredMerchants.put(id, future);
        Event event = new Event("MerchantDeregistrationRequested", Map.of("id", id, "merchantId", merchantId));
        queue.publish(event);
        future.join();
        return true;
    }

    public void confirmCustomerRegistration(Event e) {
        Customer customer = e.getArgument("customer", Customer.class);
        UUID eventId = e.getArgument("id", UUID.class);
        registeredCustomers.remove(eventId).complete(customer);
    }

    public void confirmMerchantRegistration(Event e) {
        Merchant merchant = e.getArgument("merchant", Merchant.class);
        UUID eventId = e.getArgument("id", UUID.class);
        registeredMerchants.remove(eventId).complete(merchant);
    }

}
