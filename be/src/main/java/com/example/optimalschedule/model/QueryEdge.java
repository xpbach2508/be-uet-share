package com.example.optimalschedule.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QueryEdge {

    private double distance;
    private double duration;

    public QueryEdge(double distance, double duration) {
        this.distance = distance;
        this.duration = duration;
    }
}
