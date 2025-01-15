package dtu.group17.steps;

import dtu.group17.*;
import dtu.group17.customer.Customer;
import dtu.group17.merchant.Merchant;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.MerchantAPI;
import io.cucumber.java.en.Given;

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
        holder.setCustomer(new Customer(firstName, lastName, cpr));
    }

    @Given("the customer is registered with DTU Pay using their bank account")
    public void theCustomerIsRegisteredWithDTUPayUsingTheirBankAccount() {
        String accountId = holder.getAccounts().get(holder.getCustomer().cpr());
        String customerId = customerAPI.register(holder.getCustomer(), accountId);
        holder.setCustomerId(customerId);
        holder.getCustomers().put(holder.getCustomer().firstName(), customerId);
    }

    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        holder.setMerchant(new Merchant(firstName, lastName, cpr));
    }

    @Given("the merchant is registered with DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithDTUPayUsingTheirBankAccount() {
        String accountId = holder.getAccounts().get(holder.getMerchant().cpr());
        String merchantId = merchantAPI.register(holder.getMerchant(), accountId);
        holder.setMerchantId(merchantId);
        holder.getMerchants().put(holder.getMerchant().firstName(), merchantId);
    }

}
