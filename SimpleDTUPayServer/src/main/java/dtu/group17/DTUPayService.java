package dtu.group17;

import java.util.*;

public class DTUPayService {

    private static Map<String, Customer> customers = new HashMap<>();
    private static Map<String, Merchant> merchants = new HashMap<>();
    private static List<Payment> payments = new ArrayList<>();

    public String register(Customer customer) {
        String id = UUID.randomUUID().toString();
        customers.put(id, customer);
        return id;
    }

    public String register(Merchant merchant) {
        String id = UUID.randomUUID().toString();
        merchants.put(id, merchant);
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

    public boolean createPayment(Payment payment) {
        if (!customers.containsKey(payment.customerId())) {
            throw new CustomerNotFound("customer with id \"" + payment.customerId() + "\" is unknown");
        }
        if (!merchants.containsKey(payment.merchantId())) {
            throw new MerchantNotFound("merchant with id \"" + payment.merchantId() + "\" is unknown");
        }
        return payments.add(payment);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void clearPayments(){
        payments.clear();
    }
}
