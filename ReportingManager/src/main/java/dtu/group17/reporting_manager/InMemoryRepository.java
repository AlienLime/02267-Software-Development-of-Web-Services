package dtu.group17.reporting_manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository implements ReportRepository {

    private Map<UUID, List<Token>> customerTokens = new ConcurrentHashMap<>();
    private Map<UUID, List<Token>> merchantTokens = new ConcurrentHashMap<>();
    private Map<Token, PaymentInfo> tokenPaymentInfo = new ConcurrentHashMap<>();

    @Override
    public void savePayment(UUID customerId, UUID merchantId, int amount, Token token) {
        customerTokens.computeIfAbsent(customerId, id -> new ArrayList<>()).add(token);
        merchantTokens.computeIfAbsent(merchantId, id -> new ArrayList<>()).add(token);
        tokenPaymentInfo.put(token, new PaymentInfo(amount, merchantId));
    }

    @Override
    public List<CustomerReportEntry> getCustomerReport(UUID customerId) {
        if (!customerTokens.containsKey(customerId)) {
            return new ArrayList<>();
        }

        List<Token> tokens = customerTokens.get(customerId);

        return tokens.stream().map(token -> {
            PaymentInfo info = tokenPaymentInfo.get(token);
            return new CustomerReportEntry(info.amount(), info.merchantId(), token);
        }).toList();
    }

    @Override
    public List<MerchantReportEntry> getMerchantReport(UUID merchantId) {
        if (!merchantTokens.containsKey(merchantId)) {
            return new ArrayList<>();
        }

        List<Token> tokens = merchantTokens.get(merchantId);

        return tokens.stream().map(token -> {
            PaymentInfo info = tokenPaymentInfo.get(token);
            return new MerchantReportEntry(info.amount(), token);
        }).toList();
    }

    @Override
    public List<ManagerReportEntry> getManagerReport() {
        return customerTokens.entrySet().stream().flatMap(entry ->
            entry.getValue().stream().map(token -> {
                PaymentInfo info = tokenPaymentInfo.get(token);
                return new ManagerReportEntry(info.amount(), info.merchantId(), entry.getKey(), token);
            })
        ).toList();
    }

    @Override
    public void clearReports() {
        customerTokens.clear();
        merchantTokens.clear();
        tokenPaymentInfo.clear();
    }

}
