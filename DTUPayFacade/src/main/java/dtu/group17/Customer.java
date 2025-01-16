package dtu.group17;

import java.util.UUID;

public record Customer(UUID id, String firstName, String lastName, String cpr) {
}
