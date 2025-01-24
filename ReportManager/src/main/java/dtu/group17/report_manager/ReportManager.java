package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import dtu.group17.report_manager.domain.CustomerReportEntry;
import dtu.group17.report_manager.domain.ManagerReportEntry;
import dtu.group17.report_manager.domain.MerchantReportEntry;
import dtu.group17.report_manager.domain.Token;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
