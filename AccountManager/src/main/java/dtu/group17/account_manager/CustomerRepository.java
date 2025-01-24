/*
 * Author: Benjamin Noah Lumbye (s204428)
 * Description:
 * Defines the methods the customer repository needs (adding, retrieving and removing customers).
 */

package dtu.group17.account_manager;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository {

    Customer addCustomer(Customer customer);

    Customer getCustomerById(UUID id);

    Customer removeCustomer(UUID id);

    List<Customer> getCustomers();

    void clearCustomers();

}
