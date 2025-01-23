package dtu.group17.dtu_pay_client;

import java.util.UUID;

public record FullPayment(UUID customerId, Token token, int amount, UUID merchantId) {}
