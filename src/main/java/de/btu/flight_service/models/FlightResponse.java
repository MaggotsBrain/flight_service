package de.btu.flight_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {

    private String icao;

    private String callsign;

    private String speed;

    private String heading;

    private String position;

    private String eo;

    private String downlink_Format;

    private String message_Subtype;

    private String type_Code;

    private String parity;

    private String time;
}
