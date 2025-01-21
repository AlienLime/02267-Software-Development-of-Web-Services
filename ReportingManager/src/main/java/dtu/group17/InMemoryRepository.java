package dtu.group17;

import java.util.*;

public class InMemoryRepository implements ReportRepository {

    private Map<UUID, List<Token>> customerTokens = new HashMap<>();
    private Map<UUID, List<Token>> merchantTokens = new HashMap<>();
    private Map<Token, PaymentInfo> tokenPaymentInfo = new HashMap<>();

    @Override
    public void savePayment(UUID customerId, Payment payment) {
        customerTokens.computeIfAbsent(customerId, id -> new ArrayList<>()).add(payment.token());
        merchantTokens.computeIfAbsent(payment.merchantId(), id -> new ArrayList<>()).add(payment.token());
        tokenPaymentInfo.put(payment.token(), new PaymentInfo(payment.amount(), payment.merchantId()));
    }

    @Override
    public List<CustomerReportEntry> getCustomerReport(UUID customerId) {
        if (!customerTokens.containsKey(customerId)) {
            return new ArrayList<>();
        }

        List<Token> tokens = customerTokens.get(customerId);

        return tokens.stream().map(token -> {
            PaymentInfo info = tokenPaymentInfo.get(token);
            return new CustomerReportEntry(info.merchantId(), info.amount(), token);
        }).toList();
    }

    @Override
    public List<MerchantReportEntry> getMerchantReport(UUID merchantId) {
        if (!customerTokens.containsKey(merchantId)) {
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
                return new ManagerReportEntry(info.merchantId(), info.amount(), entry.getKey(), token);
            })
        ).toList();
    }
}
