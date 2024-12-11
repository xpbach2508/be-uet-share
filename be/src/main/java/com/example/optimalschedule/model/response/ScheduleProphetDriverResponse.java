package com.example.optimalschedule.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduleProphetDriverResponse {

    private Double lat;
    private Double lng;
    private String passengerName;
    private String passengerPhone;
    private int locationId;
    private double expectedTime;
    private String expectedTimeString;
    private double wait;

    public ScheduleProphetDriverResponse(Double lat, Double lng, String passengerName, String passengerPhone, int locationId,
                                         double expectedTime, double wait) {
        this.lat = lat;
        this.lng = lng;
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
        this.locationId = locationId;
        this.expectedTime = expectedTime;
        this.wait = wait;
        convertTime();
    }

    private void convertTime() {
        expectedTimeString = "";
        int hour = (int) expectedTime;
        int minute = (int) ((expectedTime - hour) * 60);
        if (hour < 10) expectedTimeString += "0";
        expectedTimeString += hour + ":";
        if (minute < 10) expectedTimeString += "0";
        expectedTimeString += minute;
    }

}
