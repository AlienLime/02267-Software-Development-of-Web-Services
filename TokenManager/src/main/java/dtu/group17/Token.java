package dtu.group17;

import java.util.UUID;

public record Token(UUID id) {
    public synchronized static Token randomToken() {
        return new Token(UUID.randomUUID());
    }
}
