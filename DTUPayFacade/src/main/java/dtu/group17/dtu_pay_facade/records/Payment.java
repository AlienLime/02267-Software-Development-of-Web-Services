/*
    * Author: Katja Kaj (s123456)
    * Description:
    * Represents a payment.
    * Contains the token used for the payment, the amount of money involved and the merchant ID.
*/

package dtu.group17.dtu_pay_facade.records;

import java.util.UUID;

public record Payment(Token token, int amount, UUID merchantId) {}