/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Defines the methods the customer repository needs (adding, retrieving and removing customers).
 */

package dtu.group17;

import java.util.UUID;

public interface CustomerRepository {

    Customer addCustomer(Customer customer);

    Customer getCustomerById(UUID id);

    void removeCustomer(UUID id);

    void clearCustomers();

}
