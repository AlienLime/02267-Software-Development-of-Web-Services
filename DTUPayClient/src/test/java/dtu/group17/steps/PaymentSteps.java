package dtu.group17.steps;

import dtu.group17.helpers.ErrorMessageHelper;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.TokenHelper;
import dtu.group17.helpers.PaymentHelper;
import dtu.group17.merchant.Merchant;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentSteps {
    private ErrorMessageHelper errorMessageHelper;
    private AccountHelper accountHelper;
    private PaymentHelper paymentHelper;
    private TokenHelper tokenHelper;

    public PaymentSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper, PaymentHelper paymentHelper, TokenHelper tokenHelper) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.paymentHelper = paymentHelper;
        this.tokenHelper = tokenHelper;
    }

    @When("the merchant creates a payment for {int} kr")
    public void theMerchantCreatesAPaymentForKr(Integer amount) {
        paymentHelper.createPayment(amount, accountHelper.getCurrentMerchant());
    }

    @When("the merchant submits the payment")
    public void theMerchantSubmitsTheTransaction() {
        try {
            paymentHelper.submitTransaction();
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    @When("a payment of {int} kr between the customer and merchant is submitted")
    public void aPaymentOfKrBetweenTheCustomerAndMerchantIsSubmitted(Integer amount) {
        paymentHelper.createPayment(amount, accountHelper.getCurrentMerchant());
        tokenHelper.consumeFirstToken(accountHelper.getCurrentCustomer());
        paymentHelper.addToken(tokenHelper.getPresentedToken());
        try {
            paymentHelper.submitTransaction();
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    @When("a payment is created with merchant id {string}")
    public void aPaymentIsCreatedWithMerchantId(String merchantId) {
        Merchant merchant = new Merchant(UUID.fromString(merchantId), "FirstName", "LastName", AccountHelper.randomCPR());
        paymentHelper.createPayment(1, merchant);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertNull(errorMessageHelper.getErrorMessage());
    }

    @Then("the payment is unsuccessful")
    public void thePaymentIsUnsuccessful() {
        assertNotNull(errorMessageHelper.getErrorMessage());
    }

}
