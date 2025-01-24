/*
 * Author: Kristoffer Magnus Overgaard (s194110)
 * Description:
 * A correlation id is a unique identifier that is used to correlate messages between different services.
 */

package dtu.group17.dtu_pay_facade;

import java.util.UUID;

public class CorrelationId {
    public synchronized static UUID randomCorrelationId() {
        return UUID.randomUUID();
    }
}