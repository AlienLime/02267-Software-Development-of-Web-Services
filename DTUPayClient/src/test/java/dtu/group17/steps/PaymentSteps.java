package dtu.group17.steps;

import dtu.group17.Token;
import dtu.group17.customer.Customer;
import dtu.group17.helpers.*;
import dtu.group17.merchant.Merchant;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentSteps {
    private ErrorMessageHelper errorMessageHelper;
    private AccountHelper accountHelper;
    private PaymentHelper paymentHelper;
    private TokenHelper tokenHelper;
    private BankHelper bankHelper;

    public PaymentSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper, PaymentHelper paymentHelper, TokenHelper tokenHelper, BankHelper bankHelper) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.paymentHelper = paymentHelper;
        this.tokenHelper = tokenHelper;
        this.bankHelper = bankHelper;
    }

    @Given("the customer has made the following payments")
    public void theCustomerHasMadeTheFollowingPayments(io.cucumber.datatable.DataTable paymentDataTable) throws Exception {
        List<Map<String, String>> rows = paymentDataTable.asMaps(String.class, String.class);
        Customer customer = accountHelper.getCurrentCustomer();

        for (Map<String, String> columns : rows) {
            String[] name = columns.get("merchant name").trim().split(" ");
            Merchant merchant = accountHelper.createMerchant(name[0], name[1]);
            int amount = Integer.parseInt(columns.get("amount"));

            String accountId = bankHelper.createBankAccount(merchant, 0);
            merchant = accountHelper.registerMerchantWithDTUPay(merchant, accountId);
            paymentHelper.createPayment(amount, merchant);
            Token token = tokenHelper.consumeFirstToken(customer);
            paymentHelper.addToken(token);
            paymentHelper.submitPayment(customer.id());
        }
    }

    @When("the merchant creates a payment for {int} kr")
    public void theMerchantCreatesAPaymentForKr(Integer amount) {
        paymentHelper.createPayment(amount, accountHelper.getCurrentMerchant());
    }

    @When("the merchant submits the payment")
    public void theMerchantSubmitsThePayment() {
        try {
            UUID customerId = tokenHelper.getCustomerFromConsumedToken(tokenHelper.getPresentedToken());
            paymentHelper.submitPayment(customerId);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    // TODO: This covers
    /*
    *   When the merchant creates a payment for 10 kr
    *   And the customer presents a valid token to the merchant
    *   And the merchant receives the token
    *   And the merchant submits the payment
    * */
    @When("a payment of {int} kr between the customer and merchant is submitted")
    public void aPaymentOfKrBetweenTheCustomerAndMerchantIsSubmitted(Integer amount) {
        paymentHelper.createPayment(amount, accountHelper.getCurrentMerchant());
        tokenHelper.consumeFirstToken(accountHelper.getCurrentCustomer());
        paymentHelper.addToken(tokenHelper.getPresentedToken());
        try {
            UUID customerId = tokenHelper.getCustomerFromConsumedToken(tokenHelper.getPresentedToken());
            paymentHelper.submitPayment(customerId);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    @When("a payment is created with merchant id {string}")
    public void aPaymentIsCreatedWithMerchantId(String merchantId) {
        Merchant merchant = new Merchant(UUID.fromString(merchantId), "FirstName", "LastName", AccountHelper.randomCPR());
        paymentHelper.createPayment(1, merchant);
    }

    @When("the merchant creates a payment")
    public void theMerchantCreatesAPayment() {
        paymentHelper.createPayment(1, accountHelper.getCurrentMerchant());
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
