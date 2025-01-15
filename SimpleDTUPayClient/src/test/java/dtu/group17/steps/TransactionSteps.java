package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.Payment;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionSteps {
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;
    private MerchantAPI merchantAPI;

    public TransactionSteps(Holder holder, ErrorMessageHolder errorMessageHolder, MerchantAPI merchantAPI) {
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
        this.merchantAPI = merchantAPI;
    }

    @When("the merchant creates a payment for {int} kr")
    public void theMerchantCreatesAPaymentForKr(Integer amount) {
        holder.setCurrentPayment(new Payment(null, amount, holder.getMerchantId()));
    }

    @When("the merchant submits the transaction to the server")
    public void theMerchantSubmitsTheTransactionToTheServer() throws Exception {
        holder.setSuccessful(merchantAPI.submitPayment(holder.getCurrentPayment()));
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(holder.isSuccessful());
    }
}
