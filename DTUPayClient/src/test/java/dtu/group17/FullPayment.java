package dtu.group17;

import java.util.UUID;

public record FullPayment(UUID customerId, Token token, int amount, UUID merchantId) {}
