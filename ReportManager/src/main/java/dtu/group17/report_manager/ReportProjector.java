package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.report_manager.domain.CustomerReportEntry;
import dtu.group17.report_manager.domain.ManagerReportEntry;
import dtu.group17.report_manager.domain.MerchantReportEntry;
import dtu.group17.report_manager.domain.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportProjector {
    private final ReportReadRepository reportReadRepository;
    MessageQueue queue;

    public ReportProjector(ReportReadRepository reportReadRepository, MessageQueue queue) {
        this.reportReadRepository = reportReadRepository;
        this.queue = queue;

        queue.subscribe("PaymentCompleted", this::applyPaymentCompleted);
        queue.subscribe("ReportsCleared", this::applyReportsCleared);
    }

    public void applyPaymentCompleted(Event event) {
        UUID customerId = event.getArgument("customerId", UUID.class);
        UUID merchantId = event.getArgument("merchantId", UUID.class);
        int amount = event.getArgument("amount", Integer.class);
        Token token = event.getArgument("token", Token.class);

        CustomerReportEntry customerReportEntry = new CustomerReportEntry(amount, merchantId, token);
        reportReadRepository.getCustomerReports().computeIfAbsent(customerId, id -> new ArrayList<>()).add(customerReportEntry);

        MerchantReportEntry merchantReportEntry = new MerchantReportEntry(amount, token);
        reportReadRepository.getMerchantReports().computeIfAbsent(merchantId, id -> new ArrayList<>()).add(merchantReportEntry);

        ManagerReportEntry managerReportEntry = new ManagerReportEntry(amount, merchantId, customerId, token);
        reportReadRepository.getManagerReports().add(managerReportEntry);
    }

    public void applyReportsCleared(Event event) {
        reportReadRepository.clear();
    }
}
