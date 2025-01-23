/*
 * Author: Katja Kaj (s123456)
 * Description:
 *  This class is responsible for managing the accounts of customers and merchants.
 *  It listens for events from the message queue and register, deregister and retrieves accounts.
 */

package dtu.group17.account_manager;


import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import org.jboss.logging.Logger;
import java.util.UUID;
import java.util.Map;

public class AccountManager {
    private static final Logger LOG = Logger.getLogger(AccountManager.class);

    MessageQueue queue;
    AccountFactory accountFactory = new AccountFactory();
    CustomerRepository customerRepository;
    MerchantRepository merchantRepository;

    public static void main(String[] args) {
        InMemoryRepository repo = new InMemoryRepository();
        new AccountManager(new RabbitMQQueue(), repo, repo);
    }

    /**
     * Constructor for AccountManager.
     * Subscribes to all registration, deregistration and retrieval events.
     * @param queue: RabbitMQ message queue
     * @param customerRepository: Repository for customers
     * @param merchantRepository: Repository for merchants
     * @author Katja Kaj
     */
    public AccountManager(MessageQueue queue, CustomerRepository customerRepository, MerchantRepository merchantRepository) {
        LOG.info("Starting Account Manager...");

        this.queue = queue;
        this.customerRepository = customerRepository;
        this.merchantRepository = merchantRepository;

        queue.subscribe("CustomerRegistrationRequested", this::registerCustomer);
        queue.subscribe("MerchantRegistrationRequested", this::registerMerchant);

        queue.subscribe("TokenValidated", this::retrieveCustomerBankAccount);
        queue.subscribe("PaymentRequested", this::retrieveMerchantBankAccount);

        queue.subscribe("CustomerDeregistrationRequested", this::deregisterCustomer);
        queue.subscribe("MerchantDeregistrationRequested", this::deregisterMerchant);

        queue.subscribe("ClearRequested", this::clearAccounts);
    }

    /**
     * Uses the AccountFactory to create a new customer with a unique ID and adds it to the customer repository.
     * Publishes an event with the customer ID.
     * @param e: Event containing the customer and bank account ID
     * @author Katja
     */
    public Customer registerCustomer(Event e) {
        Customer namedCustomer = e.getArgument("customer", Customer.class);
        String accountId = e.getArgument("bankAccountId", String.class);
        Customer customer = accountFactory.createCustomerWithID(namedCustomer, accountId);
        customerRepository.addCustomer(customer);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("CustomerRegistered", Map.of("id", eventId, "customer", customer));
        queue.publish(event);
        return customer;
    }

    /**
     * Uses the AccountFactory to create a new merchant with a unique ID and adds it to the merchant repository.
     * Publishes an event with the merchant ID.
     * @param e: Event containing the merchant and bank account ID
     *         @author Katja
     */
    public Merchant registerMerchant(Event e) {
        Merchant namedMerchant = e.getArgument("merchant", Merchant.class);
        String accountId = e.getArgument("bankAccountId", String.class);
        Merchant merchant = accountFactory.createMerchantWithID(namedMerchant, accountId);
        merchantRepository.addMerchant(merchant);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("MerchantRegistered", Map.of("id", eventId, "merchant", merchant));
        queue.publish(event);
        return merchant;
    }

    /**
     * Publishes an event with a customer's bank account ID.
     * @param e Event containing the customer ID
     *        @Author Katja
     */
    public void retrieveCustomerBankAccount(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        UUID eventId = e.getArgument("id", UUID.class);

        Customer customer = customerRepository.getCustomerById(customerId);
        if (customer == null) {
            String errorMessage = "Customer with id '" + customerId + "' does not exist";
            LOG.error(errorMessage);
            Event event = new Event("RetrieveCustomerBankAccountFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        String accountId = customer.accountId();
        Event event = new Event("CustomerBankAccountRetrieved", Map.of("id", eventId, "customerId", customerId, "accountId", accountId));
        queue.publish(event);
    }

    /**
     * Publishes an event with a merchant's bank account ID.
     * @param e Event containing the merchant ID
     */
    public void retrieveMerchantBankAccount(Event e) {
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        UUID eventId = e.getArgument("id", UUID.class);

        Merchant merchant = merchantRepository.getMerchantById(merchantId);
        if (merchant == null) {
            String errorMessage = "Merchant with id '" + merchantId + "' does not exist";
            LOG.error(errorMessage);
            Event event = new Event("RetrieveMerchantBankAccountFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        String accountId = merchant.accountId();
        Event event = new Event("MerchantBankAccountRetrieved", Map.of("id", eventId, "accountId", accountId));
        queue.publish(event);
    }

    /**
     * Removes a customer from the customer repository and publishes an event with the customer ID.
     * @param e Event containing the customer ID
     *       @Author Katja
     */
    public void deregisterCustomer(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        UUID eventId = e.getArgument("id", UUID.class);

        Customer customer = customerRepository.removeCustomer(customerId);
        if (customer == null) {
            String errorMessage = "Customer with id '" + customerId + "' does not exist";
            LOG.error(errorMessage);
            Event event = new Event("DeregisterCustomerFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        Event event = new Event("CustomerDeregistered", Map.of("id", eventId));
        queue.publish(event);
    }

    /**
     * Removes a merchant from the merchant repository and publishes an event with the merchant ID.
     * @param e Event containing the merchant ID
     *        @Author Katja
     */
    public void deregisterMerchant(Event e) {
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        UUID eventId = e.getArgument("id", UUID.class);

        Merchant merchant = merchantRepository.removeMerchant(merchantId);
        if (merchant == null) {
            String errorMessage = "Merchant with id '" + merchantId + "' does not exist";
            LOG.error(errorMessage);
            Event event = new Event("DeregisterMerchantFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        Event event = new Event("MerchantDeregistered", Map.of("id", eventId));
        queue.publish(event);
    }

    /**
     * Clears all customers and merchants from the repositories and publishes an event.
     * @param e Event containing the event ID
     *        @Author Katja
     */
    public void clearAccounts(Event e) {
        customerRepository.clearCustomers();
        merchantRepository.clearMerchants();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("AccountsCleared", Map.of("id", eventId));
        queue.publish(event);
    }

}
