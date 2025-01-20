package dtu.group17.steps;

import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.BankHelper;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Then;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BankSteps {
    private AccountHelper accountHelper;
    private BankHelper bankHelper;

    public BankSteps(AccountHelper accountHelper, BankHelper bankHelper) {
        this.accountHelper = accountHelper;
        this.bankHelper = bankHelper;
    }

    @Then("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        Account account = bankHelper.getBalance(accountHelper.getCurrentCustomer());
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

    @Then("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        Account account = bankHelper.getBalance(accountHelper.getCurrentMerchant());
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

}
