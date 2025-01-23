package dtu.group17.dtu_pay_facade.records;

import java.util.UUID;

public record Customer(UUID id, String firstName, String lastName, String cpr) {}
