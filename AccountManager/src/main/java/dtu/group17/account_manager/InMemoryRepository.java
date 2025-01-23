/*
 * Author: Katja Kaj (s123456)
 * Description:
 * A simple in-memory implementation of the CustomerRepository and MerchantRepository interfaces.
 * This is useful for testing the DTUPay system without needing to set up a database.
 */

package dtu.group17.account_manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository implements CustomerRepository, MerchantRepository {
    private Map<UUID, Customer> customers = new ConcurrentHashMap<>(); // DTUPay ID -> Customer
    private Map<UUID, Merchant> merchants = new ConcurrentHashMap<>(); // DTUPay ID -> Merchant

    @Override
    public Customer addCustomer(Customer customer) {
        customers.put(customer.id(), customer);
        return customer;
    }

    @Override
    public Merchant addMerchant(Merchant merchant) {
        merchants.put(merchant.id(), merchant);
        return merchant;
    }

    @Override
    public Customer getCustomerById(UUID id) {
        return customers.get(id);
    }

    @Override
    public Merchant getMerchantById(UUID id) {
        return merchants.get(id);
    }

    @Override
    public void removeCustomer(UUID id) {
        customers.remove(id);
    }

    @Override
    public void removeMerchant(UUID id) {
        merchants.remove(id);
    }

    @Override
    public void clearCustomers() {
        customers.clear();
    }

    @Override
    public void clearMerchants() {
        merchants.clear();
    }

}
