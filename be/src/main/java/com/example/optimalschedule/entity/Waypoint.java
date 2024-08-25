package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "waypoint")
public class Waypoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int onTrip;

    private Double latitude;

    private Double longitude;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    public Waypoint(int id, int onTrip, Double latitude, Double longitude, Date time) {
        this.id = id;
        this.onTrip = onTrip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}
