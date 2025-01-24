/**
 * Author: Emil Kim Krarup (s204449)
 * Description:
 * Helper class for interacting with the bank service.
 */

package dtu.group17.dtu_pay_client.helpers;

import dtu.group17.dtu_pay_client.customer.Customer;
import dtu.group17.dtu_pay_client.merchant.Merchant;
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

    /**
     * Create a bank account for a customer with a given balance.
     * @param customer The customer for whom the account should be created.
     * @param balance The initial balance of the account.
     * @throws BankServiceException_Exception
     * @author Emil Kim Krarup (s204449)
     */
    public String createBankAccount(Customer customer, int balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(customer.toUser(), BigDecimal.valueOf(balance));
        accounts.put(customer.cpr(), accountId);
        return accountId;
    }

    /**
     * Create a bank account for a merchant with a given balance.
     * @param merchant The merchant for whom the account should be created.
     * @param balance The initial balance of the account.
     * @throws BankServiceException_Exception
     * @author Emil Kim Krarup (s204449)
     */
    public String createBankAccount(Merchant merchant, int balance) throws BankServiceException_Exception {
        String accountId = bankService.createAccountWithBalance(merchant.toUser(), BigDecimal.valueOf(balance));
        accounts.put(merchant.cpr(), accountId);
        return accountId;
    }

    /**
     * Get the bank account of a customer.
     * @param customer The customer for whom the account should be retrieved.
     * @throws BankServiceException_Exception
     * @author Kristoffer Magnus Overgaard (s194110)
     */
    public Account getAccount(Customer customer) throws BankServiceException_Exception {
        String accountId = accounts.get(customer.cpr());
        return bankService.getAccount(accountId);
    }

    /**
     * Get the bank account of a merchant.
     * @param merchant The merchant for whom the account should be retrieved.
     * @throws BankServiceException_Exception
     * @author Stine Lund Madsen (s204425)
     */
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
