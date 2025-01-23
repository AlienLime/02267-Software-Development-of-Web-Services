/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Represents a single entry in a customer report.
 * An entry is a payment.
 * Contains the amount of money spent, the merchant ID and the token used for the payment.
 */

package dtu.group17.dtu_pay_client.customer;

import dtu.group17.dtu_pay_client.Token;

import java.util.UUID;

public record CustomerReportEntry(int amount, UUID merchantId, Token token) {}
