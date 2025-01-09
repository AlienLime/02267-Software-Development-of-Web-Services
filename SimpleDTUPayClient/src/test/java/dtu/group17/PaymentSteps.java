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
    private SimpleDTUPay dtupay;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    public PaymentSteps(SimpleDTUPay dtupay, Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.dtupay = dtupay;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Before
    public void before() {
        holder.setCustomer(null);
        holder.setMerchant(null);
        holder.setCustomerId(null);
        holder.setMerchantId(null);
        holder.setSuccessful(false);
        holder.setPayments(null);
        holder.setCustomers(new HashMap<>());
        holder.setMerchants(new HashMap<>());
        errorMessageHolder.setErrorMessage(null);
    }

    @After
    public void after() {
        holder.getCustomers().values().forEach(dtupay::deregisterCustomer);
        holder.getMerchants().values().forEach(dtupay::deregisterMerchant);
        dtupay.clearPayments();
    }

    @Given("a customer with name {string}")
    public void aCustomerWithName(String name) {
        holder.setCustomer(new Customer(name,null,null));
    }

    @Given("the customer is registered with Simple DTU Pay")
    public void theCustomerIsRegisteredWithSimpleDTUPay() {
        holder.setCustomerId(dtupay.register(holder.getCustomer()));
    }

    @Given("a merchant with name {string}")
    public void aMerchantWithName(String name) {
        holder.setMerchant(new Merchant(name,null,null));
    }

    @Given("the merchant is registered with Simple DTU Pay")
    public void theMerchantIsRegisteredWithSimpleDTUPay() {
        holder.setMerchantId(dtupay.register(holder.getMerchant()));
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(Integer amount) {
        try {
            holder.setSuccessful(dtupay.pay(amount, holder.getCustomerId(), holder.getMerchantId()));
        } catch (Exception e) {
            holder.setSuccessful(false);
            errorMessageHolder.setErrorMessage(e.getMessage());
        }
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(holder.isSuccessful());
    }

    @Given("a customer with name {string}, who is registered with Simple DTU Pay")
    public void aCustomerWithNameWhoIsRegisteredWithSimpleDTUPay(String name) {
        holder.setCustomer(new Customer(name,null,null));
        holder.setCustomerId(dtupay.register(holder.getCustomer()));
        holder.getCustomers().put(holder.getCustomer().firstName(), holder.getCustomerId());
    }

    @Given("a merchant with name {string}, who is registered with Simple DTU Pay")
    public void aMerchantWithNameWhoIsRegisteredWithSimpleDTUPay(String name) {
        holder.setMerchant(new Merchant(name, null, null));
        holder.setMerchantId(dtupay.register(holder.getMerchant()));
        holder.getMerchants().put(holder.getMerchant().firstName(), holder.getMerchantId());
    }

    @Given("a successful payment of {int} kr from the customer to the merchant")
    public void aSuccessfulPaymentOfKrFromTheCustomerToTheMerchant(Integer amount) {
        try {
            holder.setSuccessful(dtupay.pay(amount, holder.getCustomerId(), holder.getMerchantId()));
        } catch (Exception e) {
            holder.setSuccessful(false);
            errorMessageHolder.setErrorMessage(e.getMessage());
        }
    }

    @When("the manager asks for a list of payments")
    public void theManagerAsksForAListOfPayments() {
        holder.setPayments(dtupay.getPayments());
    }

    @Then("the list contains a payments where customer {string} paid {int} kr to merchant {string}")
    public void theListContainsAPaymentsWhereCustomerPaidKrToMerchant(String customerName, Integer amount, String merchantName) {
        Payment payment = new Payment(holder.getCustomers().get(customerName), amount, holder.getMerchants().get(merchantName));
        assertTrue(holder.getPayments().stream().anyMatch(p -> p.equals(payment)));
    }

    @When("the merchant initiates a payment for {int} kr using customer id {string}")
    public void theMerchantInitiatesAPaymentForKrUsingCustomerId(Integer amount, String customerId) {
        try {
            holder.setSuccessful(dtupay.pay(amount, customerId, holder.getMerchantId()));
        } catch (Exception e) {
            holder.setSuccessful(false);
            errorMessageHolder.setErrorMessage(e.getMessage());
        }
    }

    @When("the merchant with id {string} initiates a payment for {int} kr using the customer")
    public void theMerchantWithIdInitiatesAPaymentForKrUsingTheCustomer(String merchantId, Integer amount) {
        try {
            holder.setSuccessful(dtupay.pay(amount, holder.getCustomerId(), merchantId));
        } catch (Exception e) {
            holder.setSuccessful(false);
            errorMessageHolder.setErrorMessage(e.getMessage());
        }
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertFalse(holder.isSuccessful());
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String errorMessage) {
        assertEquals(errorMessage, errorMessageHolder.getErrorMessage());
    }

}
