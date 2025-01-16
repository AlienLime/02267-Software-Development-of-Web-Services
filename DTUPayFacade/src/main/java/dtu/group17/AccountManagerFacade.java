package dtu.group17;


import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AccountManagerFacade {
    private static final Logger LOG = Logger.getLogger(AccountManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<Customer>> registeredCustomers = new HashMap<>();

    public AccountManagerFacade(MessageQueue queue) throws IOException {
        this.queue = queue;
        queue.subscribe("CustomerRegistered", this::handleCustomerRegistered);
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

    public void deregisterCustomer(String id) {
    }
}
