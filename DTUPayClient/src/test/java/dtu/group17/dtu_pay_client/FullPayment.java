/*
 * Author: Emil Wraae Carlsen (s204458)
 * Description:
 * A full payment is a payment with every field assigned. Only the manager has a use for full payments.
 */

package dtu.group17.dtu_pay_client;

import java.util.UUID;

public record FullPayment(UUID customerId, Token token, int amount, UUID merchantId, String description) {} //TODO: Make parameters @notnull
