package dtu.group17.report_manager;

import java.util.UUID;

public record PaymentInfo(int amount, UUID merchantId) {}
