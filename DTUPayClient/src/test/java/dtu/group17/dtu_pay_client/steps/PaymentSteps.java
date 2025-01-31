/*
 * Author: Benjamin Noah Lumbye (s204428)
 * Description:
 * Contains the steps for payments.
 * The steps are used to create payments between customers and merchants.
 */

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    /**
     * Submits a payment between a customer and a merchant with a given amount.
     * @param customer The customer
     * @param merchant The merchant
     * @param amount The amount of the payment
     * @param description The description of the payment
     * @throws Exception If the payment fails
     * @author Benjamin Noah Lumbye (s204428)
     */
    public void submitPayment(Customer customer, Merchant merchant, int amount, String description) throws Exception {
        paymentHelper.createPayment(amount, merchant, description);
        Token token = tokenHelper.consumeFirstToken(customer);
        paymentHelper.addToken(token);
        paymentHelper.submitPayment(customer.id());
    }

    /**
     * Creates customer payments for the current customer based on a data table.
     * @param paymentDataTable The data table containing the payments
     * @throws Exception If the payment fails
     * @author G. H. Rasmussen (s204475)
     */
    @Given("the customer has made the following payments")
    public void theCustomerHasMadeTheFollowingPayments(io.cucumber.datatable.DataTable paymentDataTable) throws Exception {
        List<Map<String, String>> rows = paymentDataTable.asMaps(String.class, String.class);
        Customer customer = accountHelper.getCurrentCustomer();

        for (Map<String, String> columns : rows) {
            String[] name = columns.get("merchant name").trim().split(" ");
            Merchant merchant = accountHelper.createMerchant(name[0], name[1]);
            int amount = Integer.parseInt(columns.get("amount"));
            String description = columns.get("description");

            String accountId = bankHelper.createBankAccount(merchant, 0);
            merchant = accountHelper.registerMerchantWithDTUPay(merchant, accountId);
            submitPayment(customer, merchant, amount, description);
        }
    }

    /**
     * Creates merchant payments for the current merchant based on a data table.
     * @param paymentDataTable The data table containing the payments
     * @throws Exception If the payment fails
     * @author Emil Wraae Carlsen (s204458)
     */
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
            String description = columns.get("description");

            tokenHelper.requestTokens(customer, 1);
            submitPayment(customer, merchant, amount, description);
        }
    }

    /**
     * Creates payments between customers and merchants based on a data table.
     * @param paymentDataTable The data table containing the payments
     * @throws Exception If the payment fails
     * @author Benjamin Noah Lumbye (s204428)
     */
    @Given("the following payments have been made")
    public void theFollowingPaymentsHaveBeenMade(io.cucumber.datatable.DataTable paymentDataTable) throws Exception {
        List<Map<String, String>> rows = paymentDataTable.asMaps(String.class, String.class);

        for (Map<String, String> columns : rows) {
            int amount = Integer.parseInt(columns.get("amount"));
            String[] merchantName = columns.get("merchant name").trim().split(" ");
            String[] customerName = columns.get("customer name").trim().split(" ");
            String description = columns.get("description");

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

            paymentHelper.createPayment(amount, merchant, description);
            paymentHelper.addToken(token);
            paymentHelper.submitPayment(customer.id());
        }
    }

    /**
     * Creates a payment of a given amount for the current merchant.
     * @param amount The amount of the payment
     * @author Stine Lund Madsen (s204425)
     */
    @When("the merchant creates a payment for {int} kr")
    public void theMerchantCreatesAPaymentForKr(Integer amount) {
        paymentHelper.createPayment(amount, accountHelper.getCurrentMerchant(), "test payment of " + amount + " kr");
    }

    /**
     * Submits a payment between a customer and merchant by getting the customer from the consumed token.
     * @author Emil Kim Krarup (s204449)
     */
    @When("the merchant submits the payment")
    public void theMerchantSubmitsThePayment() {
        try {
            UUID customerId = tokenHelper.getCustomerFromConsumedToken(tokenHelper.getPresentedToken());
            paymentHelper.submitPayment(customerId);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    /**
     * Creates a payment between a customer and merchant with a given amount.
     * @param amount The amount of the payment
     * @throws Exception If the payment fails
     * @author Victor G. H. Rasmussen (s204475)
     */
    @When("a payment of {int} kr between the customer and merchant is submitted")
    public void aPaymentOfKrBetweenTheCustomerAndMerchantIsSubmitted(Integer amount) throws Exception {
        paymentHelper.createPayment(amount, accountHelper.getCurrentMerchant(), "test payment of " + amount + " kr");
        tokenHelper.consumeFirstToken(accountHelper.getCurrentCustomer());
        paymentHelper.addToken(tokenHelper.getPresentedToken());
        try {
            UUID customerId = tokenHelper.getCustomerFromConsumedToken(tokenHelper.getPresentedToken());
            paymentHelper.submitPayment(customerId);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    /**
     * Creates a payment between a customer and merchant with a given amount.
     * @param merchantId The id of the merchant
     * @author Victor G. H. Rasmussen (s204475)
     */
    @When("a payment is created with merchant id {string}")
    public void aPaymentIsCreatedWithMerchantId(String merchantId) {
        Merchant merchant = new Merchant(UUID.fromString(merchantId), "FirstName", "LastName", AccountHelper.randomCPR());
        paymentHelper.createPayment(1, merchant, "test payment of 1 kr");
    }

    /**
     * Creates a payment of a default value 1.
     * @author Stine Lund Madsen (s204425)
     */
    @When("the merchant creates a payment")
    public void theMerchantCreatesAPayment() {
        paymentHelper.createPayment(1, accountHelper.getCurrentMerchant(), "test payment of 1 kr");
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
