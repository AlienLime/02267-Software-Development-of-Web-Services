package dtu.group17;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class BankPaymentSteps {
    private SimpleDTUPay dtupay;
    private BankService bankService;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    public BankPaymentSteps(SimpleDTUPay dtupay, BankService bankService, Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.dtupay = dtupay;
        this.bankService = bankService;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }

  /*  @Before
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
        holder.getAccounts().values().forEach(bankService::retireAccount);
    }*/

    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        holder.setCustomer(new Customer(firstName, lastName, cpr));
    }

    @Given("the customer is registered with the bank with an initial balance of {int} kr")
    public void theCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(holder.getCustomer().toUser(), BigDecimal.valueOf(balance));
        holder.getAccounts().put(holder.getCustomer().cpr(), accountId);
    }

    @Given("the customer is registered with Simple DTU Pay using their bank account")
    public void theCustomerIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
        String accountId = holder.getAccounts().get(holder.getCustomer().cpr());
        holder.setCustomerId(dtupay.register(holder.getCustomer(), accountId));
    }

    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        holder.setMerchant(new Merchant(firstName, lastName, cpr));
    }

    @Given("the merchant is registered with the bank with an initial balance of {int} kr")
    public void theMerchantIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(holder.getMerchant().toUser(), BigDecimal.valueOf(balance));
        holder.getAccounts().put(holder.getMerchant().cpr(), accountId);
    }

    @Given("the merchant is registered with Simple DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
        String accountId = holder.getAccounts().get(holder.getMerchant().cpr());
        holder.setMerchantId(dtupay.register(holder.getMerchant(), accountId));
    }

    @Then("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        String accountId = holder.getAccounts().get(holder.getCustomer().cpr());
        Account account = bankService.getAccount(accountId);
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

    @Then("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        String accountId = holder.getAccounts().get(holder.getMerchant().cpr());
        Account account = bankService.getAccount(accountId);
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

}
