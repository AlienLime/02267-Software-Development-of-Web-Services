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

    public void clear() {
        currentCustomer = null;
        currentMerchant = null;
        customers.clear();
        merchants.clear();
    }

    public static String randomCPR() {
        return String.format("%06d-%04d", new Random().nextInt(999999), new Random().nextInt(9999)).replace(' ', '0');
    }

    public Customer createCustomer() {
        currentCustomer = new Customer(null, "DummyCustomerFirstName", "DummyCustomerLastName", randomCPR());
        customers.add(currentCustomer);
        return currentCustomer;
    }

    public Customer registerCustomerWithDTUPay(Customer customer, String accountId) {
        currentCustomer = customerAPI.register(customer, accountId);
        customers.removeIf(c -> c.cpr().equals(customer.cpr())); // Remove version of customer without id
        customers.add(customer);
        return currentCustomer;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public Merchant createMerchant(String firstName, String lastName) {
        currentMerchant = new Merchant(null, firstName, lastName, randomCPR());
        merchants.add(currentMerchant);
        return currentMerchant;
    }

    public Merchant createMerchant() {
        return createMerchant("DummyMerchantFirstName", "DummyMerchantLastName");
    }

    public Merchant registerMerchantWithDTUPay(Merchant merchant, String accountId) {
        currentMerchant = merchantAPI.register(merchant, accountId);
        merchants.removeIf(m -> m.cpr().equals(merchant.cpr())); // Remove version of merchant without id
        merchants.add(merchant);
        return currentMerchant;
    }

    public Merchant getCurrentMerchant() {
        return currentMerchant;
    }

    public void deregisterUsers() {
//        if (currentCustomer != null) customerAPI.deregister(currentCustomer.id());
//        if (currentMerchant != null) merchantAPI.deregister(currentMerchant.id());
    }

    public Customer getCustomerById(UUID id) {
        return customers.stream().filter(c -> c.id().equals(id)).findFirst().get();
    }
}
