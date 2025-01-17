package dtu.group17;


import org.jboss.logging.Logger;
import java.util.UUID;
import java.util.Map;

public class AccountManager {
    private static final Logger LOG = Logger.getLogger(AccountManager.class);

    MessageQueue queue = new RabbitMQQueue();
    AccountFactory accountFactory = new AccountFactory();
    CustomerRepository customerRepository;
    MerchantRepository merchantRepository;

    public static void main(String[] args) {
        InMemoryRepository repo = new InMemoryRepository();
        new AccountManager(repo, repo);
    }

    public AccountManager(CustomerRepository customerRepository, MerchantRepository merchantRepository) {
        LOG.info("Starting Account Manager...");

        this.customerRepository = customerRepository;
        this.merchantRepository = merchantRepository;

        queue.subscribe("CustomerRegistrationRequested", this::onCustomerRegistrationRequested);
        queue.subscribe("MerchantRegistrationRequested", this::onMerchantRegistrationRequested);
        queue.subscribe("AccountIdFromCustomerIdRequest", this::onAccountIdFromCustomerIdRequest);
        queue.subscribe("AccountIdFromMerchantIdRequest", this::onAccountIdFromMerchantIdRequest);
    }

    public void onCustomerRegistrationRequested(Event e) {
        LOG.info("Received CustomerRegistrationRequested event");
        Customer customer = accountFactory.createCustomerWithID(e.getArgument("customer", Customer.class), e.getArgument("bankAccountId", String.class));
        customerRepository.addCustomer(customer);
        Event event = new Event("CustomerRegistered", Map.of("id", e.getArgument("id", UUID.class), "customer", customer));
        queue.publish(event);
        LOG.info("Sent CustomerRegistered event");
    }
    
    public void onMerchantRegistrationRequested(Event e) {
        LOG.info("Received MerchantRegistrationRequested event");
        Merchant merchant = accountFactory.createMerchantWithID(e.getArgument("merchant", Merchant.class), e.getArgument("bankAccountId", String.class));
        merchantRepository.addMerchant(merchant);
        Event event = new Event("MerchantRegistered", Map.of("id", e.getArgument("id", UUID.class), "merchant", merchant));
        queue.publish(event);
        LOG.info("Sent MerchantRegistered event");
    }

    public void onAccountIdFromCustomerIdRequest(Event e) {
        LOG.info("Received AccountIdFromCustomerIdRequest event");
        UUID customerId = e.getArgument("customerId", UUID.class);
        String accountId = customerRepository.getCustomerById(customerId).accountId();
        Event event = new Event("AccountIdFromCustomerIdAnswer", Map.of("id", e.getArgument("id", UUID.class), "accountId", accountId));
        queue.publish(event);
        LOG.info("Sent AccountIdFromCustomerIdAnswer event");
    }

    public void onAccountIdFromMerchantIdRequest(Event e) {
        LOG.info("Received AccountIdFromMerchantIdRequest event");
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        String accountId = merchantRepository.getMerchantById(merchantId).accountId();
        Event event = new Event("AccountIdFromMerchantIdAnswer", Map.of("id", e.getArgument("id", UUID.class), "accountId", accountId));
        queue.publish(event);
        LOG.info("Sent AccountIdFromMerchantIdAnswer event");
    }
}
