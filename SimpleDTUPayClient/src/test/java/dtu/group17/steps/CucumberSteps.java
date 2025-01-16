package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;
import dtu.group17.SimpleDTUPay;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.MerchantAPI;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class CucumberSteps {
    private SimpleDTUPay dtupay;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    BankService bankService = new BankServiceService().getBankServicePort();

    public CucumberSteps(SimpleDTUPay dtupay, Holder holder, ErrorMessageHolder errorMessageHolder, CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.dtupay = dtupay;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    @Before
    public void before() {
        holder.setCustomer(null);
        holder.setMerchant(null);
        holder.setCustomerId(null);
        holder.setMerchantId(null);
        holder.setSuccessful(false);
        holder.getCustomers().clear();
        holder.getMerchants().clear();
        holder.getAccounts().clear();
        errorMessageHolder.setErrorMessage(null);
    }

    @After
    public void after() throws BankServiceException_Exception {
        for (String accountId : holder.getAccounts().values()) {
            bankService.retireAccount(accountId);
        }
        holder.getCustomers().values().forEach(customerAPI::deregister);
        holder.getMerchants().values().forEach(merchantAPI::deregister);
        dtupay.clearPayments();
    }

}
