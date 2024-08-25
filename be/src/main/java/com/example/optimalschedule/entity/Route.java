package com.example.optimalschedule.entity;

import com.example.optimalschedule.model.response.RideResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@SqlResultSetMapping(
        name = "routeMapping",
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
                                @ColumnResult(name = "groupId", type = int.class)
                        }
                )
        }
)

@NamedNativeQuery(name = "getAllFrequentSharedByAccountId", query = "select r.id as routeOrRequestId, " +
        "r.address_start as addressStart, r.address_end as addressEnd, d.full_name as nameDriver, d.license_plate as licensePlate, " +
        "d.name_car as nameCar, d.seat, d.phone as phoneDriver, r.cost, r.group_id as groupId " +
        "from route as r " +
        "inner join group_frequent as gf on r.group_id = gf.id " +
        "inner join driver as d on gf.driver_id = d.id " +
        "where r.is_shared = true and r.account_id = ?1", resultSetMapping = "routeMapping")

@Entity
@Data
@NoArgsConstructor
@Table (name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int accountId;

    private int frequentRouteId;

    private String addressStart;
    private String addressEnd;

    private double latStart;
    private double lngStart;

    private double latEnd;
    private double lngEnd;

    private String timeStart;
    private String timeEnd;

    private boolean isShared;

    private double lengthRoute;
    private int weekday;

    private int groupId;
    private double cost;

    public Route(int id, int accountId, int frequentRouteId, String addressStart, String addressEnd, double latStart, double lngStart, double latEnd, double lngEnd, String timeStart, String timeEnd, boolean isShared, double lengthRoute, int weekday, int groupId, double cost) {
        this.id = id;
        this.accountId = accountId;
        this.frequentRouteId = frequentRouteId;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
        this.latStart = latStart;
        this.lngStart = lngStart;
        this.latEnd = latEnd;
        this.lngEnd = lngEnd;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.isShared = isShared;
        this.lengthRoute = lengthRoute;
        this.weekday = weekday;
        this.groupId = groupId;
        this.cost = cost;
    }
}
