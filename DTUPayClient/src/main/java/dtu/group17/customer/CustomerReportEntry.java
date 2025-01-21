package dtu.group17.customer;

import dtu.group17.Token;

import java.util.UUID;

//TODO: should merchantId be name?
public record CustomerReportEntry(UUID merchantId, int amount, Token token) {}
