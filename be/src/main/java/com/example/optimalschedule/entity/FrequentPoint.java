package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "frequent_point")
public class FrequentPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int frequentRouteId;

    private int lat;

    private int lng;

    private int time;

    public FrequentPoint(int frequentRouteId, int lat, int lng, int time) {
        this.frequentRouteId = frequentRouteId;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public FrequentPoint(int id, int frequentRouteId, int lat, int lng, int time) {
        this.id = id;
        this.frequentRouteId = frequentRouteId;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    @Override
    public String toString() {
        return "(" + lat + ":" + lng + ":" + time + ")";
    }
}
