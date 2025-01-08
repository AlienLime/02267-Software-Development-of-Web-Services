package dtu.group17;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class PaymentSteps {
    private Customer customer;
    private Merchant merchant;
    private String customerId, merchantId;
    private SimpleDTUPay dtupay = new SimpleDTUPay();
    private boolean successful = false;
    private List<Payment> payments;
    Map<String, String> customers = new HashMap<>(); // name -> id
    Map<String, String> merchants = new HashMap<>(); // name -> id
    private String errorMessage;

    @Before
    public void before() {
        customer = null;
        merchant = null;
        customerId = null;
        merchantId = null;
        successful = false;
        payments = null;
        customers = new HashMap<>();
        merchants = new HashMap<>();
        errorMessage = null;
    }

    @After
    public void after() {
        customers.values().forEach(dtupay::deregisterCustomer);
        merchants.values().forEach(dtupay::deregisterMerchant);
        dtupay.clearPayments();
    }

    @Given("a customer with name {string}")
    public void aCustomerWithName(String name) {
        customer = new Customer(name);
    }

    @Given("the customer is registered with Simple DTU Pay")
    public void theCustomerIsRegisteredWithSimpleDTUPay() {
        customerId = dtupay.register(customer);
    }

    @Given("a merchant with name {string}")
    public void aMerchantWithName(String name) {
        merchant = new Merchant(name);
    }

    @Given("the merchant is registered with Simple DTU Pay")
    public void theMerchantIsRegisteredWithSimpleDTUPay() {
        merchantId = dtupay.register(merchant);
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(Integer amount) {
        try {
            successful = dtupay.pay(amount, customerId, merchantId);
        } catch (Exception e) {
            successful = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(successful);
    }

    @Given("a customer with name {string}, who is registered with Simple DTU Pay")
    public void aCustomerWithNameWhoIsRegisteredWithSimpleDTUPay(String name) {
        customer = new Customer(name);
        customerId = dtupay.register(customer);
        customers.put(customer.name(), customerId);
    }

    @Given("a merchant with name {string}, who is registered with Simple DTU Pay")
    public void aMerchantWithNameWhoIsRegisteredWithSimpleDTUPay(String name) {
        merchant = new Merchant(name);
        merchantId = dtupay.register(merchant);
        merchants.put(merchant.name(), merchantId);
    }

    @Given("a successful payment of {int} kr from the customer to the merchant")
    public void aSuccessfulPaymentOfKrFromTheCustomerToTheMerchant(Integer amount) {
        try {
            successful = dtupay.pay(amount, customerId, merchantId);
        } catch (Exception e) {
            successful = false;
            errorMessage = e.getMessage();
        }
    }

    @When("the manager asks for a list of payments")
    public void theManagerAsksForAListOfPayments() {
        payments = dtupay.getPayments();
    }

    @Then("the list contains a payments where customer {string} paid {int} kr to merchant {string}")
    public void theListContainsAPaymentsWhereCustomerPaidKrToMerchant(String customerName, Integer amount, String merchantName) {
        Payment payment = new Payment(customers.get(customerName), amount, merchants.get(merchantName));
        assertTrue(payments.stream().anyMatch(p -> p.equals(payment)));
    }

    @When("the merchant initiates a payment for {int} kr using customer id {string}")
    public void theMerchantInitiatesAPaymentForKrUsingCustomerId(Integer amount, String customerId) {
        try {
            successful = dtupay.pay(amount, customerId, merchantId);
        } catch (Exception e) {
            successful = false;
            errorMessage = e.getMessage();
        }
    }

    @When("the merchant with id {string} initiates a payment for {int} kr using the customer")
    public void theMerchantWithIdInitiatesAPaymentForKrUsingTheCustomer(String merchantId, Integer amount) {
        try {
            successful = dtupay.pay(amount, customerId, merchantId);
        } catch (Exception e) {
            successful = false;
            errorMessage = e.getMessage();
        }
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertFalse(successful);
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage);
    }

}
