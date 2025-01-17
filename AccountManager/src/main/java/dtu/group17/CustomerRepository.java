package dtu.group17;

import java.util.UUID;

public interface CustomerRepository {
    public Customer addCustomer(Customer customer);

    public Customer getCustomerById(UUID id);
}
