package dtu.group17.records;

import java.util.UUID;

public record Payment(Token token, int amount, UUID merchantId) {}