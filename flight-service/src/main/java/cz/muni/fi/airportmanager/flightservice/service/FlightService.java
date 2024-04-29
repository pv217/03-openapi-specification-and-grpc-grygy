package cz.muni.fi.airportmanager.flightservice.service;

import cz.muni.fi.airportmanager.flightservice.model.Flight;
import cz.muni.fi.airportmanager.flightservice.model.FlightStatus;
import cz.muni.fi.airportmanager.proto.FlightCancellationRequest;
import cz.muni.fi.airportmanager.proto.FlightCancellationResponseStatus;
import cz.muni.fi.airportmanager.proto.MutinyFlightCancellationGrpc;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FlightService {
    /**
     * This is a temporary storage for flights
     */
    private final Map<Integer, Flight> flights = new HashMap<>();

    @GrpcClient("passenger-service")
    MutinyFlightCancellationGrpc.MutinyFlightCancellationStub flightCancellationStub;

    /**
     * Get list of all flights
     *
     * @return list of all flights
     */
    public List<Flight> listAll() {
        return flights.values().stream().toList();
    }

    /**
     * Get flight by id
     *
     * @param id flight id
     * @return flight with given id
     * @throws IllegalArgumentException if flight with given id does not exist
     */
    public Flight getFlight(int id) {
        if (flights.get(id) == null) {
            throw new IllegalArgumentException("Flight with id " + id + " does not exist");
        }
        return flights.get(id);
    }

    /**
     * Create a new flight
     *
     * @param flight flight to create.
     * @return created flight
     * @throws IllegalArgumentException if flight with given id already exists
     */
    public Flight createFlight(Flight flight) {
        if (flights.get(flight.id) != null) {
            throw new IllegalArgumentException("Flight with id " + flight.id + " already exists");
        }
        flights.put(flight.id, flight);
        return flight;
    }

    /**
     * Update flight
     *
     * @param flight flight to update
     * @return updated flight
     * @throws IllegalArgumentException if flight with given id does not exist
     */
    public Flight updateFlight(Flight flight) {
        if (flights.get(flight.id) == null) {
            throw new IllegalArgumentException("Flight with id " + flight.id + " does not exist");
        }
        flights.put(flight.id, flight);
        return flight;
    }

    /**
     * Delete flight
     *
     * @param id flight id
     * @throws IllegalArgumentException if flight with given id does not exist
     */
    public void deleteFlight(int id) {
        if (flights.get(id) == null) {
            throw new IllegalArgumentException("Flight with id " + id + " does not exist");
        }
        flights.remove(id);
    }

    /**
     * Delete all flights
     */
    public void deleteAllFlights() {
        flights.clear();
    }

    /**
     * Cancel flight
     *
     * @param id flight id
     * @throws IllegalArgumentException if flight with given id does not exist
     */
    public void cancelFlight(int id) {
//        TODO if flight exists, call cancelFlight on stub
//        TODO set flight status to CANCELLED in flights map
//        TODO use FlightCancellationRequest.newBuilder() to create a request body
//        TODO await the result
        if (flights.get(id) == null) {
            throw new IllegalArgumentException("Flight with id " + id + " does not exist");
        }
        flights.get(id).status = FlightStatus.CANCELLED;
        var response = flightCancellationStub.cancelFlight(FlightCancellationRequest.newBuilder().setId(id).setReason("Unknown").build()).await().indefinitely();
        if (response.getStatus() != FlightCancellationResponseStatus.Cancelled) {
            throw new RuntimeException("Flight cancellation failed");
        }
    }
}
