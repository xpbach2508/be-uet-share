package com.example.optimalschedule.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RideResponse {

    private int routeOrRequestId;
    private String addressStart;
    private String addressEnd;
    private Double expectedTimeOrigin;
    private Double expectedTimeDestination;
    private String nameDriver;
    private String licensePlate;
    private String nameCar;
    private int seat;
    private String phoneDriver;
    private Double cost;
    private int groupId;
    private boolean frequent;
    private int statusId;
    private LocalDate bookingDate;

    public RideResponse(int routeOrRequestId, String addressStart, String addressEnd, String nameDriver,
                        String licensePlate, String nameCar, int seat, String phoneDriver, Double cost, int groupId) {
        this.routeOrRequestId = routeOrRequestId;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
        this.nameDriver = nameDriver;
        this.licensePlate = licensePlate;
        this.nameCar = nameCar;
        this.seat = seat;
        this.phoneDriver = phoneDriver;
        this.cost = cost;
        this.groupId = groupId;
    }

    public RideResponse(int routeOrRequestId, String addressStart, String addressEnd, String nameDriver,
                        String licensePlate, String nameCar, int seat, String phoneDriver, Double cost, int groupId,
                        int statusId, LocalDate bookingDate) {
        this.routeOrRequestId = routeOrRequestId;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
        this.nameDriver = nameDriver;
        this.licensePlate = licensePlate;
        this.nameCar = nameCar;
        this.seat = seat;
        this.phoneDriver = phoneDriver;
        this.cost = cost;
        this.groupId = groupId;
        this.statusId = statusId;
        this.bookingDate = bookingDate;
    }

    public RideResponse(int routeOrRequestId, String addressStart, String addressEnd, Double expectedTimeOrigin,
                        Double expectedTimeDestination, String nameDriver, String licensePlate, String nameCar,
                        int seat, String phoneDriver, Double cost, int groupId, boolean frequent, int statusId,
                        LocalDate bookingDate) {
        this.routeOrRequestId = routeOrRequestId;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
        this.expectedTimeOrigin = expectedTimeOrigin;
        this.expectedTimeDestination = expectedTimeDestination;
        this.nameDriver = nameDriver;
        this.licensePlate = licensePlate;
        this.nameCar = nameCar;
        this.seat = seat;
        this.phoneDriver = phoneDriver;
        this.cost = cost;
        this.groupId = groupId;
        this.frequent = frequent;
        this.statusId = statusId;
        this.bookingDate = bookingDate;
    }
}
