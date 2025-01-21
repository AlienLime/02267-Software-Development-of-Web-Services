package dtu.group17;

import java.util.UUID;

public record ManagerReportEntry(UUID merchantId, int amount, UUID customerId, Token token) {}
