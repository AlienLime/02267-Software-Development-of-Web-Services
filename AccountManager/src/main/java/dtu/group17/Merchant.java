package dtu.group17;

import java.util.UUID;

public record Merchant(UUID id, String accountId, String firstName, String lastName, String cpr) {}
