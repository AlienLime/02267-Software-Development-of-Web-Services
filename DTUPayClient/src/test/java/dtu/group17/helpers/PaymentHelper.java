package dtu.group17.helpers;

import dtu.group17.Token;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.Payment;

public class PaymentHelper {
    private MerchantAPI merchantAPI;

    private Payment currentPayment;

    public void clear() {
        currentPayment = null;
    }

    public PaymentHelper(MerchantAPI merchantAPI) {
        this.merchantAPI = merchantAPI;
    }

    public Payment createPayment(int amount, Merchant merchant) {
        currentPayment = new Payment(null, amount, merchant.id());
        return currentPayment;
    }

    public void submitTransaction() throws Exception {
        merchantAPI.submitPayment(currentPayment);
    }

    public Payment addToken (Token token) {
        currentPayment = new Payment(token, currentPayment.amount(), currentPayment.merchantId());
        return currentPayment;
    }

}
