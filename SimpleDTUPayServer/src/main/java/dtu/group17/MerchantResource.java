package dtu.group17;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("merchants/{id}")
public class MerchantResource {

    @Inject
    DTUPayService service;

    @DELETE
    public boolean deregisterMerchant(@PathParam("id") String id) {
        return service.deregisterMerchant(id);
    }

}
