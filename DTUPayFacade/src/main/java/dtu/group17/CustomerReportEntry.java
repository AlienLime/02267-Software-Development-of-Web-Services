package dtu.group17;

import java.util.UUID;

public record CustomerReportEntry(int amount, UUID merchantId, Token token) {}
