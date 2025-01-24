/*
 * Author: Victor G. H. Rasmussen (s204475)
 * Description:
 * Describes a custom exception for when a merchant is not found.
 */

package dtu.group17.dtu_pay_facade.exceptions;

public class MerchantNotFoundException extends RuntimeException {
    public MerchantNotFoundException(String message) {
        super(message);
    }
}
