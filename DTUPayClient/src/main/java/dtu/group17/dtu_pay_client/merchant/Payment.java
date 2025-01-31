/*
 * Author: Benjamin Noah Lumbye (s204428)
 * Description:
 * Represents a payment.
 * Contains the token used for the payment, the amount of money involved and the merchant ID.
 */

package dtu.group17.dtu_pay_client.merchant;
import dtu.group17.dtu_pay_client.Token;

import java.util.UUID;


public record Payment(Token token, int amount, UUID merchantId, String description) {}