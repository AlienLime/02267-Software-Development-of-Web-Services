package dtu.group17;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.*;

@Singleton
public class DTUPayService {
    private Map<String, Customer> customers = new HashMap<>(); // customer id -> customer
    private Map<String, Merchant> merchants = new HashMap<>(); // merchant id -> merchant
    private Map<String, String> accounts = new HashMap<>(); // customer/merchant id -> bank account id
    private List<Payment> payments = new ArrayList<>();

    BankServiceService bankServiceService = new BankServiceService();
    BankService bankService = bankServiceService.getBankServicePort();

    public String register(Customer customer, String bankAccountId) {
        String id = UUID.randomUUID().toString();
        customers.put(id, customer);
        accounts.put(id, bankAccountId);
        return id;
    }

    public String register(Merchant merchant, String accountId) {
        String id = UUID.randomUUID().toString();
        merchants.put(id, merchant);
        accounts.put(id, accountId);
        return id;
    }

    public boolean deregisterCustomer(String id) {
        try {
            customers.remove(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deregisterMerchant(String id) {
        try {
            merchants.remove(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean createPayment(Payment payment) throws BankServiceException_Exception {
        if (!customers.containsKey(payment.customerId())) {
            throw new CustomerNotFound("customer with id \"" + payment.customerId() + "\" is unknown");
        }
        if (!merchants.containsKey(payment.merchantId())) {
            throw new MerchantNotFound("merchant with id \"" + payment.merchantId() + "\" is unknown");
        }
        String customerName = customers.get(payment.customerId()).firstName();
        String merchantName = merchants.get(payment.merchantId()).firstName();
        String description = "Group 17 - transfer of " + payment.amount() + " kr. from " + customerName + " to " + merchantName;
        bankService.transferMoneyFromTo(accounts.get(payment.customerId()), accounts.get(payment.merchantId()), BigDecimal.valueOf(payment.amount()), description);
        return payments.add(payment);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void clearPayments(){
        payments.clear();
    }
}
