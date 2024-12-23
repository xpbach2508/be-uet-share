package com.example.optimalschedule.entity;

import com.example.optimalschedule.model.response.ScheduleAdminProphetResponse;
import com.example.optimalschedule.model.response.ScheduleAdminResponse;
import com.example.optimalschedule.model.response.ScheduleDriverResponse;
import com.example.optimalschedule.model.response.ScheduleProphetDriverResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "guidanceScheduleMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ScheduleProphetDriverResponse.class,
                        columns = {
                                @ColumnResult(name = "lat", type = Double.class),
                                @ColumnResult(name = "lng", type = Double.class),
                                @ColumnResult(name = "passengerName", type = String.class),
                                @ColumnResult(name = "passengerPhone", type = String.class),
                                @ColumnResult(name = "locationId", type = int.class),
                                @ColumnResult(name = "expectedTime", type = Double.class),
                                @ColumnResult(name = "wait", type = Double.class),
                        }
                )
        }
)

@NamedNativeQuery(name = "getGuidanceScheduleByGroupIdOrderByExpectedTime", query = "select s.lat, s.lng, p.full_name as passengerName, " +
        "p.phone as passengerPhone, s.location_id as locationId, s.expected_time as expectedTime, s.wait as wait " +
        "from guidance_schedule as s " +
        "inner join passenger as p on s.passenger_id = p.id " +
        "where s.group_id = ?1 " +
        "order by s.expected_time asc", resultSetMapping = "guidanceScheduleMapping")

@SqlResultSetMapping(
        name = "guidanceScheduleAdminMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ScheduleAdminProphetResponse.class,
                        columns = {
                                @ColumnResult(name = "id", type = Integer.class),
                                @ColumnResult(name = "groupId", type = Integer.class),
                                @ColumnResult(name = "lat", type = Double.class),
                                @ColumnResult(name = "lng", type = Double.class),
                                @ColumnResult(name = "driverName", type = String.class),
                                @ColumnResult(name = "licensePlate", type = String.class),
                                @ColumnResult(name = "nameCar", type = String.class),
                                @ColumnResult(name = "expectedTime", type = Double.class),
                                @ColumnResult(name = "wait", type = Double.class),
                        }
                )
        }
)

@NamedNativeQuery(name = "getAllGuidanceScheduleOrderByExpectedTime", query = "select s.id as id, s.group_id as groupId, s.lat, s.lng, " +
        "d.full_name as driverName, d.license_plate as licensePlate, d.name_car as nameCar, s.expected_time as expectedTime, s.wait as wait " +
        "from guidance_schedule as s " +
        "inner join group_frequent as gf on gf.id = s.group_id " +
        "inner join driver as d on gf.driver_id = d.id " +
        "order by s.group_id asc, s.expected_time asc", resultSetMapping = "guidanceScheduleAdminMapping")

@Entity
@Data
@NoArgsConstructor
@Table(name = "guidance_schedule")
public class GuidanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int groupId;

    private int passengerId;

    private Double lat;
    private Double lng;

    private Double lateTime;

    private Double expectedTime;
    private Double wait;

    private int locationId;

    private int scheduleId;

    private int capacityAvailable;

    public GuidanceSchedule(int groupId, int passengerId, Double lat, Double lng, Double lateTime, Double expectedTime, int locationId, int scheduleId, int capacityAvailable, Double wait) {
        this.groupId = groupId;
        this.passengerId = passengerId;
        this.lat = lat;
        this.lng = lng;
        this.lateTime = lateTime;
        this.expectedTime = expectedTime;
        this.locationId = locationId;
        this.scheduleId = scheduleId;
        this.capacityAvailable = capacityAvailable;
        this.wait = wait;
    }

    public GuidanceSchedule(int id, int groupId, int passengerId, Double lat, Double lng, Double lateTime, Double expectedTime, int locationId, int scheduleId, int capacityAvailable, Double wait) {
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
        this.wait = wait;
    }
}
