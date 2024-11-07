package com.example.optimalschedule.model;

import lombok.Data;

@Data
public class ListEdgeCaseSpecial {

    private QueryEdge iToOrigin;
    private QueryEdge originToDes;
    // desToI1, iToI1 is null if insert into last point
    private QueryEdge desToI1;
    private QueryEdge iToI1;

    public ListEdgeCaseSpecial(QueryEdge iToOrigin, QueryEdge originToDes, QueryEdge desToI1, QueryEdge iToI1) {
        this.iToOrigin = iToOrigin;
        this.originToDes = originToDes;
        this.desToI1 = desToI1;
        this.iToI1 = iToI1;
    }

    public Double timeIncrease() {
        double result = iToOrigin.getDuration() + originToDes.getDuration();
        if (desToI1 != null) result += desToI1.getDuration(); //case insert not at end
        if (iToI1 != null) result -= iToI1.getDuration(); //case insert not at end
        return result;
    }

    public Double distanceIncrease() {
        double result = iToOrigin.getDistance() + originToDes.getDistance();
        if (desToI1 != null) result += desToI1.getDistance();
        if (iToI1 != null) result -= iToI1.getDistance();
        return result;
    }

}
