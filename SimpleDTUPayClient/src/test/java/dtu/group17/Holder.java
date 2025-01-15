package dtu.group17;

import dtu.group17.customer.Customer;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.Payment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Holder {
    private Customer customer;
    private Merchant merchant;
    private String customerId, merchantId;
    private boolean successful = false;
    private Map<String, String> customers = new HashMap<>(); // name -> id
    private Map<String, String> merchants = new HashMap<>(); // name -> id
    private Map<String, String> accounts = new HashMap<>(); // cpr -> account id
    private Map<String, List<Token>> tokens = new HashMap<>(); // id -> list of tokens
    private Payment currentPayment;
    private Token presentedToken;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Map<String, String> getCustomers() {
        return customers;
    }

    public void setCustomers(Map<String, String> customers) {
        this.customers = customers;
    }

    public Map<String, String> getMerchants() {
        return merchants;
    }

    public void setMerchants(Map<String, String> merchants) {
        this.merchants = merchants;
    }

    public Map<String, String> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, String> accounts) {
        this.accounts = accounts;
    }

    public Map<String, List<Token>> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, List<Token>> tokens) {
        this.tokens = tokens;
    }

    public Payment getCurrentPayment() {
        return currentPayment;
    }

    public void setCurrentPayment(Payment currentPayment) {
        this.currentPayment = currentPayment;
    }

    public Token getPresentedToken() {
        return presentedToken;
    }

    public void setPresentedToken(Token presentedToken) {
        this.presentedToken = presentedToken;
    }
}
