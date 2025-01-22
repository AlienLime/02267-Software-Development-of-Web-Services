/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Represents a payment.
 * Contains the token used for the payment, the amount of money involved and the merchant ID.
 */

package dtu.group17.merchant;
import dtu.group17.Token;

import java.util.UUID;


public record Payment(Token token, int amount, UUID merchantId) {}