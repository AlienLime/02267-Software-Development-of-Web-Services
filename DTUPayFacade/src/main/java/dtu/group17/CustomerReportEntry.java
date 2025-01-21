package dtu.group17;

import java.util.UUID;

//TODO: should merchantId be name?
public record CustomerReportEntry(UUID merchantId, int amount, Token token) {}
