/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Represents a single entry in a customer report.
 * An entry is a payment.
 * Contains the amount of money spent, the merchant ID and the token used for each payment.
 */

package dtu.group17.customer;

import dtu.group17.Token;

import java.util.UUID;

public record CustomerReportEntry(int amount, UUID merchantId, Token token) {}
