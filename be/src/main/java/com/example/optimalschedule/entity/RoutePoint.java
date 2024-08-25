package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table (name = "route_point")
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    int routeId;

    double lat;
    double lng;

    public RoutePoint(int id, int routeId, double lat, double lng) {
        this.id = id;
        this.routeId = routeId;
        this.lat = lat;
        this.lng = lng;
    }
}
