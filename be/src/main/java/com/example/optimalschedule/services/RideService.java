package com.example.optimalschedule.services;

import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.entity.Schedule;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.repository.RequestRideRepository;
import com.example.optimalschedule.repository.RouteRepository;
import com.example.optimalschedule.repository.ScheduleRepository;
import com.example.optimalschedule.services.IServices.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService implements IRideService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RequestRideRepository rideRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public List<RideResponse> getAllRideByAccountId() {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int accountId = userDetails.getId();
        // Get list frequent
        List<RideResponse> listFrequent = routeRepository.getAllFrequentSharedByAccountId(accountId);
        for (RideResponse ride : listFrequent) {
            List<Schedule> scheduleOfOnePassenger = scheduleRepository.findByGroupIdAndPassengerId(ride.getGroupId(), accountId);
            Schedule point1 = scheduleOfOnePassenger.get(0);
            Schedule point2 = scheduleOfOnePassenger.get(1);
            // If point1 is origin
            if (point1.getLocationId() == 1) {
                ride.setExpectedTimeOrigin(point1.getExpectedTime());
                ride.setExpectedTimeDestination(point2.getExpectedTime());
            } else {
                ride.setExpectedTimeOrigin(point2.getExpectedTime());
                ride.setExpectedTimeDestination(point1.getExpectedTime());
            }
            ride.setFrequent(true);
            ride.setStatusId(1);
        }

        // Get list booking online is serving
        List<RideResponse> listOnline = rideRepository.getAllOnlineByAccountId(accountId);
        for (RideResponse ride : listOnline) {
            // If ride is serving thì mới có record trong schedule table
            if (ride.getStatusId() == 1) {
                List<Schedule> scheduleOfOnePassenger = scheduleRepository.findByGroupIdAndPassengerId(ride.getGroupId(), accountId);
                Schedule point1 = scheduleOfOnePassenger.get(0);
                Schedule point2 = scheduleOfOnePassenger.get(1);
                // If point1 is origin
                if (point1.getLocationId() == 1) {
                    ride.setExpectedTimeOrigin(point1.getExpectedTime());
                    ride.setExpectedTimeDestination(point2.getExpectedTime());
                } else {
                    ride.setExpectedTimeOrigin(point2.getExpectedTime());
                    ride.setExpectedTimeDestination(point1.getExpectedTime());
                }
            } else {
                ride.setExpectedTimeOrigin(null);
                ride.setExpectedTimeDestination(null);
            }
            ride.setFrequent(false);
        }

        listFrequent.addAll(listOnline);
        return listFrequent;
    }
}
