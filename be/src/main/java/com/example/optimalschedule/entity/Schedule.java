package com.example.optimalschedule.entity;

import com.example.optimalschedule.model.response.ScheduleAdminResponse;
import com.example.optimalschedule.model.response.ScheduleDriverResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "scheduleMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ScheduleDriverResponse.class,
                        columns = {
                                @ColumnResult(name = "lat", type = Double.class),
                                @ColumnResult(name = "lng", type = Double.class),
                                @ColumnResult(name = "passengerName", type = String.class),
                                @ColumnResult(name = "passengerPhone", type = String.class),
                                @ColumnResult(name = "locationId", type = int.class),
                                @ColumnResult(name = "expectedTime", type = Double.class)
                        }
                )
        }
)

@NamedNativeQuery(name = "getScheduleByGroupIdOrderByExpectedTime", query = "select s.lat, s.lng, p.full_name as passengerName, " +
        "p.phone as passengerPhone, s.location_id as locationId, s.expected_time as expectedTime " +
        "from schedule as s " +
        "inner join passenger as p on s.passenger_id = p.id " +
        "where s.group_id = ?1 " +
        "order by s.expected_time asc", resultSetMapping = "scheduleMapping")

@SqlResultSetMapping(
        name = "scheduleAdminMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ScheduleAdminResponse.class,
                        columns = {
                                @ColumnResult(name = "groupId", type = Integer.class),
                                @ColumnResult(name = "lat", type = Double.class),
                                @ColumnResult(name = "lng", type = Double.class),
                                @ColumnResult(name = "driverName", type = String.class),
                                @ColumnResult(name = "licensePlate", type = String.class),
                                @ColumnResult(name = "nameCar", type = String.class),
                                @ColumnResult(name = "expectedTime", type = Double.class)
                        }
                )
        }
)

@NamedNativeQuery(name = "getAllScheduleOrderByExpectedTime", query = "select s.group_id as groupId, s.lat, s.lng, " +
        "d.full_name as driverName, d.license_plate as licensePlate, d.name_car as nameCar, s.expected_time as expectedTime " +
        "from schedule as s " +
        "inner join group_frequent as gf on gf.id = s.group_id " +
        "inner join driver as d on gf.driver_id = d.id " +
        "order by s.group_id asc, s.expected_time asc", resultSetMapping = "scheduleAdminMapping")

@Entity
@Data
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int groupId;

    private int passengerId;

    private Double lat;
    private Double lng;

    private Double lateTime;

    private Double expectedTime;

    private int locationId;

    private int scheduleId;

    private int capacityAvailable;

    public Schedule(int groupId, int passengerId, Double lat, Double lng, Double lateTime, Double expectedTime, int locationId, int scheduleId, int capacityAvailable) {
        this.groupId = groupId;
        this.passengerId = passengerId;
        this.lat = lat;
        this.lng = lng;
        this.lateTime = lateTime;
        this.expectedTime = expectedTime;
        this.locationId = locationId;
        this.scheduleId = scheduleId;
        this.capacityAvailable = capacityAvailable;
    }

    public Schedule(int id, int groupId, int passengerId, Double lat, Double lng, Double lateTime, Double expectedTime, int locationId, int scheduleId, int capacityAvailable) {
        this.id = id;
        this.groupId = groupId;
        this.passengerId = passengerId;
        this.lat = lat;
        this.lng = lng;
        this.lateTime = lateTime;
        this.expectedTime = expectedTime;
        this.locationId = locationId;
        this.scheduleId = scheduleId;
        this.capacityAvailable = capacityAvailable;
    }
}
