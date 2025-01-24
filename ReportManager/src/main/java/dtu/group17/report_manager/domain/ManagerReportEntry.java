/*
 * Author: Benjamin Noah Lumbye (s204428)
 * Description:
 * Represents a single entry in a manager report.
 * An entry is a payment.
 * Contains the amount of money spent, the merchant ID, the token used and the description for the payment.
 */
package dtu.group17.report_manager.domain;

import java.util.UUID;

public record ManagerReportEntry(int amount, UUID merchantId, UUID customerId, Token token, String description) {}
