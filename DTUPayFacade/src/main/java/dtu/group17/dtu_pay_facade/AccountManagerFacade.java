/*
 * Author: Emil Kim Krarup (s204449)
 * Description:
 * The AccountManagerFacade is a facade for the AccountManager service. It delegates account registration and deregistration without any business logic.
 */

package dtu.group17.dtu_pay_facade;

import dtu.group17.dtu_pay_facade.exceptions.CustomerNotFoundException;
import dtu.group17.dtu_pay_facade.exceptions.MerchantNotFoundException;
import dtu.group17.dtu_pay_facade.domain.Customer;
import dtu.group17.dtu_pay_facade.domain.Merchant;
import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static dtu.group17.dtu_pay_facade.HandlerUtil.completedHandler;
import static dtu.group17.dtu_pay_facade.HandlerUtil.errorHandler;

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

    /**
     * Subscribes to the relevant events and sets up the message queue.
     * @author Emil Kim Krarup (s204449)
     */
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

    /**
     * For testing, on hot reload we the remove previous subscription
     * @author Emil Kim Krarup (s204449)
     */
    @PreDestroy
    public void close() {
        unsubscribeCustomerRegistered.run();
        unsubscribeMerchantRegistered.run();
        unsubscribeCustomerDeregistered.run();
        unsubscribeDeregisterCustomerFailed.run();
        unsubscribeMerchantDeregistered.run();
        unsubscribeDeregisterMerchantFailed.run();
    }

    /**
     * Publishes an event for registering a customer and waits for the response.
     * @param customer The customer to register
     * @param bankAccountId The bank account id of the customer
     * @return The registered customer
     * @throws CustomerNotFoundException If the customer could not be registered
     * @author Stine Lund Madsen (s204425)
     */
    public Customer registerCustomer(Customer customer, String bankAccountId) {
        CompletableFuture<Customer> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        registeredCustomers.put(id, future);
        Event event = new Event("CustomerRegistrationRequested", Map.of("id", id, "customer", customer, "bankAccountId", bankAccountId));
        queue.publish(event);
        return future.join();
    }

    /**
     * Publishes an event for deregistering a customer and waits for the response.
     * @param customerId The id of the customer to deregister
     * @return True if the customer was deregistered
     * @throws CustomerNotFoundException If the customer could not be deregistered
     * @author Victor G. H. Rasmussen (s204475)
     */
    public boolean deregisterCustomer(UUID customerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        deregisteredCustomers.put(id, future);
        Event event = new Event("CustomerDeregistrationRequested", Map.of("id", id, "customerId", customerId));
        queue.publish(event);
        future.join();
        return true;
    }

    /**
     * Publishes an event for registering a merchant and waits for the response.
     * @param merchant The merchant to register
     * @param bankAccountId The bank account id of the merchant
     * @return The registered merchant
     * @throws MerchantNotFoundException If the merchant could not be registered
     * @author Emil Wraae Carlsen (s204458)
     */
    public Merchant registerMerchant(Merchant merchant, String bankAccountId) {
        CompletableFuture<Merchant> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        registeredMerchants.put(id, future);
        Event event = new Event("MerchantRegistrationRequested", Map.of("id", id, "merchant", merchant, "bankAccountId", bankAccountId));
        queue.publish(event);
        return future.join();
    }

    /**
     * Publishes an event for deregistering a merchant and waits for the response.
     * @param merchantId The id of the merchant to deregister
     * @return True if the merchant was deregistered
     * @throws MerchantNotFoundException If the merchant could not be deregistered
     * @author Stine Lund Madsen (s204425)
     */
    public boolean deregisterMerchant(UUID merchantId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        deregisteredMerchants.put(id, future);
        Event event = new Event("MerchantDeregistrationRequested", Map.of("id", id, "merchantId", merchantId));
        queue.publish(event);
        future.join();
        return true;
    }

    /**
     * Confirms the registration of a customer by completing the future with the customer.
     * @param e The event containing the customer and the correlation id
     * @author Emil Kim Krarup (s204449)
     */
    public void confirmCustomerRegistration(Event e) {
        Customer customer = e.getArgument("customer", Customer.class);
        UUID eventId = e.getArgument("id", UUID.class);
        registeredCustomers.remove(eventId).complete(customer);
    }

    /**
     * Confirms the registration of a merchant by completing the future with the merchant.
     * @param e The event containing the merchant and the correlation id
     * @throws MerchantNotFoundException If the merchant could not be registered
     * @author Kristoffer Magnus Overgaard (s194110)
     */
    public void confirmMerchantRegistration(Event e) {
        Merchant merchant = e.getArgument("merchant", Merchant.class);
        UUID eventId = e.getArgument("id", UUID.class);
        registeredMerchants.remove(eventId).complete(merchant);
    }

}
