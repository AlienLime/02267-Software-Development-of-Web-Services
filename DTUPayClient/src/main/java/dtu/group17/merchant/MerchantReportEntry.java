/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Represents a single entry in a merchant report.
 * An entry is a payment.
 * Contains the amount of money involved and the token used for the payment.
 */

package dtu.group17.merchant;

import dtu.group17.Token;

public record MerchantReportEntry(int amount, Token token) {}
