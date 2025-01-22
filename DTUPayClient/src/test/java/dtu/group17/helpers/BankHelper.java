package dtu.group17.helpers;

import dtu.group17.customer.Customer;
import dtu.group17.merchant.Merchant;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.MerchantAPI;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

import java.util.HashMap;
import java.util.Map;

public class BankHelper {
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;
    private BankService bankService = new BankServiceService().getBankServicePort();

    private Map<String, String> accounts = new HashMap<>(); // cpr -> account id

    public BankHelper(CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    public void clear() {
        accounts.clear();
    }

    public String createBankAccount(Customer customer, int balance) throws BankServiceException_Exception {
        String accountId = customerAPI.createBankAccount(customer, balance);
        accounts.put(customer.cpr(), accountId);
        return accountId;
    }

    public String createBankAccount(Merchant merchant, int balance) throws BankServiceException_Exception {
        String accountId = merchantAPI.createBankAccount(merchant, balance);
        accounts.put(merchant.cpr(), accountId);
        return accountId;
    }

    public Account getAccount(Customer customer) throws BankServiceException_Exception {
        String accountId = accounts.get(customer.cpr());
        return customerAPI.getAccount(accountId);
    }

    public Account getAccount(Merchant merchant) throws BankServiceException_Exception {
        String accountId = accounts.get(merchant.cpr());
        return merchantAPI.getAccount(accountId);
    }

    public String getAccountId(Customer customer) {
        return accounts.get(customer.cpr());
    }

    public String getAccountId(Merchant merchant) {
        return accounts.get(merchant.cpr());
    }

    public void retireAccounts() throws BankServiceException_Exception {
        for (String accountId : accounts.values()) {
            bankService.retireAccount(accountId);
        }
    }
}
