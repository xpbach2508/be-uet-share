package com.example.optimalschedule.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduleAdminProphetResponse {

    private int id;
    private int groupId;
    private Double lat;
    private Double lng;
    private String driverName;
    private String licensePlate;
    private String nameCar;
    private double expectedTime;
    private String expectedTimeString;
    private double wait;

    public ScheduleAdminProphetResponse(int id, int groupId, Double lat, Double lng, String driverName, String licensePlate,
                                        String nameCar, double expectedTime, double wait) {
        this.id = id;
        this.groupId = groupId;
        this.lat = lat;
        this.lng = lng;
        this.driverName = driverName;
        this.licensePlate = licensePlate;
        this.nameCar = nameCar;
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
