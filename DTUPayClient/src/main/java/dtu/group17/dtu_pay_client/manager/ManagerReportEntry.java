/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Represents a single entry in a manager report.
 * An entry is a payment.
 * Contains the amount of money spent, the merchant ID, the customer ID and the token used for the payment.
 */

package dtu.group17.dtu_pay_client.manager;

import dtu.group17.dtu_pay_client.Token;

import java.util.UUID;

public record ManagerReportEntry(int amount, UUID merchantId, UUID customerId, Token token) {}
