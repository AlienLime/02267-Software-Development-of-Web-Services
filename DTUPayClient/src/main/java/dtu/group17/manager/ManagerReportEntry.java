package dtu.group17.manager;

import dtu.group17.Token;

import java.util.UUID;

public record ManagerReportEntry(int amount, UUID merchantId, UUID customerId, Token token) {}
