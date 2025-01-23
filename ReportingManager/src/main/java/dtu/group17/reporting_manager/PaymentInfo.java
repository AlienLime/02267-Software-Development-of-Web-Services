package dtu.group17.reporting_manager;

import java.util.UUID;

public record PaymentInfo(int amount, UUID merchantId) {}
