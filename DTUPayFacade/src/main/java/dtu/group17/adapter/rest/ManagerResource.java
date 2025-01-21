package dtu.group17.adapter.rest;

import dtu.group17.ManagerReportEntry;
import dtu.group17.ReportingManagerFacade;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/manager")
public class ManagerResource {

    @Inject
    ReportingManagerFacade reportingManagerFacade;

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ManagerReportEntry> getManagerReport() {
        return reportingManagerFacade.getManagerReport();
    }

}
