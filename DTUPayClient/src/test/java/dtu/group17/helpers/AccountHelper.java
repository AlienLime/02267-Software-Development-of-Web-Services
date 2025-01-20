package dtu.group17.helpers;

import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;

import java.util.Random;

public class AccountHelper {
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    private Customer currentCustomer;
    private Merchant currentMerchant;

    public AccountHelper(CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    public void clear() {
        currentCustomer = null;
        currentMerchant = null;
    }

    public static String randomCPR() {
        return String.format("%06d-%04d", new Random().nextInt(999999), new Random().nextInt(9999)).replace(' ', '0');
    }

    public Customer createCustomer() {
        currentCustomer = new Customer(null, "DummyCustomerFirstName", "DummyCustomerLastName", randomCPR());;
        return currentCustomer;
    }

    public Customer registerCustomerWithDTUPay(Customer customer, String accountId) {
        currentCustomer = customerAPI.register(customer, accountId);
        return currentCustomer;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public Merchant createMerchant() {
        currentMerchant = new Merchant(null, "DummyMerchantFirstName", "DummyMerchantLastName", randomCPR());
        return currentMerchant;
    }

    public Merchant registerMerchantWithDTUPay(Merchant merchant, String accountId) {
        currentMerchant = merchantAPI.register(merchant, accountId);
        return currentMerchant;
    }

    public Merchant getCurrentMerchant() {
        return currentMerchant;
    }

    public void deregisterUsers() {
        if (currentCustomer != null) customerAPI.deregister(currentCustomer.id());
        if (currentMerchant != null) merchantAPI.deregister(currentMerchant.id());
    }
}
