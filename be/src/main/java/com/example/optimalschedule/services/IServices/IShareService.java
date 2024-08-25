package com.example.optimalschedule.services.IServices;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.ForbiddenException;
import com.example.optimalschedule.common.exception.NotFoundException;
import com.example.optimalschedule.entity.Trip;
import com.example.optimalschedule.entity.Waypoint;

public interface IShareService {

    public boolean shareFrequent(int routeId) throws NotFoundException, ForbiddenException, BadRequestException;

    public void cancelShareFrequent(int routeId) throws NotFoundException, ForbiddenException;

    public Trip addTrip(Trip trip);

    public Waypoint addWaypoint(Waypoint waypoint);

}
