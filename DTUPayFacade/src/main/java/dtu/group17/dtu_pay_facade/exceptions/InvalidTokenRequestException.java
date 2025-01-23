/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Describes a custom exception for when a token request is invalid.
 * If the customer has too many tokens, or requests an invalid amount of tokens (i.e <1).
 */

package dtu.group17.dtu_pay_facade.exceptions;

public class InvalidTokenRequestException extends RuntimeException {
    public InvalidTokenRequestException(String message) {
        super(message);
    }
}
