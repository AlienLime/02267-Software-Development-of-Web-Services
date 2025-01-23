package dtu.group17.dtu_pay_facade.records;

import java.util.UUID;

public record CustomerReportEntry(int amount, UUID merchantId, Token token) {}
