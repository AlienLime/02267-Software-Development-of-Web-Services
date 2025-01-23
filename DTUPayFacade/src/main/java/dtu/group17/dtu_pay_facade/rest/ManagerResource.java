package dtu.group17.dtu_pay_facade.rest;

import dtu.group17.dtu_pay_facade.Clear;
import dtu.group17.dtu_pay_facade.records.ManagerReportEntry;
import dtu.group17.dtu_pay_facade.ReportManagerFacade;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/manager")
public class ManagerResource {

    @Inject
    ReportManagerFacade reportManagerFacade;

    @Inject
    Clear clear;

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ManagerReportEntry> getManagerReport() {
        return reportManagerFacade.getManagerReport();
    }

    @POST
    @Path("/clear")
    public boolean clearEverything() {
        return clear.clearEverything();
    }
}
