package com.example.optimalschedule.entity;

import com.example.optimalschedule.model.response.RideResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@SqlResultSetMapping(
        name = "rrMapping",
        classes = {
                @ConstructorResult(
                        targetClass = RideResponse.class,
                        columns = {
                                @ColumnResult(name = "routeOrRequestId", type = int.class),
                                @ColumnResult(name = "addressStart", type = String.class),
                                @ColumnResult(name = "addressEnd", type = String.class),
                                @ColumnResult(name = "nameDriver", type = String.class),
                                @ColumnResult(name = "licensePlate", type = String.class),
                                @ColumnResult(name = "nameCar", type = String.class),
                                @ColumnResult(name = "seat", type = int.class),
                                @ColumnResult(name = "phoneDriver", type = String.class),
                                @ColumnResult(name = "cost", type = Double.class),
                                @ColumnResult(name = "groupId", type = int.class),
                                @ColumnResult(name = "statusId", type = int.class),
                                @ColumnResult(name = "bookingDate", type = LocalDate.class)
                        }
                )
        }
)

@NamedNativeQuery(name = "getAllOnlineByAccountId", query = "select r.id as routeOrRequestId, r.address_start as addressStart, " +
        "r.address_end as addressEnd, d.full_name as nameDriver, d.license_plate as licensePlate, d.name_car as nameCar, " +
        "d.seat, d.phone as phoneDriver, r.cost, r.group_id as groupId, r.status_id as statusId, r.booking_date as bookingDate " +
        "from request_ride as r " +
        "inner join group_frequent as gf on r.group_id = gf.id " +
        "inner join driver as d on gf.driver_id = d.id " +
        "where r.passenger_id = ?1 " +
        "order by r.status_id asc, r.booking_date desc", resultSetMapping = "rrMapping")

@Entity
@Data
@NoArgsConstructor
@Table(name = "request_ride")
public class RequestRide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int passengerId;
    private Double pickUpTime;
    private int capacity;
    private Double cost;
    private Double latOrigin;
    private Double lngOrigin;
    private Double latDestination;
    private Double lngDestination;
    /*
        1: serving
        2: done
        3: cancel
     */
    private int statusId;
    private int groupId;
    private String addressStart;
    private String addressEnd;
    private LocalDate bookingDate;

    public RequestRide(int passengerId, Double pickUpTime, int capacity, Double cost, Double latOrigin,
                       Double lngOrigin, Double latDestination, Double lngDestination, int statusId, int groupId,
                       String addressStart, String addressEnd) {
        this.passengerId = passengerId;
        this.pickUpTime = pickUpTime;
        this.capacity = capacity;
        this.cost = cost;
        this.latOrigin = latOrigin;
        this.lngOrigin = lngOrigin;
        this.latDestination = latDestination;
        this.lngDestination = lngDestination;
        this.statusId = statusId;
        this.groupId = groupId;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
        this.bookingDate = LocalDate.now();
    }

    public RequestRide(int id, int passengerId, Double pickUpTime, int capacity, Double cost, Double latOrigin,
                       Double lngOrigin, Double latDestination, Double lngDestination, int statusId, int groupId,
                       String addressStart, String addressEnd, LocalDate bookingDate) {
        this.id = id;
        this.passengerId = passengerId;
        this.pickUpTime = pickUpTime;
        this.capacity = capacity;
        this.cost = cost;
        this.latOrigin = latOrigin;
        this.lngOrigin = lngOrigin;
        this.latDestination = latDestination;
        this.lngDestination = lngDestination;
        this.statusId = statusId;
        this.groupId = groupId;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
        this.bookingDate = bookingDate;
    }
}
