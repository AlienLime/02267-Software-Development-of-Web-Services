package dtu.group17;

import java.util.List;
import java.util.UUID;

public interface ReportRepository {

    void savePayment(UUID customerID, Payment payment);

    List<CustomerReportEntry> getCustomerReport(UUID customerID);

    List<MerchantReportEntry> getMerchantReport(UUID merchantID);

    List<ManagerReportEntry> getManagerReport();

}
