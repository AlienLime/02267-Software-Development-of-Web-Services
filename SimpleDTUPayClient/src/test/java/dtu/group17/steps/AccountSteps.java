package dtu.group17.steps;

import dtu.group17.*;
import io.cucumber.java.en.Given;

public class AccountSteps {
    private SimpleDTUPay dtupay;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    public AccountSteps (SimpleDTUPay dtupay, Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.dtupay = dtupay;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        holder.setCustomer(new Customer(firstName, lastName, cpr));
    }

    @Given("the customer is registered with DTU Pay using their bank account")
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

    @Given("the merchant is registered with DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithSimpleDTUPayUsingTheirBankAccount() {
        String accountId = holder.getAccounts().get(holder.getMerchant().cpr());
        holder.setMerchantId(dtupay.register(holder.getMerchant(), accountId));
        holder.getMerchants().put(holder.getMerchant().firstName(), holder.getMerchantId());
    }

}
