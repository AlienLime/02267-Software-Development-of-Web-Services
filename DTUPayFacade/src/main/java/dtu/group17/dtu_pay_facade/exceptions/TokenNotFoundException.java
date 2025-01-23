/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Describes a custom exception for when a token cannot be found.
 */

package dtu.group17.dtu_pay_facade.exceptions;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
