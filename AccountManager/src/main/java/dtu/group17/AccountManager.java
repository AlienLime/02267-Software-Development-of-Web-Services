package dtu.group17;


import org.jboss.logging.Logger;
import java.util.UUID;
import java.util.Map;

public class AccountManager {
    private static final Logger LOG = Logger.getLogger(AccountManager.class);

    MessageQueue queue = new RabbitMQQueue();
    AccountFactory accountFactory = new AccountFactory();

    public static void main(String[] args) {
        new AccountManager();
    }

    public AccountManager() {
        LOG.info("Starting Account Manager...");
        queue.subscribe("CustomerRegistrationRequested", this::onCustomerRegistrationRequested);
        queue.subscribe("MerchantRegistrationRequested", this::onMerchantRegistrationRequested);
    }

    public void onCustomerRegistrationRequested(Event e) {
        LOG.info("Received CustomerRegistrationRequested event");
        Customer customer = accountFactory.createCustomerWithID(e.getArgument("customer", Customer.class), e.getArgument("bankAccountId", String.class));
        Event event = new Event("CustomerRegistered", Map.of("id", e.getArgument("id", UUID.class), "customer", customer));
        queue.publish(event);
        LOG.info("Sent CustomerRegistered event");
    }
    
    public void onMerchantRegistrationRequested(Event e) {
        LOG.info("Received MerchantRegistrationRequested event");
        Merchant merchant = accountFactory.createMerchantWithID(e.getArgument("merchant", Merchant.class), e.getArgument("bankAccountId", String.class));
        Event event = new Event("MerchantRegistered", Map.of("id", e.getArgument("id", UUID.class), "merchant", merchant));
        queue.publish(event);
        LOG.info("Sent MerchantRegistered event");
    }
}
