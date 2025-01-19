package dtu.group17;

import dtu.group17.customer.Customer;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.Payment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Holder {
    private Customer customer;
    private Merchant merchant;
    private UUID customerId, merchantId;
    private boolean successful = false;
    private Map<String, UUID> customers = new HashMap<>(); // name -> id
    private Map<String, UUID> merchants = new HashMap<>(); // name -> id
    private Map<String, String> accounts = new HashMap<>(); // cpr -> account id
    private Map<UUID, List<Token>> tokens = new HashMap<>(); // id -> list of tokens
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

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(UUID merchantId) {
        this.merchantId = merchantId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Map<String, UUID> getCustomers() {
        return customers;
    }

    public void setCustomers(Map<String, UUID> customers) {
        this.customers = customers;
    }

    public Map<String, UUID> getMerchants() {
        return merchants;
    }

    public void setMerchants(Map<String, UUID> merchants) {
        this.merchants = merchants;
    }

    public Map<String, String> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, String> accounts) {
        this.accounts = accounts;
    }

    public Map<UUID, List<Token>> getTokens() {
        return tokens;
    }

    public void setTokens(Map<UUID, List<Token>> tokens) {
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
