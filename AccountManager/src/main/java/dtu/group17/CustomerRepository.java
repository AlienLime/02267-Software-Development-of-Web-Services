package dtu.group17;

import java.util.UUID;

public interface CustomerRepository {

    Customer addCustomer(Customer customer);

    Customer getCustomerById(UUID id);

    void removeCustomer(UUID id);

}
