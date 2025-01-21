package dtu.group17.steps;

import dtu.group17.customer.Customer;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.BankHelper;
import dtu.group17.helpers.ErrorMessageHelper;
import dtu.group17.helpers.TokenHelper;
import dtu.group17.merchant.Merchant;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Given;

import java.util.UUID;

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
    @Given("a registered customer")
    public void aRegisteredCustomer() throws BankServiceException_Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = bankHelper.createBankAccount(customer, 10000000);
        accountHelper.registerCustomerWithDTUPay(customer, accountId);
    }

    @Given("a registered customer with {int} kr and {int} token\\(s)")
    public void aRegisteredCustomerWithKrAndTokens(Integer balance, Integer amountTokens) throws Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = bankHelper.createBankAccount(customer, balance);
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        tokenHelper.requestTokens(customer, amountTokens);
    }

    @Given("a registered customer with {int} token\\(s)")
    public void aRegisteredCustomerWithTokenS(Integer amountTokens) throws Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = bankHelper.createBankAccount(customer, 10000000);
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        if (amountTokens != 0) tokenHelper.requestTokens(customer, amountTokens);
    }

    @Given("a customer who is not registered with the bank")
    public void aCustomerWhoIsNotRegisteredWithTheBank() throws Exception {
        Customer customer = accountHelper.createCustomer();
        String accountId = UUID.randomUUID().toString();
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        tokenHelper.requestTokens(customer, 5);
    }
    //#endregion

    //#region Merchant steps
    @Given("a registered merchant")
    public void aRegisteredMerchant() throws BankServiceException_Exception {
        Merchant merchant = accountHelper.createMerchant();
        String accountId = bankHelper.createBankAccount(merchant, 100000);
        accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    @Given("a registered merchant with {int} kr")
    public void aRegisteredMerchantWithKr(Integer amount) throws BankServiceException_Exception {
        Merchant merchant = accountHelper.createMerchant();
        String accountId = bankHelper.createBankAccount(merchant, amount);
        accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    @Given("a merchant who is not registered with the bank")
    public void aMerchantWhoIsNotRegisteredWithTheBank() {
        Merchant merchant = accountHelper.createMerchant();
        String accountId = UUID.randomUUID().toString();
        accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }
    //#endregion
}
