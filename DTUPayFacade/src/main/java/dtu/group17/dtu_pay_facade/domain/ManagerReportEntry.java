/*
 * Author: Kristoffer Magnus Overgaard (s194110)
 * Description:
 * Represents a single entry in a manager report.
 * An entry is a full payment with all information.
 * Contains the amount of money spent, the merchant ID, the customerId and the token used for the payment.
 */
package dtu.group17.dtu_pay_facade.domain;

import java.util.UUID;

public record ManagerReportEntry(int amount, UUID merchantId, UUID customerId, Token token, String description) {}
