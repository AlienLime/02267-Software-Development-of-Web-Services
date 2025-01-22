/*
*
 */

package dtu.group17.helpers;

import dtu.group17.FullPayment;
import dtu.group17.Token;
import dtu.group17.customer.Customer;
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


    /**
     * Create a new payment with the given amount and merchant.
     * @param amount The amount of money involved in the payment.
     * @param merchant The merchant receiving the payment.
     * @author Katja
     */
    public Payment createPayment(int amount, Merchant merchant) {
        currentPayment = new Payment(null, amount, merchant.id());
        return currentPayment;
    }

    /**
     * Submit the current payment to the merchant API.
     * @param customerId The ID of the customer making the payment.
     * @throws Exception
     * @author Katja
     */
    public void submitPayment(UUID customerId) throws Exception {
        merchantAPI.submitPayment(currentPayment);
        previousPayments.add(new FullPayment(customerId, currentPayment.token(), currentPayment.amount(), currentPayment.merchantId()));
    }

    /**
     * Add a token to the current payment.
     * @param token The token to add to the payment.
     * @return The updated payment.
     * @author Katja
     */
    public Payment addToken (Token token) {
        currentPayment = new Payment(token, currentPayment.amount(), currentPayment.merchantId());
        return currentPayment;
    }

    /**
     * Get the payments made by a customer.
     * @param customer The customer for whom the payments should be retrieved.
     * @author Katja
     */
    public List<FullPayment> getCustomerPayments(Customer customer) {
        return previousPayments.stream().filter(p -> p.customerId().equals(customer.id())).toList();
    }

    /**
     * Get the payments involving the given merchant.
     * @param merchant The merchant for whom the payments should be retrieved.
     * @author Katja
     */
    public List<FullPayment> getMerchantPayments(Merchant merchant) {
        return previousPayments.stream().filter(p -> p.merchantId().equals(merchant.id())).toList();
    }

    public List<FullPayment> getPayments() {
        return previousPayments;
    }

}
