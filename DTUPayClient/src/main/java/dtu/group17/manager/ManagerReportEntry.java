package dtu.group17.manager;

import dtu.group17.Token;

import java.util.UUID;

public record ManagerReportEntry(UUID merchantId, int amount, UUID customerId, Token token) {}
