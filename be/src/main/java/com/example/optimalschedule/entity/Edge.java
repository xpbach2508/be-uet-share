package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "edge")
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int origin;
    private int destination;

    private Double distance;
    private Double duration;

    public Edge(int origin, int destination, Double distance, Double duration) {
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
    }

    public Edge(int id, int origin, int destination, Double distance, Double duration) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
    }
}
