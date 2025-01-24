/*
 * Author: Emil Wraae Carlsen (s204458)
 * Description:
 * The steps are used to check the balance of the customer and merchant at the bank.
 */


package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.helpers.AccountHelper;
import dtu.group17.dtu_pay_client.helpers.BankHelper;
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

    /**
     * Asserts that the bank balance of the customer the expected balance.
     * @param balance The expected balance of the customer.
     * @throws BankServiceException_Exception
     * @author Emil Wraae Carlsen (s204458)
     */
    @Then("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        Account account = bankHelper.getAccount(accountHelper.getCurrentCustomer());
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

    /**
     * Asserts that the bank balance of the merchant the expected balance.
     * @param balance The expected balance of the merchant.
     * @throws BankServiceException_Exception
     * @author Emil Wraae Carlsen (s204458)
     */
    @Then("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        Account account = bankHelper.getAccount(accountHelper.getCurrentMerchant());
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

}
