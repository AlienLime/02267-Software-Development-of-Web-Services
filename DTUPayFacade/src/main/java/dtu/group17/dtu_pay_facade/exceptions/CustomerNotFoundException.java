/*
 * Author: Kristoffer Magnus Overgaard (s194110)
 * Description:
 * Describes a custom exception for when a customer is not found.
 */

package dtu.group17.dtu_pay_facade.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
