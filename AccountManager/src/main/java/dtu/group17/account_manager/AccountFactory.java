/*
 * Author: Victor G. H. Rasmussen (s204475)
 * Description:
 *  This class is responsible for creating new customers and merchants in the DTUPay app given they have a bank account.
 *
 */

package dtu.group17.account_manager;

import java.util.UUID;

public class AccountFactory {
    /** Constructor for creating a new customer with a unique ID. Assigns a unique ID.
     * @param customer: The customer to be created.
     * @param bankAccountId: The bank account ID of the customer.
     * @author Victor G. H. Rasmussen (s204475)
     */
    public synchronized Customer createCustomerWithID(Customer customer, String bankAccountId) {
        UUID id = UUID.randomUUID();
        return new Customer(id, bankAccountId, customer.firstName(), customer.lastName(), customer.cpr());
    }

    /** Constructor used when signing up for DTU Pay with a back account. Assigns a unique ID.
     * @param merchant: The merchant to be created.
     * @param bankAccountId: The bank account ID of the merchant.
     * @author Emil Kim Krarup (s204449)
     */
    public synchronized Merchant createMerchantWithID(Merchant merchant, String bankAccountId) {
        UUID id = UUID.randomUUID();
        return new Merchant(id, bankAccountId, merchant.firstName(), merchant.lastName(), merchant.cpr());
    }
}
