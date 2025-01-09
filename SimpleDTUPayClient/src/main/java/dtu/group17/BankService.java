package dtu.group17;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;

import java.math.BigDecimal;

public class BankService implements dtu.ws.fastmoney.BankService {

    @Override
    public Account getAccount(String accountId) throws BankServiceException_Exception {
        return null;
    }

    @Override
    public String createAccountWithBalance(User user, BigDecimal balance) throws BankServiceException_Exception {
        return "";
    }

    @Override
    public Account getAccountByCprNumber(String cpr) throws BankServiceException_Exception {
        return null;
    }

    @Override
    public void retireAccount(String accountId) throws BankServiceException_Exception {

    }

    @Override
    public void transferMoneyFromTo(String debtor, String creditor, BigDecimal amount, String description) throws BankServiceException_Exception {

    }
}
