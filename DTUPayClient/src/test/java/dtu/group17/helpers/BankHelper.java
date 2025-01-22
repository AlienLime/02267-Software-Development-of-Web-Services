package dtu.group17.helpers;

import dtu.group17.customer.Customer;
import dtu.group17.merchant.Merchant;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BankHelper {
    private BankService bankService = new BankServiceService().getBankServicePort();

    private Map<String, String> accounts = new HashMap<>(); // cpr -> account id

    public BankHelper() {}

    public void clear() {
        accounts.clear();
    }

    public String createBankAccount(Customer customer, int balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(customer.toUser(), BigDecimal.valueOf(balance));
        accounts.put(customer.cpr(), accountId);
        return accountId;
    }

    public String createBankAccount(Merchant merchant, int balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(merchant.toUser(), BigDecimal.valueOf(balance));
        accounts.put(merchant.cpr(), accountId);
        return accountId;
    }

    public Account getAccount(Customer customer) throws BankServiceException_Exception {
        String accountId = accounts.get(customer.cpr());
        return bankService.getAccount(accountId);
    }

    public Account getAccount(Merchant merchant) throws BankServiceException_Exception {
        String accountId = accounts.get(merchant.cpr());
        return bankService.getAccount(accountId);
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
