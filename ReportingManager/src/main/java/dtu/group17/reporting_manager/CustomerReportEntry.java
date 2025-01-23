package dtu.group17.reporting_manager;

import java.util.UUID;

public record CustomerReportEntry(int amount, UUID merchantId, Token token) {}
