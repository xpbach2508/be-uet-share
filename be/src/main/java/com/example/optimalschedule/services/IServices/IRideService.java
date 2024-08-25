package com.example.optimalschedule.services.IServices;

import com.example.optimalschedule.model.response.RideResponse;

import java.util.List;

public interface IRideService {

    List<RideResponse> getAllRideByAccountId();
}
