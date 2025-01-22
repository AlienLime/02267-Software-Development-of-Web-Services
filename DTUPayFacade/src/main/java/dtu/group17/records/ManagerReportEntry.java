package dtu.group17.records;

import java.util.UUID;

public record ManagerReportEntry(int amount, UUID merchantId, UUID customerId, Token token) {}
