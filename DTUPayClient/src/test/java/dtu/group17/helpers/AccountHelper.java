/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Helper class for creating and managing customer and merchant accounts.
 */

package dtu.group17.helpers;

import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AccountHelper {
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    private Customer currentCustomer;
    private Merchant currentMerchant;
    private List<Customer> customers = new ArrayList<>();
    private List<Merchant> merchants = new ArrayList<>();

    public AccountHelper(CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    /**
     * Clear all accounts and reset customer and merchant references.
     * Used to reset the state of the helper between tests.
     * @author Katja
     */
    public void clear() {
        currentCustomer = null;
        currentMerchant = null;
        customers.clear();
        merchants.clear();
    }

    /**
     * Generate a random CPR number.
     * @author Katja
     */
    public static String randomCPR() {
        return String.format("%06d-%04d", new Random().nextInt(999999), new Random().nextInt(9999)).replace(' ', '0');
    }

    /**
     * Create a new customer with a random CPR number and dummy first and last name.
     * @author Katja
     */
    public Customer createCustomer() {
        currentCustomer = new Customer(null, "DummyCustomerFirstName", "DummyCustomerLastName", randomCPR());
        customers.add(currentCustomer);
        return currentCustomer;
    }

    /**
     * Create a new customer with the given first and last name and a random CPR number.
     * The costumer will have no ID.
     * @author Katja
     */
    public Customer createCustomer(String firstName, String lastName) {
        currentCustomer = new Customer(null, firstName, lastName, randomCPR());
        customers.add(currentCustomer);
        return currentCustomer;
    }

    /**
     * Register a customer with DTUPay and assign an ID.
     * The customer must have a bank account with the given account ID.
     * The customer will be removed from the list of customers without an ID and added to the list of customers with an ID.
     * @param customer The customer to register
     * @param accountId The ID of the bank account
     * @return The registered customer with an ID
     * @throws Error if the registration fails
     * @author Katja
     */
    public Customer registerCustomerWithDTUPay(Customer customer, String accountId) {
        currentCustomer = customerAPI.register(customer, accountId);
        customers.removeIf(c -> c.cpr().equals(customer.cpr())); // Remove version of customer without id
        customers.add(currentCustomer);
        return currentCustomer;
    }

    /**
     * Deregister a customer with DTUPay.
     * The customer will be removed from the list of customers with an ID.
     * @param customer The customer to deregister
     * @author Katja
     */
    public void deregisterCustomerWithDTUPay(Customer customer) {
        customerAPI.deregister(customer.id());
        customers.removeIf(c -> c.cpr().equals(customer.cpr()));
        currentCustomer = null;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    /**
     * Create a new merchant with a random CPR number and dummy first and last name.
     * @author Katja
     */
    public Merchant createMerchant(String firstName, String lastName) {
        currentMerchant = new Merchant(null, firstName, lastName, randomCPR());
        merchants.add(currentMerchant);
        return currentMerchant;
    }

    public Merchant createMerchant() {
        return createMerchant("DummyMerchantFirstName", "DummyMerchantLastName");
    }

    /**
     * Register a merchant with DTUPay and assign an ID.
     * @param merchant The merchant to register
     * @param accountId The ID of the bank account
     * @author Katja
     */
    public Merchant registerMerchantWithDTUPay(Merchant merchant, String accountId) {
        currentMerchant = merchantAPI.register(merchant, accountId);
        merchants.removeIf(m -> m.cpr().equals(merchant.cpr())); // Remove version of merchant without id
        merchants.add(merchant);
        return currentMerchant;
    }

    /**
     * Deregister a merchant with DTUPay.
     * @param merchant The merchant to deregister
     * @author Katja
     */
    public void deregisterMerchantWithDTUPay(Merchant merchant) {
        merchantAPI.deregister(merchant.id());
        merchants.removeIf(m -> m.cpr().equals(merchant.cpr()));
        currentMerchant = null;
    }

    public Merchant getCurrentMerchant() {
        return currentMerchant;
    }

    public void setCurrentMerchant(Merchant merchant) {
        currentMerchant = merchant;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

}
