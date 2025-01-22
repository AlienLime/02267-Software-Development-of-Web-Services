/*
 * Author: Katja Kaj (s123456)
 * Description:
 *  This class is responsible for creating new customers and merchants with a unique ID.
 *
 * Notes:
 *   Example: Most of these functionalities were taken from the example project xxx.
 */

package dtu.group17;

import java.util.UUID;

public class AccountFactory {
    public Customer createCustomerWithID(Customer customer, String bankAccountId) {
        UUID id = UUID.randomUUID();
        return new Customer(id, bankAccountId, customer.firstName(), customer.lastName(), customer.cpr());
    }

    public Merchant createMerchantWithID(Merchant merchant, String bankAccountId) {
        UUID id = UUID.randomUUID();
        return new Merchant(id, bankAccountId, merchant.firstName(), merchant.lastName(), merchant.cpr());
    }
}
