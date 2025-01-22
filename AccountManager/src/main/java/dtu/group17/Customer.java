package dtu.group17;

import java.util.UUID;

public record Customer(UUID id, String accountId, String firstName, String lastName, String cpr) {}
