package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;
import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.Merchant;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BankSteps {
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    public BankSteps(Holder holder, ErrorMessageHolder errorMessageHolder, CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    @Given("the customer is registered with the bank with an initial balance of {int} kr")
    public void theCustomerIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer balance) throws BankServiceException_Exception {
        Customer customer = holder.getCustomer();
        String accountId = customerAPI.createBankAccount(customer, balance);
        holder.getAccounts().put(holder.getCustomer().cpr(), accountId);
    }

    @Given("the merchant is registered with the bank with an initial balance of {int} kr")
    public void theMerchantIsRegisteredWithTheBankWithAnInitialBalanceOfKr(Integer balance) throws BankServiceException_Exception {
        Merchant merchant = holder.getMerchant();
        String accountId = merchantAPI.createBankAccount(merchant, balance);
        holder.getAccounts().put(holder.getMerchant().cpr(), accountId);
    }

    @Then("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        String accountId = holder.getAccounts().get(holder.getCustomer().cpr());
        Account account = customerAPI.getBalance(accountId);
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

    @Then("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(Integer balance) throws BankServiceException_Exception {
        String accountId = holder.getAccounts().get(holder.getMerchant().cpr());
        Account account = merchantAPI.getBalance(accountId);
        assertEquals(BigDecimal.valueOf(balance), account.getBalance());
    }

}
