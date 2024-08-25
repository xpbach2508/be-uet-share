package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table (name = "grid_point")
public class GridPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int gridTripId;

    private int lat;

    private int lng;

    private int time;

    public GridPoint(int id, int gridTripId, int lat, int lng, int time) {
        this.id = id;
        this.gridTripId = gridTripId;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    @Override
    public String toString(){
        return "(" + lat + ":" + lng + ":" + time +")";
    }
}
