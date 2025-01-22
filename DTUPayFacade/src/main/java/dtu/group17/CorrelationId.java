package dtu.group17;

import java.util.UUID;

public class CorrelationId {
    public synchronized static UUID randomCorrelationId() {
        return UUID.randomUUID();
    }
}