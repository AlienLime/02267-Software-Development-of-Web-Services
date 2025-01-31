/*
 * Author: Emil Wraae Carlsen (s204458)
 * Description:
 * A simple in-memory implementation of the CustomerRepository and MerchantRepository interfaces.
 * This is useful for testing the DTUPay system without needing to set up a database.
 */

package dtu.group17.account_manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository implements CustomerRepository, MerchantRepository {
    private Map<UUID, Customer> customers = new ConcurrentHashMap<>(); // DTUPay ID -> Customer
    private Map<UUID, Merchant> merchants = new ConcurrentHashMap<>(); // DTUPay ID -> Merchant

    @Override
    public Customer addCustomer(Customer customer) {
        return customers.put(customer.id(), customer);
    }

    @Override
    public Merchant addMerchant(Merchant merchant) {
        return merchants.put(merchant.id(), merchant);
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
    public Customer removeCustomer(UUID id) {
        return customers.remove(id);
    }

    @Override
    public Merchant removeMerchant(UUID id) {
        return merchants.remove(id);
    }

    @Override
    public List<Customer> getCustomers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public List<Merchant> getMerchants() {
        return new ArrayList<>(merchants.values());
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
