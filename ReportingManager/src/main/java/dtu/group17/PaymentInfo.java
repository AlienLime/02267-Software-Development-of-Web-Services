package dtu.group17;

import java.util.UUID;

public record PaymentInfo(int amount, UUID merchantId) {}
