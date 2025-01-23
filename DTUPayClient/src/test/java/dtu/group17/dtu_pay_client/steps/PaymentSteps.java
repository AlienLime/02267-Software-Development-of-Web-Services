package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.Token;
import dtu.group17.dtu_pay_client.customer.Customer;
import dtu.group17.dtu_pay_client.helpers.*;
import dtu.group17.dtu_pay_client.merchant.Merchant;
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

    public PaymentSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper,
                        PaymentHelper paymentHelper, TokenHelper tokenHelper, BankHelper bankHelper) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.paymentHelper = paymentHelper;
        this.tokenHelper = tokenHelper;
        this.bankHelper = bankHelper;
    }

    public void submitPayment(Customer customer, Merchant merchant, int amount) throws Exception {
        paymentHelper.createPayment(amount, merchant);
        Token token = tokenHelper.consumeFirstToken(customer);
        paymentHelper.addToken(token);
        paymentHelper.submitPayment(customer.id());
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
            submitPayment(customer, merchant, amount);
        }
    }

    @Given("the following payments have been made to the merchant")
    public void theFollowingPaymentsHaveBeenMadeToTheMerchant(io.cucumber.datatable.DataTable paymentDataTable) throws Exception {
        List<Map<String, String>> rows = paymentDataTable.asMaps(String.class, String.class);
        Merchant merchant = accountHelper.getCurrentMerchant();

        // Generate a customer
        Customer customer = accountHelper.createCustomer();
        String customerAccountId = bankHelper.createBankAccount(customer, 100000);
        customer = accountHelper.registerCustomerWithDTUPay(customer, customerAccountId);

        for (Map<String, String> columns : rows) {
            int amount = Integer.parseInt(columns.get("amount"));

            tokenHelper.requestTokens(customer, 1);
            submitPayment(customer, merchant, amount);
        }
    }

    @Given("the following payments have been made")
    public void theFollowingPaymentsHaveBeenMade(io.cucumber.datatable.DataTable paymentDataTable) throws Exception {
        List<Map<String, String>> rows = paymentDataTable.asMaps(String.class, String.class);

        for (Map<String, String> columns : rows) {
            int amount = Integer.parseInt(columns.get("amount"));
            String[] merchantName = columns.get("merchant name").trim().split(" ");
            String[] customerName = columns.get("customer name").trim().split(" ");

            // Create merchant
            Merchant merchant = accountHelper.createMerchant(merchantName[0], merchantName[1]);
            String merchantAccountId = bankHelper.createBankAccount(merchant, 0);
            merchant = accountHelper.registerMerchantWithDTUPay(merchant, merchantAccountId);

            // Create customer
            Customer customer = accountHelper.createCustomer(customerName[0], customerName[1]);
            String customerAccountId = bankHelper.createBankAccount(customer, amount);
            customer = accountHelper.registerCustomerWithDTUPay(customer, customerAccountId);

            tokenHelper.requestTokens(customer, 1);
            Token token = tokenHelper.consumeFirstToken(customer);

            paymentHelper.createPayment(amount, merchant);
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

    @When("a payment of {int} kr between the customer and merchant is submitted")
    public void aPaymentOfKrBetweenTheCustomerAndMerchantIsSubmitted(Integer amount) throws Exception {
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
