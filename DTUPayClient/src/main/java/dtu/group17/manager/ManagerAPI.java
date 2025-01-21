package dtu.group17.manager;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

public class ManagerAPI {

    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    public List<ManagerReportEntry> requestManagerReport(UUID id) {
        Response response = target.path("manager").path("report").request().get();
        return response.readEntity(new GenericType<>() {});
    }

}
