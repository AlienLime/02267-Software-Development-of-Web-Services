package dtu.group17.records;

import java.util.UUID;

public record CustomerReportEntry(int amount, UUID merchantId, Token token) {}
