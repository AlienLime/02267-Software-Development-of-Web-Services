package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;
import dtu.group17.SimpleDTUPay;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class CucumberSteps {
    private SimpleDTUPay dtupay;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    BankService bankService = new BankServiceService().getBankServicePort();

    public CucumberSteps(SimpleDTUPay dtupay, Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.dtupay = dtupay;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Before
    public void before() {
        holder.setCustomer(null);
        holder.setMerchant(null);
        holder.setCustomerId(null);
        holder.setMerchantId(null);
        holder.setSuccessful(false);
        holder.setPayments(null);
        holder.getCustomers().clear();
        holder.getMerchants().clear();
        holder.getAccounts().clear();
        errorMessageHolder.setErrorMessage(null);
    }

    @After
    public void after() throws BankServiceException_Exception {
        holder.getCustomers().values().forEach(dtupay::deregisterCustomer);
        holder.getMerchants().values().forEach(dtupay::deregisterMerchant);
        dtupay.clearPayments();
        for (String accountId : holder.getAccounts().values()) {
            bankService.retireAccount(accountId);
        }
    }

}
