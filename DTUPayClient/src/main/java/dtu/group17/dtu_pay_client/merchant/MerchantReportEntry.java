/*
 * Author: Kristoffer Magnus Overgaard (s194110)
 * Description:
 * Represents a single entry in a merchant report.
 * An entry is a payment.
 * Contains the amount of money involved and the token used for the payment.
 */

package dtu.group17.dtu_pay_client.merchant;

import dtu.group17.dtu_pay_client.Token;

public record MerchantReportEntry(int amount, Token token, String description) {}
