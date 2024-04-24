package cz.muni.fi.airportmanager.flightservice.resources;

import cz.muni.fi.airportmanager.flightservice.model.example.Examples;
import cz.muni.fi.airportmanager.flightservice.service.FlightService;
import cz.muni.fi.airportmanager.flightservice.model.Flight;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * This class is a REST resource that will be hosted on /flight
 */
@Path("/flight")
@Tag(name = "Flight", description = "Flight CRUD API")
public class FlightResource {

    @Inject
    FlightService flightService;


    /**
     * Get list of all flights
     *
     * @return list of all flights
     */
    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Get list of all flights")
    @APIResponse(
            responseCode = "200",
            description = "Get list of all flights",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Flight.class, required = true),
                    examples = @ExampleObject(name = "flight", value = Examples.VALID_FLIGHT_LIST)
            )
    )
    //    TODO add openapi docs
    public RestResponse<List<Flight>> list() {
        return RestResponse.status(Response.Status.OK, flightService.listAll());
    }

    /**
     * Create a new flight
     *
     * @param flight flight to create.
     * @return created flight
     */
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create a new flight")
    @APIResponse(
            responseCode = "201",
            description = "Created flight",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Flight.class, required = true),
                    examples = @ExampleObject(name = "flight", value = Examples.VALID_FLIGHT)
            )
    )
    @APIResponse(
            responseCode = "409",
            description = "Conflict - flight with given id already exists"
    )
    //    TODO add openapi docs
    public RestResponse<Flight> create(
            @Schema(implementation = Flight.class, required = true)
            Flight flight) {
        try {
            var newFlight = flightService.createFlight(flight);
            return RestResponse.status(Response.Status.CREATED, newFlight);
        } catch (IllegalArgumentException e) {
            return RestResponse.status(Response.Status.CONFLICT);
        }
    }


    /**
     * Get flight by id
     *
     * @param id id of flight
     * @return flight with given id
     */
    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Get flight by id")
    @APIResponse(
            responseCode = "200",
            description = "Flight with given id",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Flight.class, required = true),
                    examples = @ExampleObject(name = "flight", value = Examples.VALID_FLIGHT)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Flight with given id does not exist"
    )
    //    TODO add openapi docs
    public RestResponse<Flight> get(@Parameter(name = "id", required = true) @PathParam("id") int id) {
        try {
            var flight = flightService.getFlight(id);
            return RestResponse.status(Response.Status.OK, flight);
        } catch (IllegalArgumentException e) {
            return RestResponse.status(Response.Status.NOT_FOUND);
        }
    }


    /**
     * Update flight
     *
     * @param id     id of flight
     * @param flight flight to update
     */
    @PUT
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Update flight")
    @APIResponse(
            responseCode = "200",
            description = "Updated flight",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Flight.class, required = true),
                    examples = @ExampleObject(name = "flight", value = Examples.VALID_FLIGHT)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Flight with given id does not exist"
    )
    //    TODO add openapi docs
    public RestResponse<Flight> update(@Parameter(name = "id", required = true) @PathParam("id") int id,
                                       @Schema(implementation = Flight.class, required = true)
                                       Flight flight) {
        if (flight.id != id) {
            return RestResponse.status(Response.Status.BAD_REQUEST);
        }
        try {
            var updatedFlight = flightService.updateFlight(flight);
            return RestResponse.status(Response.Status.OK, updatedFlight);
        } catch (IllegalArgumentException e) {
            return RestResponse.status(Response.Status.NOT_FOUND);
        }
    }

    /**
     * Delete flight
     *
     * @param id id of flight
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete flight")
    @APIResponse(
            responseCode = "200",
            description = "Flight deleted"
    )
    @APIResponse(
            responseCode = "404",
            description = "Flight with given id does not exist"
    )
    //    TODO add openapi docs
    public RestResponse<Flight> delete(@Parameter(name = "id", required = true) @PathParam("id") int id) {
        try {
            flightService.deleteFlight(id);
            return RestResponse.status(Response.Status.OK);
        } catch (IllegalArgumentException e) {
            return RestResponse.status(Response.Status.NOT_FOUND);
        }
    }

    /**
     * Helper method for to delete all flights
     */
    @DELETE
    @Operation(summary = "Delete all flights")
    @APIResponse(
            responseCode = "200",
            description = "All flights deleted"
    )
    public RestResponse<Flight> deleteAll() {
        flightService.deleteAllFlights();
        return RestResponse.status(Response.Status.OK);
    }

    /**
     * Cancel flight
     */
    @PUT
    @Path("/{id}/cancel")
    @Operation(summary = "Cancel flight")
    @APIResponse(
            responseCode = "200",
            description = "Flight cancelled"
    )
    @APIResponse(
            responseCode = "404",
            description = "Flight with given id does not exist"
    )
    //    TODO add openapi docs
    public RestResponse<Flight> cancel(@Parameter(name = "id", required = true) @PathParam("id") int id) {
        try {
            flightService.cancelFlight(id);
            return RestResponse.status(Response.Status.OK);
        } catch (IllegalArgumentException e) {
            return RestResponse.status(Response.Status.NOT_FOUND);
        }
    }

}

