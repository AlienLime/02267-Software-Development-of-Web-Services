/*
 * Author: Benjamin Noah Lumbye (s204428)
 * Description:
 * Contains the step definitions for the account-related features of the DTUPay system.
 * Concerns registering and deregistering customers and merchants with DTUPay and the bank.
 */

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

import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

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

    /**
     * Register a customer with the bank and DTUPay.
     * @param customer The customer to register.
     * @param balance  The initial balance of the customer's bank account.
     * @throws BankServiceException_Exception
     * @author Emil Wraae Carlsen (s204458) 34
     */
    public Customer registerCustomer(Customer customer, int balance) throws BankServiceException_Exception {
        String accountId = bankHelper.createBankAccount(customer, balance);
        return accountHelper.registerCustomerWithDTUPay(customer, accountId);
    }

    /**
     * Register a merchant with the bank and DTUPay.
     * @param merchant The merchant to register.
     * @param balance  The initial balance of the merchant's bank account.
     * @return The registered merchant
     * @author Emil Kim Krarup (s204449)
     */
    public Merchant registerMerchant(Merchant merchant, int balance) throws BankServiceException_Exception {
        String accountId = bankHelper.createBankAccount(merchant, balance);
        return accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    //#region Customer steps
    /**
     * Create a new customer with a random CPR number and dummy first and last name.
     *
     * @param firstName The first name of the customer
     * @param lastName  The last name of the customer
     * @param cpr       The CPR number of the customer
     * @author Kristoffer Magnus Overgaard (s194110)
     */
    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        accountHelper.setCurrentCustomer(new Customer(null, firstName, lastName, cpr));
    }

    /**
     * Register a customer with the bank.
     *
     * @throws BankServiceException_Exception
     * @author Victor G. H. Rasmussen (s204475)
     */
    @Given("the customer is registered with the bank")
    public void theCustomerIsRegisteredWithTheBank() throws BankServiceException_Exception {
        bankHelper.createBankAccount(accountHelper.getCurrentCustomer(), 1000000);
    }

    /**
     * Create and register a customer with the bank and DTUPay with a random CPR
     * number.
     *
     * @throws BankServiceException_Exception
     * @author Emil Wraae Carlsen (s204458)
     */
    @Given("a registered customer")
    public void aRegisteredCustomer() throws BankServiceException_Exception {
        Customer customer = accountHelper.createCustomer();
        registerCustomer(customer, 1000000);
    }

    /**
     * Create and register a customer with the bank and DTUPay with a specific CPR
     * number.
     * @param cpr The CPR number of the customer
     * @throws BankServiceException_Exception
     * @author Kristoffer Magnus Overgaard (s194110)
     */
    @Given("a registered customer with cpr {string}")
    public void aRegisteredCustomerWithCpr(String cpr) throws BankServiceException_Exception {
        Customer customer = new Customer(null, "DummyFirstName", "DummyLastName", cpr);
        registerCustomer(customer, 1000000);
    }

    /**
     * Create and register a customer with the bank and DTUPay with a specific
     * balance and specific number of tokens.
     * @param balance      The initial balance of the customer's bank account
     * @param amountTokens The number of tokens the customer should have
     * @author Emil Kim Krarup (s204449)
     */
    @Given("a registered customer with a balance of {int} kr and {int} token\\(s)")
    public void aRegisteredCustomerWithABalanceOfKrAndTokens(Integer balance, Integer amountTokens) throws Exception {
        Customer customer = accountHelper.createCustomer();
        customer = registerCustomer(customer, balance);
        tokenHelper.requestTokens(customer, amountTokens);
    }


    /**
     * Create and register a customer with the bank and DTUPay with a specific
     * number of tokens.
     * @param amountTokens The number of tokens the customer should have
     * @author Stine Lund Madsen (s204425)
     */
    @Given("a registered customer with {int} token\\(s)")
    public void aRegisteredCustomerWithTokenS(Integer amountTokens) throws Exception {
        Customer customer = accountHelper.createCustomer();
        customer = registerCustomer(customer, 1000000);
        if (amountTokens != 0) tokenHelper.requestTokens(customer, amountTokens);
    }

    /**
     * Create a customer who is not registered with the bank.
     * @author Victor G. H. Rasmussen (s204475)
     */
    @Given("a customer who is not registered with the bank")
    public void aCustomerWhoIsNotRegisteredWithTheBank() throws Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = UUID.randomUUID().toString();
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        tokenHelper.requestTokens(customer, 5);
    }

    /**
     * Register the current customer with DTUPay using their given bank account.
     * @author Benjamin Noah Lumbye (s204428)
     */
    @When("the customer tries to register with DTU Pay using their bank account")
    public void theCustomerTriesToRegisterWithDTUPayUsingTheirBankAccount() {
        Customer customer = accountHelper.getCurrentCustomer();
        accountHelper.registerCustomerWithDTUPay(customer, bankHelper.getAccountId(customer));
    }

    /**
     * Deregister the current customer from DTUPay.
     * @author Kristoffer Magnus Overgaard (s194110)
     */
    @When("the customer tries to deregister their account from DTU Pay")
    public void theCustomerTriesToDeregisterTheirAccountFromDTUPay() throws Exception {
        accountHelper.deregisterCustomerWithDTUPay(accountHelper.getCurrentCustomer());
    }

    /**
     * A customer with a specific ID tries to deregister their account from DTUPay.
     * @param id The ID of the customer
     *           @author Emil Wraae Carlsen (s204458)
     */
    @When("a customer with id {string} tries to deregister their account from DTU Pay")
    public void aCustomerWithIdTriesToDeregisterTheirAccountFromDTUPay(String id) {
        Customer customer = new Customer(UUID.fromString(id), "DummyFirstName", "DummyLastName", AccountHelper.randomCPR());
        try {
            accountHelper.deregisterCustomerWithDTUPay(customer);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    /**
     * Check that the customer is registered with the given name, last name, and CPR
     * @param firstName The first name of the customer
     * @param lastName The last name of the customer
     * @param cpr The CPR number of the customer
     * @author Victor G. H. Rasmussen (s204475)
     */
    @Then("the customer is registered successfully and with the name {string}, last name {string}, and CPR {string}")
    public void theCustomerIsRegisteredSuccessfullyAndWithTheNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        List<Customer> customers = accountHelper.getCustomers();

        assertTrue(customers.stream().anyMatch(customer ->
            customer.firstName().equals(firstName)
            && customer.lastName().equals(lastName)
            && customer.cpr().equals(cpr)
        ));
    }

    /**
     * Check that the customer with the given CPR number is deregistered.
     * @param cpr The CPR number of the customer
     * @author Emil Kim Krarup (s204449)
     */
    @Then("the customer with cpr {string} is successfully deregistered")
    public void theCustomerWithCprIsSuccessfullyDeregistered(String cpr) {
        List<Customer> customers = accountHelper.getCustomers();

        assertTrue(accountHelper.getCustomerIsDeregistered());
        assertFalse(customers.stream().anyMatch(customer ->
            customer.cpr().equals(cpr)
        ));
    }

    /**
     * Check that the customer could not be deregistered.
     * @author Emil Wraae Carlsen (s204458)
     */
    @Then("the customer could not be deregistered")
    public void theCustomerCouldNotBeDeregistered() {
        assertFalse(accountHelper.getCustomerIsDeregistered());
    }
    //#endregion

    //#region Merchant steps
    /**
     * Create a new merchant with a specific CPR number, first and last name.
     *
     * @param firstName The first name of the merchant
     * @param lastName  The last name of the merchant
     * @param cpr       The CPR number of the merchant
     * @author Kristoffer Magnus Overgaard (s194110)
     */
    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        accountHelper.setCurrentMerchant(new Merchant(null, firstName, lastName, cpr));
    }

    /**
     * Register the current merchant with the bank.
     * @throws BankServiceException_Exception
     * @author Emil Wraae Carlsen (s204458)
     */
    @Given("the merchant is registered with the bank")
    public void theMerchantIsRegisteredWithTheBank() throws BankServiceException_Exception {
        bankHelper.createBankAccount(accountHelper.getCurrentMerchant(), 1000000);
    }

    /**
     * Create and register a merchant with the bank and DTUPay with a random CPR
     * number.
     * Uses the default balance of 1000000.
     * @throws BankServiceException_Exception
     * @author Kristoffer Magnus Overgaard (s194110)
     */
    @Given("a registered merchant with a bank account")
    public void aRegisteredMerchantWithABankAccount() throws BankServiceException_Exception {
        Merchant merchant = accountHelper.createMerchant();
        registerMerchant(merchant, 1000000);
    }

    /**
     * Create and register a merchant with the bank and DTUPay with a specific CPR
     * number.
     *
     * @param cpr The CPR number of the merchant
     * @throws BankServiceException_Exception
     * @author Emil Wraae Carlsen (s204458)
     */
    @Given("a registered merchant with cpr {string}")
    public void aRegisteredMerchantWithCpr(String cpr) throws BankServiceException_Exception {
        Merchant merchant = new Merchant(null, "DummyFirstName", "DummyLastName", cpr);
        registerMerchant(merchant, 1000000);
    }

    /**
     * Create and register a merchant with the bank and DTUPay with a specific
     * balance.
     * @param balance The initial balance of the merchant's bank account
     * @throws BankServiceException_Exception
     * @author Emil Kim Krarup (s204449)
     */
    @Given("a registered merchant with a bank account and a balance of {int} kr")
    public void aRegisteredMerchantWithABankAccountAndABalanceOfKr(Integer balance) throws BankServiceException_Exception {
        Merchant merchant = accountHelper.createMerchant();
        registerMerchant(merchant, balance);
    }

    /**
     * Create a merchant with an incorrect bank account ID and register them with
     * DTUPay.
     * @author Victor G. H. Rasmussen (s204475)
     */
    @Given("a merchant who is not registered with the bank")
    public void aMerchantWhoIsNotRegisteredWithTheBank() {
        Merchant merchant = accountHelper.createMerchant();
        String accountId = UUID.randomUUID().toString();
        accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    /**
     * Register the current merchant with DTUPay using their given bank account.
     * @author Emil Wraae Carlsen (s204458)
     */
    @When("the merchant tries to register with DTU Pay using their bank account")
    public void theMerchantTriesToRegisterWithDTUPayUsingTheirBankAccount() {
        Merchant merchant = accountHelper.getCurrentMerchant();
        accountHelper.registerMerchantWithDTUPay(merchant, bankHelper.getAccountId(merchant));
    }

    /**
     * Deregister the current merchant from DTUPay.
     * @author Emil Kim Krarup (s204449)
     */
    @When("the merchant tries to deregister their account from DTU Pay")
    public void theMerchantTriesToDeregisterTheirAccountFromDTUPay() throws Exception {
        accountHelper.deregisterMerchantWithDTUPay(accountHelper.getCurrentMerchant());
    }

    /**
     * A merchant with a specific ID tries to deregister their account from DTUPay.
     * @param id The ID of the merchant
     * @author Emil Kim Krarup (s204449)
     */
    @When("a merchant with id {string} tries to deregister their account from DTU Pay")
    public void aMerchantWithIdTriesToDeregisterTheirAccountFromDTUPay(String id) {
        Merchant merchant = new Merchant(UUID.fromString(id), "DummyFirstName", "DummyLastName", AccountHelper.randomCPR());
        try {
            accountHelper.deregisterMerchantWithDTUPay(merchant);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    /**
     * Check that the merchant is registered with the given name, last name, and CPR
     * @param firstName The first name of the merchant
     * @param lastName The last name of the merchant
     * @param cpr The CPR number of the merchant
     * @author  Stine Lund Madsen (s204425)
     */
    @Then("the merchant is registered successfully and with the name {string}, last name {string}, and CPR {string}")
    public void theMerchantIsRegisteredSuccessfullyAndWithTheNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        List<Merchant> merchants = accountHelper.getMerchants();

        assertTrue(merchants.stream().anyMatch(merchant ->
                merchant.firstName().equals(firstName)
                        && merchant.lastName().equals(lastName)
                        && merchant.cpr().equals(cpr)
        ));
    }

    /**
     * Check that the merchant with the given CPR number is deregistered (not among the registered merchants).
     * @param cpr The CPR number of the merchant
     * @author Benjamin Noah Lumbye (s204428)
     */
    @Then("the merchant with cpr {string} is successfully deregistered")
    public void theMerchantWithCprIsSuccessfullyDeregistered(String cpr) {
        List<Merchant> merchants = accountHelper.getMerchants();

        assertFalse(merchants.stream().anyMatch(merchant ->
                merchant.cpr().equals(cpr)
        ));
    }

    /**
     * Check that the merchant could not be deregistered.
     * @author Victor G. H. Rasmussen (s204475)
     */
    @Then("the merchant could not be deregistered")
    public void theMerchantCouldNotBeDeregistered() {
        assertFalse(accountHelper.getMerchantIsDeregistered());
    }
    //#endregion

}
