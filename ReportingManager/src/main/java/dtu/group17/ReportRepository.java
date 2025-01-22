package dtu.group17;

import java.util.List;
import java.util.UUID;

public interface ReportRepository {

    void savePayment(UUID customerId, UUID merchantId, int amount, Token token);

    List<CustomerReportEntry> getCustomerReport(UUID customerId);

    List<MerchantReportEntry> getMerchantReport(UUID merchantId);

    List<ManagerReportEntry> getManagerReport();

    void clearReports();

}
