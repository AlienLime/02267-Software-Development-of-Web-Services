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

        queue.subscribe("AccountIdFromCustomerIdRequest", this::onAccountIdFromCustomerIdRequest); //TODO: Rename to past tense
        queue.subscribe("AccountIdFromMerchantIdRequest", this::onAccountIdFromMerchantIdRequest);

        queue.subscribe("CustomerDeregistrationRequested", this::onCustomerDeregistrationRequested);
        queue.subscribe("MerchantDeregistrationRequested", this::onMerchantDeregistrationRequested);

        queue.subscribe("ClearRequested", this::onClearRequested);
    }

    public void onCustomerRegistrationRequested(Event e) {
        Customer namedCustomer = e.getArgument("customer", Customer.class);
        String accountId = e.getArgument("bankAccountId", String.class);
        Customer customer = accountFactory.createCustomerWithID(namedCustomer, accountId);
        customerRepository.addCustomer(customer);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("CustomerRegistered", Map.of("id", eventId, "customer", customer));
        queue.publish(event);
    }
    
    public void onMerchantRegistrationRequested(Event e) {
        Merchant namedMerchant = e.getArgument("merchant", Merchant.class);
        String accountId = e.getArgument("bankAccountId", String.class);
        Merchant merchant = accountFactory.createMerchantWithID(namedMerchant, accountId);
        merchantRepository.addMerchant(merchant);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("MerchantRegistered", Map.of("id", eventId, "merchant", merchant));
        queue.publish(event);
    }

    public void onAccountIdFromCustomerIdRequest(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        String accountId = customerRepository.getCustomerById(customerId).accountId();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("AccountIdFromCustomerIdAnswer", Map.of("id", eventId, "accountId", accountId));
        queue.publish(event);
    }

    public void onAccountIdFromMerchantIdRequest(Event e) {
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        UUID eventId = e.getArgument("id", UUID.class);

        if (merchantRepository.getMerchantById(merchantId) == null) {
            String errorMessage = "Merchant with id '" + merchantId + "' does not exist";
            LOG.error(errorMessage);
            Event event = new Event("AccountIdFromMerchantIdError", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }
        String accountId = merchantRepository.getMerchantById(merchantId).accountId();

        Event event = new Event("AccountIdFromMerchantIdAnswer", Map.of("id", eventId, "accountId", accountId));
        queue.publish(event);
    }

    public void onCustomerDeregistrationRequested(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        customerRepository.removeCustomer(customerId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("CustomerDeregistered", Map.of("id", eventId));
        queue.publish(event);
    }

    public void onMerchantDeregistrationRequested(Event e) {
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        merchantRepository.removeMerchant(merchantId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("MerchantDeregistered", Map.of("id", eventId));
        queue.publish(event);
    }

    public void onClearRequested(Event e) {
        customerRepository.clearCustomers();
        merchantRepository.clearMerchants();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("AccountsCleared", Map.of("id", eventId));
        queue.publish(event);
    }

}
