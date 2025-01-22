package dtu.group17;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class AccountManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<Customer>> registeredCustomers = new HashMap<>(); //
    private Map<UUID, CompletableFuture<Merchant>> registeredMerchants = new HashMap<>();

    private Map<UUID, CompletableFuture<Void>> deregisteredCustomers = new HashMap<>();
    private Map<UUID, CompletableFuture<Void>> deregisteredMerchants = new HashMap<>();

    private Runnable unsubscribeCustomerRegistered, unsubscribeMerchantRegistered,
            unsubscribeCustomerDeregistered, unsubscribeMerchantDeregistered;

    public AccountManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribeCustomerRegistered = queue.subscribe("CustomerRegistered", this::handleCustomerRegistered);
        unsubscribeMerchantRegistered = queue.subscribe("MerchantRegistered", this::handleMerchantRegistered);

        unsubscribeCustomerDeregistered = queue.subscribe("CustomerDeregistered", e ->
                handleDeregistered(deregisteredCustomers, e)
        );
        unsubscribeMerchantDeregistered = queue.subscribe("MerchantDeregistered", e ->
                handleDeregistered(deregisteredMerchants, e)
        );
    }

    @PreDestroy // For testing, on hot reload we the remove previous subscription
    public void close() {
        unsubscribeCustomerRegistered.run();
        unsubscribeMerchantRegistered.run();
        unsubscribeCustomerDeregistered.run();
        unsubscribeMerchantDeregistered.run();
    }

    public Customer registerCustomer(Customer customer, String bankAccountId) {
        CompletableFuture<Customer> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        registeredCustomers.put(id, future);
        Event event = new Event("CustomerRegistrationRequested", Map.of("id", id, "customer", customer, "bankAccountId", bankAccountId));
        queue.publish(event);
        return future.orTimeout(3, TimeUnit.SECONDS).join();
    }

    public boolean deregisterCustomer(UUID customerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        deregisteredCustomers.put(id, future);
        Event event = new Event("CustomerDeregistrationRequested", Map.of("id", id, "customerId", customerId));
        queue.publish(event);
        future.orTimeout(3, TimeUnit.SECONDS).join();
        return true;
    }

    public Merchant registerMerchant(Merchant merchant, String bankAccountId) {
        CompletableFuture<Merchant> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        registeredMerchants.put(id, future);
        Event event = new Event("MerchantRegistrationRequested", Map.of("id", id, "merchant", merchant, "bankAccountId", bankAccountId));
        queue.publish(event);
        return future.orTimeout(3, TimeUnit.SECONDS).join();
    }

    public boolean deregisterMerchant(UUID merchantId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        deregisteredMerchants.put(id, future);
        Event event = new Event("MerchantDeregistrationRequested", Map.of("id", id, "merchantId", merchantId));
        queue.publish(event);
        future.orTimeout(3, TimeUnit.SECONDS).join();
        return true;
    }

    public void handleCustomerRegistered(Event e) {
        Customer customer = e.getArgument("customer", Customer.class);
        UUID eventId = e.getArgument("id", UUID.class);
        registeredCustomers.remove(eventId).complete(customer);
    }

    public void handleMerchantRegistered(Event e) {
        Merchant merchant = e.getArgument("merchant", Merchant.class);
        UUID eventId = e.getArgument("id", UUID.class);
        registeredMerchants.remove(eventId).complete(merchant);
    }

    public void handleDeregistered(Map<UUID, CompletableFuture<Void>> deregistered, Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        deregistered.remove(eventId).complete(null);
    }

}
