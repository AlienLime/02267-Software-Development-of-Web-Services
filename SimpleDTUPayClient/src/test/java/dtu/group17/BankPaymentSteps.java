package dtu.group17;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class BankPaymentSteps {
    private SimpleDTUPay dtupay;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    BankServiceService bankServiceService = new BankServiceService();
    BankService bankService = bankServiceService.getBankServicePort();

    public BankPaymentSteps(SimpleDTUPay dtupay, Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.dtupay = dtupay;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Before
    public void before() {
        holder.getAccounts().clear();
    }

    @After
    public void after() throws BankServiceException_Exception {
        for (String accountId : holder.getAccounts().values()) {
            bankService.retireAccount(accountId);
        }
    }

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
        String customerId = dtupay.register(holder.getCustomer(), accountId);
        holder.setCustomerId(customerId);
        holder.getCustomers().put(holder.getCustomer().firstName(), customerId);
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
        holder.getMerchants().put(holder.getMerchant().firstName(), holder.getMerchantId());
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
