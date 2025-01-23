package dtu.group17.dtu_pay_facade.records;

import java.util.UUID;

public record Payment(Token token, int amount, UUID merchantId) {}