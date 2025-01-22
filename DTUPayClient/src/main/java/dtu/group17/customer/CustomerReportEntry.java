package dtu.group17.customer;

import dtu.group17.Token;

import java.util.UUID;

public record CustomerReportEntry(int amount, UUID merchantId, Token token) {}
