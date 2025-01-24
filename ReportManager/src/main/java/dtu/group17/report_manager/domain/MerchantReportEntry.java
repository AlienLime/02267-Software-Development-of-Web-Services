/*
 * Author: Kristoffer Magnus Overgaard (s194110)
 * Description:
 * Represents a single entry in a merchant report.
 * An entry is a payment.
 * Contains the amount of money spent, the token used and the description for the payment.
 */

package dtu.group17.report_manager.domain;

public record MerchantReportEntry(int amount, Token token, String description) {}
