package dtu.group17.helpers;

import dtu.group17.FullPayment;
import dtu.group17.Token;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentHelper {
    private MerchantAPI merchantAPI;

    private List<FullPayment> previousPayments = new ArrayList<>();
    private Payment currentPayment;

    public void clear() {
        currentPayment = null;
        previousPayments.clear();
    }

    public PaymentHelper(MerchantAPI merchantAPI) {
        this.merchantAPI = merchantAPI;
    }

    public Payment createPayment(int amount, Merchant merchant) {
        currentPayment = new Payment(null, amount, merchant.id());
        return currentPayment;
    }

    public void submitPayment(UUID customerId) throws Exception {
        merchantAPI.submitPayment(currentPayment);
        previousPayments.add(new FullPayment(customerId, currentPayment.token(), currentPayment.amount(), currentPayment.merchantId()));
    }

    public Payment addToken (Token token) {
        currentPayment = new Payment(token, currentPayment.amount(), currentPayment.merchantId());
        return currentPayment;
    }

    public List<FullPayment> getCustomerPayments(UUID id) {
        return previousPayments.stream().filter(p -> p.customerId().equals(id)).toList();
    }

}
