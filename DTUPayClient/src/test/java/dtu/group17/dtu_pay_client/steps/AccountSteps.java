package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.customer.Customer;
import dtu.group17.dtu_pay_client.helpers.AccountHelper;
import dtu.group17.dtu_pay_client.helpers.BankHelper;
import dtu.group17.dtu_pay_client.helpers.ErrorMessageHelper;
import dtu.group17.dtu_pay_client.helpers.TokenHelper;
import dtu.group17.dtu_pay_client.merchant.Merchant;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.UUID;

import static org.wildfly.common.Assert.*;

public class AccountSteps {
    private ErrorMessageHelper errorMessageHelper;
    private AccountHelper accountHelper;
    private BankHelper bankHelper;
    private TokenHelper tokenHelper;

    public AccountSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper, BankHelper bankHelper, TokenHelper tokenHelper) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.bankHelper = bankHelper;
        this.tokenHelper = tokenHelper;
    }

    public Customer registerCustomer(Customer customer, int balance) throws BankServiceException_Exception {
        String accountId = bankHelper.createBankAccount(customer, balance);
        return accountHelper.registerCustomerWithDTUPay(customer, accountId);
    }

    public Merchant registerMerchant(Merchant merchant, int balance) throws BankServiceException_Exception {
        String accountId = bankHelper.createBankAccount(merchant, balance);
        return accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    //#region Customer steps
    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        accountHelper.setCurrentCustomer(new Customer(null, firstName, lastName, cpr));
    }

    @Given("the customer is registered with the bank")
    public void theCustomerIsRegisteredWithTheBank() throws BankServiceException_Exception {
        bankHelper.createBankAccount(accountHelper.getCurrentCustomer(), 1000000);
    }

    @Given("a registered customer")
    public void aRegisteredCustomer() throws BankServiceException_Exception {
        Customer customer = accountHelper.createCustomer();
        registerCustomer(customer, 1000000);
    }

    @Given("a registered customer with cpr {string}")
    public void aRegisteredCustomerWithCpr(String cpr) throws BankServiceException_Exception {
        Customer customer = new Customer(null, "DummyFirstName", "DummyLastName", cpr);
        registerCustomer(customer, 1000000);
    }

    @Given("a registered customer with a balance of {int} kr and {int} token\\(s)")
    public void aRegisteredCustomerWithABalanceOfKrAndTokens(Integer balance, Integer amountTokens) throws Exception {
        Customer customer = accountHelper.createCustomer();
        customer = registerCustomer(customer, balance);
        tokenHelper.requestTokens(customer, amountTokens);
    }

    @Given("a registered customer with {int} token\\(s)")
    public void aRegisteredCustomerWithTokenS(Integer amountTokens) throws Exception {
        Customer customer = accountHelper.createCustomer();
        customer = registerCustomer(customer, 1000000);
        if (amountTokens != 0) tokenHelper.requestTokens(customer, amountTokens);
    }

    @Given("a customer who is not registered with the bank")
    public void aCustomerWhoIsNotRegisteredWithTheBank() throws Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = UUID.randomUUID().toString();
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        tokenHelper.requestTokens(customer, 5);
    }

    @When("the customer tries to register with DTU Pay using their bank account")
    public void theCustomerTriesToRegisterWithDTUPayUsingTheirBankAccount() {
        Customer customer = accountHelper.getCurrentCustomer();
        accountHelper.registerCustomerWithDTUPay(customer, bankHelper.getAccountId(customer));
    }

    @When("the customer tries to deregister their account from DTU Pay")
    public void theCustomerTriesToDeregisterTheirAccountFromDTUPay() throws Exception {
        accountHelper.deregisterCustomerWithDTUPay(accountHelper.getCurrentCustomer());
    }

    @When("a customer with id {string} tries to deregister their account from DTU Pay")
    public void aCustomerWithIdTriesToDeregisterTheirAccountFromDTUPay(String id) {
        Customer customer = new Customer(UUID.fromString(id), "DummyFirstName", "DummyLastName", AccountHelper.randomCPR());
        try {
            accountHelper.deregisterCustomerWithDTUPay(customer);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    @Then("the customer is registered successfully and with the name {string}, last name {string}, and CPR {string}")
    public void theCustomerIsRegisteredSuccessfullyAndWithTheNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        List<Customer> customers = accountHelper.getCustomers();

        assertTrue(customers.stream().anyMatch(customer ->
            customer.firstName().equals(firstName)
            && customer.lastName().equals(lastName)
            && customer.cpr().equals(cpr)
        ));
    }

    @Then("the customer with cpr {string} is successfully deregistered")
    public void theCustomerWithCprIsSuccessfullyDeregistered(String cpr) {
        List<Customer> customers = accountHelper.getCustomers();

        assertTrue(accountHelper.getCustomerIsDeregistered());
        assertFalse(customers.stream().anyMatch(customer ->
            customer.cpr().equals(cpr)
        ));
    }

    @Then("the customer could not be deregistered")
    public void theCustomerCouldNotBeDeregistered() {
        assertFalse(accountHelper.getCustomerIsDeregistered());
    }
    //#endregion

    //#region Merchant steps
    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        accountHelper.setCurrentMerchant(new Merchant(null, firstName, lastName, cpr));
    }

    @Given("the merchant is registered with the bank")
    public void theMerchantIsRegisteredWithTheBank() throws BankServiceException_Exception {
        bankHelper.createBankAccount(accountHelper.getCurrentMerchant(), 1000000);
    }

    @Given("a registered merchant with a bank account")
    public void aRegisteredMerchantWithABankAccount() throws BankServiceException_Exception {
        Merchant merchant = accountHelper.createMerchant();
        registerMerchant(merchant, 1000000);
    }

    @Given("a registered merchant with cpr {string}")
    public void aRegisteredMerchantWithCpr(String cpr) throws BankServiceException_Exception {
        Merchant merchant = new Merchant(null, "DummyFirstName", "DummyLastName", cpr);
        registerMerchant(merchant, 1000000);
    }

    @Given("a registered merchant with a bank account and a balance of {int} kr")
    public void aRegisteredMerchantWithABankAccountAndABalanceOfKr(Integer balance) throws BankServiceException_Exception {
        Merchant merchant = accountHelper.createMerchant();
        registerMerchant(merchant, balance);
    }

    @Given("a merchant who is not registered with the bank")
    public void aMerchantWhoIsNotRegisteredWithTheBank() {
        Merchant merchant = accountHelper.createMerchant();
        String accountId = UUID.randomUUID().toString();
        accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    @When("the merchant tries to register with DTU Pay using their bank account")
    public void theMerchantTriesToRegisterWithDTUPayUsingTheirBankAccount() {
        Merchant merchant = accountHelper.getCurrentMerchant();
        accountHelper.registerMerchantWithDTUPay(merchant, bankHelper.getAccountId(merchant));
    }

    @When("the merchant tries to deregister their account from DTU Pay")
    public void theMerchantTriesToDeregisterTheirAccountFromDTUPay() throws Exception {
        accountHelper.deregisterMerchantWithDTUPay(accountHelper.getCurrentMerchant());
    }

    @When("a merchant with id {string} tries to deregister their account from DTU Pay")
    public void aMerchantWithIdTriesToDeregisterTheirAccountFromDTUPay(String id) {
        Merchant merchant = new Merchant(UUID.fromString(id), "DummyFirstName", "DummyLastName", AccountHelper.randomCPR());
        try {
            accountHelper.deregisterMerchantWithDTUPay(merchant);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    @Then("the merchant is registered successfully and with the name {string}, last name {string}, and CPR {string}")
    public void theMerchantIsRegisteredSuccessfullyAndWithTheNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        List<Merchant> merchants = accountHelper.getMerchants();

        assertTrue(merchants.stream().anyMatch(merchant ->
                merchant.firstName().equals(firstName)
                        && merchant.lastName().equals(lastName)
                        && merchant.cpr().equals(cpr)
        ));
    }

    @Then("the merchant with cpr {string} is successfully deregistered")
    public void theMerchantWithCprIsSuccessfullyDeregistered(String cpr) {
        List<Merchant> merchants = accountHelper.getMerchants();

        assertFalse(merchants.stream().anyMatch(merchant ->
                merchant.cpr().equals(cpr)
        ));
    }

    @Then("the merchant could not be deregistered")
    public void theMerchantCouldNotBeDeregistered() {
        assertFalse(accountHelper.getMerchantIsDeregistered());
    }
    //#endregion

}
