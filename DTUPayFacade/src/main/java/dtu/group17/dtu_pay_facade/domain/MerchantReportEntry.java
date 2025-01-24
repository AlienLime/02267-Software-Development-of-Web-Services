/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Represents a single entry in a merchant report.
 * An entry is a payment.
 * Contains the amount of money spent and the token used for the payment.
 */

package dtu.group17.dtu_pay_facade.domain;

public record MerchantReportEntry(int amount, Token token, String description) {}
