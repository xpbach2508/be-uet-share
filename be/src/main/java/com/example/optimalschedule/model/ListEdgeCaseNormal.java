package com.example.optimalschedule.model;

import lombok.Data;

@Data
public class ListEdgeCaseNormal {

    private QueryEdge iToOrigin;
    private QueryEdge originToI1;
    private QueryEdge iToI1;
    private QueryEdge jToDes;
    // desToJ1, jToJ1 is null if destination is last point
    private QueryEdge desToJ1;
    private QueryEdge jToJ1;

    public ListEdgeCaseNormal(QueryEdge iToOrigin, QueryEdge originToI1, QueryEdge iToI1, QueryEdge jToDes, QueryEdge desToJ1, QueryEdge jToJ1) {
        this.iToOrigin = iToOrigin;
        this.originToI1 = originToI1;
        this.iToI1 = iToI1;
        this.jToDes = jToDes;
        this.desToJ1 = desToJ1;
        this.jToJ1 = jToJ1;
    }

    public Double timeIncreaseOrigin() {
        return iToOrigin.getDuration() + originToI1.getDuration() - iToI1.getDuration();
    }

    public Double distanceIncreaseOrigin() {
        return iToOrigin.getDistance() + originToI1.getDistance() - iToI1.getDistance();
    }

    public Double timeIncreaseDes() {
        double result = jToDes.getDuration();
        if (desToJ1 != null) result += desToJ1.getDuration();
        if (jToJ1 != null) result -= jToJ1.getDuration();
        return result;
    }

    public Double distanceIncreaseDes() {
        double result = jToDes.getDistance();
        if (desToJ1 != null) result += desToJ1.getDistance();
        if (jToJ1 != null) result -= jToJ1.getDistance();
        return result;
    }

    public double distanceIncrease() {
        return distanceIncreaseOrigin() + distanceIncreaseDes();
    }

    public double timeIncrease() {
        return timeIncreaseOrigin() + timeIncreaseDes();
    }
}
