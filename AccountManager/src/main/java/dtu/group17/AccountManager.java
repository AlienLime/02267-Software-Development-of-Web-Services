package dtu.group17;


import org.jboss.logging.Logger;

import java.util.UUID;
import java.util.Map;

public class AccountManager {
    private static final Logger LOG = Logger.getLogger(AccountManager.class);

    MessageQueue queue = new RabbitMQQueue();
    CustomerFactory customerFactory = new AccountFactory();

    public static void main(String[] args) {
        new AccountManager();
    }

    public AccountManager() {
        LOG.info("Starting Account Manager...");
        queue.subscribe("CustomerRegistrationRequested", this::onCustomerRegistrationRequested);
    }

    public void onCustomerRegistrationRequested(Event e) {
        LOG.info("Received CustomerRegistrationRequested event");
        Customer customer = customerFactory.createCustomerWithID(e.getArgument("customer", Customer.class), e.getArgument("bankAccountId", String.class));
        Event event = new Event("CustomerRegistered", Map.of("id", e.getArgument("id", UUID.class), "customer", customer));
        queue.publish(event);
        LOG.info("Sent CustomerRegistered event");
    }
}
