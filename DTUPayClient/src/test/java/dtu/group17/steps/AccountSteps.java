package dtu.group17.steps;

import dtu.group17.customer.Customer;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.BankHelper;
import dtu.group17.helpers.ErrorMessageHelper;
import dtu.group17.helpers.TokenHelper;
import dtu.group17.merchant.Merchant;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Given;

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

    //#region Customer steps
    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        accountHelper.createCustomer();
    }

    @Given("a registered customer with {int} kr and {int} token\\(s)")
    public void aRegisteredCustomerWithKrAndTokens(Integer balance, Integer amountTokens) throws BankServiceException_Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = bankHelper.createBankAccount(customer, balance);
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        tokenHelper.requestTokens(customer, amountTokens);
    }

    @Given("a registered customer with {int} token\\(s)")
    public void aRegisteredCustomerWithTokenS(Integer amountTokens) throws BankServiceException_Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = bankHelper.createBankAccount(customer, 10000000);
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        tokenHelper.requestTokens(customer, amountTokens);
    }
    //#endregion

    //#region Merchant steps
    @Given("a registered merchant with {int} kr")
    public void aRegisteredMerchantWithKr(Integer amount) throws BankServiceException_Exception {
        Merchant merchant = accountHelper.createMerchant();
        String accountId = bankHelper.createBankAccount(merchant, amount);
        accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }
    //#endregion
}
