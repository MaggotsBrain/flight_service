package de.btu.flight_service.interfaces;

import de.btu.flight_service.models.FlightAddRequest;
import de.btu.flight_service.models.FlightGetRequest;
import de.btu.flight_service.models.FlightResponse;
import de.btu.flight_service.models.IcaoListResponse;

import java.util.List;

public interface IFlightService {

    boolean add(FlightAddRequest request);

    List<FlightResponse> get(FlightGetRequest request);

    FlightResponse getLast(FlightGetRequest request);

    List<FlightResponse> getAll();

    IcaoListResponse listIcao();

}
