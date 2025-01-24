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
