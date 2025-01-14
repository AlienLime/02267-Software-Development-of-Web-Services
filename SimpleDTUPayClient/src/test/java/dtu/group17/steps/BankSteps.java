package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;
import dtu.group17.SimpleDTUPay;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BankSteps {
    private SimpleDTUPay dtupay;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    BankService bankService = new BankServiceService().getBankServicePort();

    public BankSteps(SimpleDTUPay dtupay, Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.dtupay = dtupay;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Given("the customer is registered with the bank with an initial balance of {int} kr")
    public void theCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(holder.getCustomer().toUser(), BigDecimal.valueOf(balance));
        holder.getAccounts().put(holder.getCustomer().cpr(), accountId);
    }

    @Given("the merchant is registered with the bank with an initial balance of {int} kr")
    public void theMerchantIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(holder.getMerchant().toUser(), BigDecimal.valueOf(balance));
        holder.getAccounts().put(holder.getMerchant().cpr(), accountId);
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
