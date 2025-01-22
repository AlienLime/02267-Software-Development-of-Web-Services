package dtu.group17;

import java.util.Optional;
import java.util.UUID;

class PaymentData {
    private UUID id;
    private Optional<Token> token = Optional.empty();
    private Optional<Integer> amount = Optional.empty();
    private Optional<UUID> customerId = Optional.empty();
    private Optional<UUID> merchantId = Optional.empty();
    private Optional<String> customerAccountId = Optional.empty();
    private Optional<String> merchantAccountId = Optional.empty();

    public PaymentData(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Optional<Token> getToken() {
        return token;
    }

    public void setToken(Optional<Token> token) {
        this.token = token;
    }

    public Optional<Integer> getAmount() {
        return amount;
    }

    public void setAmount(Optional<Integer> amount) {
        this.amount = amount;
    }

    public Optional<UUID> getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Optional<UUID> customerId) {
        this.customerId = customerId;
    }

    public Optional<UUID> getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Optional<UUID> merchantId) {
        this.merchantId = merchantId;
    }

    public Optional<String> getCustomerAccountId() {
        return customerAccountId;
    }

    public void setCustomerAccountId(Optional<String> customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    public Optional<String> getMerchantAccountId() {
        return merchantAccountId;
    }

    public void setMerchantAccountId(Optional<String> merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }

    public boolean isComplete() {
        return token.isPresent() && amount.isPresent() && customerId.isPresent() && merchantId.isPresent() && customerAccountId.isPresent() && merchantAccountId.isPresent();
    }
    
}
