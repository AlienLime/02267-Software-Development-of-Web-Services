package dtu.group17.dtu_pay_facade;

import java.util.UUID;

public class CorrelationId {
    public synchronized static UUID randomCorrelationId() {
        return UUID.randomUUID();
    }
}