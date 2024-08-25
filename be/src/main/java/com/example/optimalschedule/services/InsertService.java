package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.NotImplementedException;
import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.GroupFrequent;
import com.example.optimalschedule.entity.RequestRide;
import com.example.optimalschedule.entity.Schedule;
import com.example.optimalschedule.gripmap.MapUtility;
import com.example.optimalschedule.model.ExpectedTimeRequest;
import com.example.optimalschedule.model.ListEdgeCaseNormal;
import com.example.optimalschedule.model.ListEdgeCaseSpecial;
import com.example.optimalschedule.model.QueryEdge;
import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsertService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private RequestRideRepository rideRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private GridRepository gridRepository;

    @Autowired
    private GroupFrequentRepository gfRepository;

    public RideResponse createAndInsertRequest(BookOnlineRequest data, GroupFrequent groupOptimal, int indexOrigin,
                                               int indexDes, List<Schedule> scheduleOfOptimal,
                                               ListEdgeCaseNormal listOptimalNormal,
                                               ListEdgeCaseSpecial listOptimalSpecial, int userId) {
        RequestRide request = new RequestRide(userId, data.getPickUpTime(), data.getCapacity(),
                data.getLength() * MapUtility.COST_OF_KM * data.getCapacity(), data.getLatOrigin(),
                data.getLngOrigin(), data.getLatDestination(), data.getLngDestination(), 1, groupOptimal.getId(),
                data.getAddressStart(), data.getAddressEnd());
        rideRepository.save(request);
        ExpectedTimeRequest etRequest = insertSchedule(groupOptimal, indexOrigin, indexDes, data, scheduleOfOptimal,
                listOptimalNormal, listOptimalSpecial, userId);
        Driver driver = driverRepository.findById(groupOptimal.getDriverId()).orElse(null);

        return new RideResponse(request.getId(), data.getAddressStart(), data.getAddressEnd(),
                etRequest.getExpectedTimeOrigin(), etRequest.getExpectedTimeDes(), driver.getFullName(),
                driver.getLicensePlate(), driver.getNameCar(), driver.getSeat(), driver.getPhone(), request.getCost(),
                groupOptimal.getId(), false, 1, request.getBookingDate());
    }

    // Insert and update schedule table
    private ExpectedTimeRequest insertSchedule(GroupFrequent groupOptimal, int origin, int des, BookOnlineRequest req, List<Schedule> scheduleOfOptimal,
                                               ListEdgeCaseNormal listOptimalNormal, ListEdgeCaseSpecial listOptimalSpecial, int userId) {
        double expectedTimeOrigin = scheduleOfOptimal.get(origin).getExpectedTime();
        double expectedTimeDes;
        int capacityOfOrigin = scheduleOfOptimal.get(origin).getCapacityAvailable() - req.getCapacity();
        int capacityOfDes;

        if (origin == des) {
            expectedTimeOrigin += listOptimalSpecial.getIToOrigin().getDuration();
            expectedTimeDes = expectedTimeOrigin + listOptimalSpecial.getOriginToDes().getDuration();
            capacityOfDes = capacityOfOrigin + req.getCapacity();
        } else {
            expectedTimeOrigin += listOptimalNormal.getIToOrigin().getDuration();
            expectedTimeDes = scheduleOfOptimal.get(des).getExpectedTime() + listOptimalNormal.timeIncreaseOrigin() + listOptimalNormal.getJToDes().getDuration();
            capacityOfDes = scheduleOfOptimal.get(des).getCapacityAvailable();
        }

        // insert
        Schedule scheduleOrigin = new Schedule(groupOptimal.getId(), userId, req.getLatOrigin(), req.getLngOrigin(),
                req.getPickUpTimeLate(), expectedTimeOrigin, 1, 2, capacityOfOrigin);
        double lateTimeDes = req.getPickUpTimeLate() + (expectedTimeDes - expectedTimeOrigin);
        Schedule scheduleDes = new Schedule(groupOptimal.getId(), userId, req.getLatDestination(), req.getLngDestination(),
                lateTimeDes, expectedTimeDes, 2, 2, capacityOfDes);
        scheduleRepository.save(scheduleOrigin);
        scheduleRepository.save(scheduleDes);

        // update
        // for only run when origin != destination
        for (int i = origin + 1; i <= des; i++) {
            Schedule point = scheduleOfOptimal.get(i);
            point.setExpectedTime(point.getExpectedTime() + listOptimalNormal.timeIncreaseOrigin());
            point.setCapacityAvailable(point.getCapacityAvailable() - req.getCapacity());
            scheduleRepository.save(point);
        }
        for (int i = des + 1; i < scheduleOfOptimal.size(); i++) {
            Schedule point = scheduleOfOptimal.get(i);
            if (origin == des) point.setExpectedTime(point.getExpectedTime() + listOptimalSpecial.timeIncrease());
            else point.setExpectedTime(point.getExpectedTime() + listOptimalNormal.timeIncrease());
            scheduleRepository.save(point);
        }

        return new ExpectedTimeRequest(expectedTimeOrigin, expectedTimeDes);
    }

    public RideResponse createNewGroup(BookOnlineRequest data, int gridOriginId, int gridDesId, int userId) {
        // Create group
        Driver taxi = driverRepository.findOneNewTaxi();
        if (taxi == null) throw new NotImplementedException("Not have taxi ready now!");
        GroupFrequent newGF = new GroupFrequent(taxi.getId(), 0);
        gfRepository.save(newGF);

        // Update schedule
            // origin
        double expected_time_origin = data.getPickUpTime();
        Schedule start = new Schedule(newGF.getId(), userId, data.getLatOrigin(),
                data.getLngOrigin(), data.getPickUpTimeLate(), expected_time_origin, 1, 2,
                taxi.getSeat() - 1 - data.getCapacity());
            // destination
        QueryEdge originToDes = findIdGridWhenHaveOriginAndDesId(gridOriginId, gridDesId);
        double expected_time_des = expected_time_origin + originToDes.getDuration();
        double late_time_des = data.getPickUpTimeLate() + (expected_time_des - expected_time_origin);
        Schedule end = new Schedule(newGF.getId(), userId, data.getLatDestination(),
                data.getLngDestination(), late_time_des, expected_time_des, 2, 2,
                taxi.getSeat() - 1);
        scheduleRepository.save(start);
        scheduleRepository.save(end);

        // Create request
        RequestRide request = new RequestRide(userId, data.getPickUpTime(), data.getCapacity(),
                data.getLength() * MapUtility.COST_OF_KM * data.getCapacity(), data.getLatOrigin(),
                data.getLngOrigin(), data.getLatDestination(), data.getLngDestination(), 1, newGF.getId(),
                data.getAddressStart(), data.getAddressEnd());
        rideRepository.save(request);

        return new RideResponse(request.getId(), data.getAddressStart(), data.getAddressEnd(),
                expected_time_origin, expected_time_des, taxi.getFullName(), taxi.getLicensePlate(), taxi.getNameCar(),
                taxi.getSeat(), taxi.getPhone(), request.getCost(), newGF.getId(), false, 1,
                request.getBookingDate());
    }

    public QueryEdge findIdGrid(double latOrigin, double lngOrigin, double latDestination, double lngDestination) {
        int gridOriginId = MapUtility.convertToGridId(latOrigin, lngOrigin);
        int gridDesId = MapUtility.convertToGridId(latDestination, lngDestination);
        int edgeId = MapUtility.convertToEdgeId(gridOriginId, gridDesId);
        if (edgeId == 0) return new QueryEdge(0, 0);
        return edgeRepository.findQueryEdgeById(edgeId);
    }

    public QueryEdge findIdGridWhenHaveOriginId(int originId, double latDestination, double lngDestination) {
        int gridDesId = MapUtility.convertToGridId(latDestination, lngDestination);
        int edgeId = MapUtility.convertToEdgeId(originId, gridDesId);
        if (edgeId == 0) return new QueryEdge(0, 0);
        return edgeRepository.findQueryEdgeById(edgeId);
    }

    public QueryEdge findIdGridWhenHaveDesId(double latOrigin, double lngOrigin, int desId) {
        int gridOriginId = MapUtility.convertToGridId(latOrigin, lngOrigin);
        int edgeId = MapUtility.convertToEdgeId(gridOriginId, desId);
        if (edgeId == 0) return new QueryEdge(0, 0);
        return edgeRepository.findQueryEdgeById(edgeId);
    }

    public QueryEdge findIdGridWhenHaveOriginAndDesId(int originId, int desId) {
        int edgeId = MapUtility.convertToEdgeId(originId, desId);
        if (edgeId == 0) return new QueryEdge(0, 0);
        return edgeRepository.findQueryEdgeById(edgeId);
    }
}
