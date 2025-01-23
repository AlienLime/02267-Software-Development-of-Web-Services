package dtu.group17.helpers;

import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountHelper {
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    private Customer currentCustomer;
    private Merchant currentMerchant;
    private List<Customer> customers = new ArrayList<>();
    private List<Merchant> merchants = new ArrayList<>();

    private boolean customerIsDeregistered;
    private boolean merchantIsDeregistered;

    public AccountHelper(CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    public void clear() {
        currentCustomer = null;
        currentMerchant = null;
        customers.clear();
        merchants.clear();
        customerIsDeregistered = false;
        merchantIsDeregistered = false;
    }

    public static String randomCPR() {
        return String.format("%06d-%04d", new Random().nextInt(999999), new Random().nextInt(9999)).replace(' ', '0');
    }

    public Customer createCustomer() {
        return createCustomer("DummyCustomerFirstName", "DummyCustomerLastName");
    }

    public Customer createCustomer(String firstName, String lastName) {
        currentCustomer = new Customer(null, firstName, lastName, randomCPR());
        customers.add(currentCustomer);
        return currentCustomer;
    }

    public Customer registerCustomerWithDTUPay(Customer customer, String accountId) {
        currentCustomer = customerAPI.register(customer, accountId);
        customers.removeIf(c -> c.cpr().equals(customer.cpr())); // Remove version of customer without id
        customers.add(currentCustomer);
        return currentCustomer;
    }

    public boolean deregisterCustomerWithDTUPay(Customer customer) throws Exception {
        customerIsDeregistered = customerAPI.deregister(customer.id());
        customers.removeIf(c -> c.cpr().equals(customer.cpr()));
        currentCustomer = null;
        return customerIsDeregistered;
    }

    public boolean getCustomerIsDeregistered() {
        return customerIsDeregistered;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    public Merchant createMerchant() {
        return createMerchant("DummyMerchantFirstName", "DummyMerchantLastName");
    }

    public Merchant createMerchant(String firstName, String lastName) {
        currentMerchant = new Merchant(null, firstName, lastName, randomCPR());
        merchants.add(currentMerchant);
        return currentMerchant;
    }

    public Merchant registerMerchantWithDTUPay(Merchant merchant, String accountId) {
        currentMerchant = merchantAPI.register(merchant, accountId);
        merchants.removeIf(m -> m.cpr().equals(merchant.cpr())); // Remove version of merchant without id
        merchants.add(currentMerchant);
        return currentMerchant;
    }

    public boolean deregisterMerchantWithDTUPay(Merchant merchant) throws Exception {
        merchantIsDeregistered = merchantAPI.deregister(merchant.id());
        merchants.removeIf(m -> m.cpr().equals(merchant.cpr()));
        currentMerchant = null;
        return merchantIsDeregistered;
    }

    public boolean getMerchantIsDeregistered() {
        return merchantIsDeregistered;
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
