package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "grid")
public class Grid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Double latStart;
    private Double latEnd;
    private Double lngStart;
    private Double lngEnd;

    private Double anchorLat;
    private Double anchorLng;

    public Grid(Double latStart, Double latEnd, Double lngStart, Double lngEnd, Double anchorLat, Double anchorLng) {
        this.latStart = latStart;
        this.latEnd = latEnd;
        this.lngStart = lngStart;
        this.lngEnd = lngEnd;
        this.anchorLat = anchorLat;
        this.anchorLng = anchorLng;
    }

    public Grid(int id, Double latStart, Double latEnd, Double lngStart, Double lngEnd, Double anchorLat, Double anchorLng) {
        this.id = id;
        this.latStart = latStart;
        this.latEnd = latEnd;
        this.lngStart = lngStart;
        this.lngEnd = lngEnd;
        this.anchorLat = anchorLat;
        this.anchorLng = anchorLng;
    }
}
