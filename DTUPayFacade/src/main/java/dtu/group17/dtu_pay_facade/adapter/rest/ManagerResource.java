/*
 * Author: Benjamin Noah Lumbye (s204428)
 * Description:
 * ManagerResource is an endpoint for the REST API that provides the functionality for the manager to view the report.
 * Managers are not actual users as Merchants and Customers are, so they do not have a separate class.
 *
 * There is only ever one manager.
 */

package dtu.group17.dtu_pay_facade.adapter.rest;

import dtu.group17.dtu_pay_facade.Clear;
import dtu.group17.dtu_pay_facade.ReportManagerFacade;
import dtu.group17.dtu_pay_facade.domain.ManagerReportEntry;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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

    /**
     * Get the manager report containing all transactions with all information
     * @return List of ManagerReportEntry
     * @see ManagerReportEntry
     * @author Benjamin Noah Lumbye (s204428)
     */
    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ManagerReportEntry> getManagerReport() {
        return reportManagerFacade.getManagerReport();
    }

    /**
     * Clears all accounts, reports and tokens.
     * @return true if everything was cleared
     * @throws java.util.concurrent.CancellationException if something went wrong
     * @throws java.util.concurrent.CompletionException if something went wrong
     * @see Clear#clearEverything()
     * @author Stine Lund Madsen (s204425)
     */
    @DELETE
    @Path("/clear")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean clearEverything() {
        return clear.clearEverything();
    }

}
