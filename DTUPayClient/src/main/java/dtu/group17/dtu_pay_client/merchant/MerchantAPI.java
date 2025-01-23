/*
 * Author: Katja Kaj (s123456)
 * Description:
 * MerchantAPI provides an interface for interacting with merchant-related functionalities (including banking services and REST APIs).
 * It connects between the backend service and the application logic to enable managing merchant accounts, tokens, and reports.
 */

package dtu.group17.dtu_pay_client.merchant;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

public class MerchantAPI {

    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    public record RegisterMerchantBody(Merchant merchant, String accountId) {}
    public Merchant register(Merchant merchant, String accountId) {
        try {
            RegisterMerchantBody body = new RegisterMerchantBody(merchant, accountId); //TODO: Use factory pattern
            try (Response response = target.path("merchants").request().post(Entity.json(body))) {
                return response.readEntity(Merchant.class);
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean deregister(UUID id) {
        try {
            Response response = target.path("merchants").path(id.toString()).request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean submitPayment(Payment payment) throws Exception {
        Response response = target.path("merchants").path(payment.merchantId().toString()).path("payment").request().post(Entity.json(payment));

        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
                || response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }
        return response.getStatus() == Response.Status.OK.getStatusCode();

    }

    public List<MerchantReportEntry> requestMerchantReport(UUID id) {
        Response response = target.path("merchants").path(id.toString()).path("report").request().get();
        return response.readEntity(new GenericType<>() {});
    }

}
