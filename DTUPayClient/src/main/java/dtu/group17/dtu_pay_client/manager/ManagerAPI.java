/*
 * Author: Katja Kaj (s123456)
 * Description:
 * ManagerAPI is a simple interface for interacting with the single manager-related activity (generating a report).
 */

package dtu.group17.dtu_pay_client.manager;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import java.util.List;

public class ManagerAPI {

    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    /**
     * Requests a manager report by sending a GET request.
     * @author Katja
     */
    public List<ManagerReportEntry> requestManagerReport() {
        Response response = target.path("manager").path("report").request().get();
        return response.readEntity(new GenericType<>() {});
    }

    /**
     * Clears all data by sending a POST request to the manager.
     * @author Katja
     */
    public boolean clearEverything() {
        Response response = target.path("manager").path("clear").request().post(null);
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }
}
