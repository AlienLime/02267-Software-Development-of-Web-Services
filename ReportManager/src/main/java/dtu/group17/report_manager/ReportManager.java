/*
 * Author: Emil Kim Krarup (s204449)
 * Description:
 * Main class for the report manager.
 * Initializes the report manager by creating the necessary components: the event store, the report read repository, the report projector, the report projection and the aggregate.
 */

package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;

public class ReportManager {
    public static void main(String[] args) {
        MessageQueue queue = new RabbitMQQueue();
        EventStore eventStore = new EventStore();
        ReportReadRepository reportReadRepository = new ReportReadRepository();

        new ReportProjector(reportReadRepository, queue);
        new ReportProjection(reportReadRepository, queue);
        new Aggregate(queue, eventStore);
    }
}
