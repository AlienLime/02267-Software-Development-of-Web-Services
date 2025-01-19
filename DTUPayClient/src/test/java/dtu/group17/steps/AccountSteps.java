package dtu.group17.steps;

import dtu.group17.*;
import dtu.group17.customer.Customer;
import dtu.group17.merchant.Merchant;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.MerchantAPI;
import io.cucumber.java.en.Given;

import java.util.UUID;

public class AccountSteps {
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    public AccountSteps(Holder holder, ErrorMessageHolder errorMessageHolder, CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        holder.setCustomer(new Customer(null, firstName, lastName, cpr));
    }

    @Given("the customer is registered with DTU Pay using their bank account")
    public void theCustomerIsRegisteredWithDTUPayUsingTheirBankAccount() {
        String accountId = holder.getAccounts().get(holder.getCustomer().cpr());
        Customer customer = customerAPI.register(holder.getCustomer(), accountId);
        holder.setCustomerId(customer.id());
        holder.getCustomers().put(holder.getCustomer().firstName(), customer.id());
    }

    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        holder.setMerchant(new Merchant(null, firstName, lastName, cpr));
    }

    @Given("the merchant is registered with DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithDTUPayUsingTheirBankAccount() {
        String accountId = holder.getAccounts().get(holder.getMerchant().cpr());
        Merchant merchant = merchantAPI.register(holder.getMerchant(), accountId);
        holder.setMerchantId(merchant.id());
        holder.getMerchants().put(holder.getMerchant().firstName(), merchant.id());
    }

}
