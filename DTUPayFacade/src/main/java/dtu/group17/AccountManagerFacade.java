package dtu.group17;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class AccountManagerFacade {
    private static final Logger LOG = Logger.getLogger(AccountManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<Customer>> registeredCustomers = new HashMap<>(); //
    private Map<UUID, CompletableFuture<Merchant>> registeredMerchants = new HashMap<>();

    private Runnable unsubscribeCustomerRegistered, unsubscribeMerchantRegistered;

    public AccountManagerFacade() throws IOException {
        queue = new RabbitMQQueue();
        unsubscribeCustomerRegistered = queue.subscribe("CustomerRegistered", this::handleCustomerRegistered);
        unsubscribeMerchantRegistered = queue.subscribe("MerchantRegistered", this::handleMerchantRegistered);
    }

    @PreDestroy
    public void close() {
        unsubscribeCustomerRegistered.run();
        unsubscribeMerchantRegistered.run();
    }

    public Customer registerCustomer(Customer customer, String bankAccountId) {
        CompletableFuture<Customer> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        registeredCustomers.put(id, future);
        Event event = new Event("CustomerRegistrationRequested", Map.of("id", id, "customer", customer, "bankAccountId", bankAccountId));
        queue.publish(event);
        LOG.info("Sent CustomerRegistrationRequested event");
        return future.join();
    }

    public void handleCustomerRegistered(Event e) {
        LOG.info("Received CustomerRegistered event");
        Customer customer = e.getArgument("customer", Customer.class);
        registeredCustomers.remove(e.getArgument("id", UUID.class)).complete(customer);
    }

    public Merchant registerMerchant(Merchant merchant, String bankAccountId) {
        CompletableFuture<Merchant> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        registeredMerchants.put(id, future);
        Event event = new Event("MerchantRegistrationRequested", Map.of("id", id, "merchant", merchant, "bankAccountId", bankAccountId));
        queue.publish(event);
        LOG.info("Sent MerchantRegistrationRequested event");
        return future.join();
    }

    public void handleMerchantRegistered(Event e) {
        LOG.info("Received MerchantRegistered event");
        Merchant merchant = e.getArgument("merchant", Merchant.class);
        registeredMerchants.remove(e.getArgument("id", UUID.class)).complete(merchant);
    }

    public void deregisterCustomer(String id) {
    }
}
