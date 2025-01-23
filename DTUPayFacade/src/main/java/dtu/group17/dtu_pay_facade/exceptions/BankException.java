/*
    * This exception is thrown when the bank is unable to process a request.
 */

package dtu.group17.dtu_pay_facade.exceptions;

public class BankException extends RuntimeException {
    public BankException(String message) {
        super(message);
    }
}
