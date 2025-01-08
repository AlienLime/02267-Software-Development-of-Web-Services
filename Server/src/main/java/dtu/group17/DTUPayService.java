package dtu.group17;

import org.jboss.logging.Logger;

import java.util.*;

public class DTUPayService {
    private static final Logger LOG = Logger.getLogger(DTUPayService.class);

    Map<String, Customer> customers = new HashMap<>();
    Map<String, Merchant> merchants = new HashMap<>();
    List<Payment> payments = new ArrayList<>();

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
        LOG.info("Customer id: " + payment.customerId());
        LOG.info("Customers: " + String.join(", ", customers.keySet()));
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
