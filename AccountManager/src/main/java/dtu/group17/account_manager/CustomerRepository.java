/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Defines the methods the customer repository needs (adding, retrieving and removing customers).
 */

package dtu.group17.account_manager;

import java.util.UUID;

public interface CustomerRepository {

    Customer addCustomer(Customer customer);

    Customer getCustomerById(UUID id);

    Customer removeCustomer(UUID id);

    void clearCustomers();

}
