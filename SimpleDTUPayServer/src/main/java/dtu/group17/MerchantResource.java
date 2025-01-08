package dtu.group17;

import jakarta.inject.Singleton;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/merchant")
public class MerchantResource {

    @Singleton
    private DTUPayService service = new DTUPayService();

    @DELETE
    @Path("/{id}")
    public boolean deregisterMerchant(@PathParam("id") String id) {
        return service.deregisterMerchant(id);
    }

}
