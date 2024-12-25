package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.NotImplementedException;
import com.example.optimalschedule.constant.GroupType;
import com.example.optimalschedule.entity.*;
import com.example.optimalschedule.gripmap.MapUtility;
import com.example.optimalschedule.model.ExpectedTimeRequest;
import com.example.optimalschedule.model.ListEdgeCaseNormal;
import com.example.optimalschedule.model.ListEdgeCaseSpecial;
import com.example.optimalschedule.model.QueryEdge;
import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.request.PredictedRequest;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProphetInsertService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private GuidanceScheduleRepository scheduleRepository;

    @Autowired
    private RequestRideRepository rideRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private GridRepository gridRepository;

    @Autowired
    private GroupFrequentRepository gfRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void clearData() {
        jdbcTemplate.update("DELETE FROM group_frequent WHERE id >= 11");
        jdbcTemplate.update("DELETE FROM request_ride WHERE id >= 19");
        jdbcTemplate.update("DELETE FROM optimal_schedule.guidance_schedule WHERE id >= 1");
        jdbcTemplate.update("DELETE FROM optimal_schedule.schedule WHERE id >= 93");
    }

    public RideResponse createAndInsertRequest(PredictedRequest data, GroupFrequent groupOptimal, int indexOrigin,
                                               int indexDes, List<GuidanceSchedule> scheduleOfOptimal,
                                               ListEdgeCaseNormal listOptimalNormal,
                                               ListEdgeCaseSpecial listOptimalSpecial, int userId, int requestType) {
        RequestRide request = new RequestRide(userId, data.getPickUpTime(), data.getCapacity(),
                data.getLength() * MapUtility.COST_OF_KM * data.getCapacity(), data.getLatOrigin(),
                data.getLngOrigin(), data.getLatDestination(), data.getLngDestination(), 1, groupOptimal.getId(),
                data.getAddressStart(), data.getAddressEnd());
        rideRepository.save(request);
        ExpectedTimeRequest etRequest = insertSchedule(groupOptimal, indexOrigin, indexDes, data, scheduleOfOptimal,
                listOptimalNormal, listOptimalSpecial, userId, requestType);
        Driver driver = driverRepository.findById(groupOptimal.getDriverId()).orElse(null);

        return new RideResponse(request.getId(), data.getAddressStart(), data.getAddressEnd(),
                etRequest.getExpectedTimeOrigin(), etRequest.getExpectedTimeDes(), driver.getFullName(),
                driver.getLicensePlate(), driver.getNameCar(), driver.getSeat(), driver.getPhone(), request.getCost(),
                groupOptimal.getId(), false, 1, request.getBookingDate());
    }

    // Insert and update schedule table
    private ExpectedTimeRequest insertSchedule(GroupFrequent groupOptimal, int origin, int des, PredictedRequest req, List<GuidanceSchedule> scheduleOfOptimal,
                                               ListEdgeCaseNormal listOptimalNormal, ListEdgeCaseSpecial listOptimalSpecial, int userId, int requestType) {
        GuidanceSchedule scheduleOrigin = scheduleOfOptimal.get(origin);

        double expectedTimeOrigin = scheduleOrigin.getExpectedTime();
        if (scheduleOrigin.getWait() > 0.0) {
            expectedTimeOrigin += scheduleOrigin.getWait();
        }
        double expectedTimeDes;
        int capacityOfOrigin = scheduleOrigin.getCapacityAvailable() - req.getCapacity();
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

        double waitTimeOrigin = Math.max(0.0, req.getPickUpTime() - expectedTimeOrigin);
        double pickUpTimeLate = MapUtility.timeLate(expectedTimeOrigin + waitTimeOrigin);
        double lateTimeDes = pickUpTimeLate + expectedTimeDes - expectedTimeOrigin;
        // insert
        GuidanceSchedule scheduleOriginNew = new GuidanceSchedule(groupOptimal.getId(), userId, req.getLatOrigin(), req.getLngOrigin(),
                pickUpTimeLate, expectedTimeOrigin, 1, requestType, capacityOfOrigin, waitTimeOrigin);
        GuidanceSchedule scheduleDes = new GuidanceSchedule(groupOptimal.getId(), userId, req.getLatDestination(), req.getLngDestination(),
                lateTimeDes, expectedTimeDes, 2, requestType, capacityOfDes, 0.0);
        scheduleRepository.save(scheduleOriginNew);
        scheduleRepository.save(scheduleDes);

        // update
        // for only run when origin != destination
        for (int i = origin + 1; i <= des; i++) {
            GuidanceSchedule point = scheduleOfOptimal.get(i);
            point.setExpectedTime(point.getExpectedTime() + listOptimalNormal.timeIncreaseOrigin());
            point.setCapacityAvailable(point.getCapacityAvailable() - req.getCapacity());
            if (point.getWait() != 0.0) point.setWait(Math.max(point.getWait() - listOptimalNormal.timeIncreaseOrigin(), 0.0));
            scheduleRepository.save(point);
        }
        for (int i = des + 1; i < scheduleOfOptimal.size(); i++) {
            GuidanceSchedule point = scheduleOfOptimal.get(i);
            if (origin == des) {
                point.setExpectedTime(point.getExpectedTime() + listOptimalSpecial.timeIncrease());
                if (point.getWait() != 0.0) point.setWait(Math.max(point.getWait() - listOptimalSpecial.timeIncrease(), 0.0));
            }
            else {
                point.setExpectedTime(point.getExpectedTime() + listOptimalNormal.timeIncrease());
                if (point.getWait() != 0.0) point.setWait(Math.max(point.getWait() - listOptimalNormal.timeIncreaseOrigin(), 0.0));
            }
            scheduleRepository.save(point);
        }

        return new ExpectedTimeRequest(expectedTimeOrigin, expectedTimeDes);
    }

    public RideResponse createNewGroup(PredictedRequest data, int gridOriginId, int gridDesId, int userId, int typeGroup, int requestType) {
        if (typeGroup == GroupType.GUIDANCE.getValue()) {
            // Create group
            Driver taxi = driverRepository.findOneNewHypoTaxi();
            if (taxi == null) {
                RequestRide request = new RequestRide(userId, data.getPickUpTime(), data.getCapacity(),
                        data.getLength() * MapUtility.COST_OF_KM * data.getCapacity(), data.getLatOrigin(),
                        data.getLngOrigin(), data.getLatDestination(), data.getLngDestination(), 4, 0,
                        data.getAddressStart(), data.getAddressEnd());
                rideRepository.save(request);
                throw new NotImplementedException("Not have taxi ready now!");
            }
            GroupFrequent newGF = new GroupFrequent(taxi.getId(), 0, typeGroup);
            gfRepository.save(newGF);

            // Update schedule
            // origin
            double expected_time_origin = data.getPickUpTime();
            double waitTime = 0.2; //init waiting time from first request
            GuidanceSchedule start = new GuidanceSchedule(newGF.getId(), userId, data.getLatOrigin(),
                    data.getLngOrigin(), data.getPickUpTimeLate(), expected_time_origin - waitTime, 1, requestType,
                    taxi.getSeat() - 1, waitTime);
            // destination
            QueryEdge originToDes = findIdGridWhenHaveOriginAndDesId(gridOriginId, gridDesId);
            double expected_time_des = expected_time_origin + originToDes.getDuration();
            double waitTimeDes = 0.0;
            double late_time_des = data.getPickUpTimeLate() + (expected_time_des - expected_time_origin);
            GuidanceSchedule end = new GuidanceSchedule(newGF.getId(), userId, data.getLatDestination(),
                    data.getLngDestination(), late_time_des, expected_time_des, 2, requestType,
                    taxi.getSeat() - 1, waitTimeDes);
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
        } else {
            // Create group
            Driver taxi = driverRepository.findOneNewTaxi();
            if (taxi == null) throw new NotImplementedException("Not have taxi ready now!");
            GroupFrequent newGF = new GroupFrequent(taxi.getId(), 0, typeGroup);
            gfRepository.save(newGF);

            // Update schedule
            // origin
            double expected_time_origin = data.getPickUpTime();
            double waitTime = Math.max(data.getPickUpTime() - expected_time_origin, 0.0);
            GuidanceSchedule start = new GuidanceSchedule(newGF.getId(), userId, data.getLatOrigin(),
                    data.getLngOrigin(), data.getPickUpTimeLate(), expected_time_origin, 1, 2,
                    taxi.getSeat() - 1 - data.getCapacity(), waitTime);
            // destination
            QueryEdge originToDes = findIdGridWhenHaveOriginAndDesId(gridOriginId, gridDesId);
            double expected_time_des = expected_time_origin + originToDes.getDuration();
            double waitTimeDes = Math.max(data.getPickUpTime() - expected_time_des, 0.0);
            double late_time_des = data.getPickUpTimeLate() + (expected_time_des - expected_time_origin);
            GuidanceSchedule end = new GuidanceSchedule(newGF.getId(), userId, data.getLatDestination(),
                    data.getLngDestination(), late_time_des, expected_time_des, 2, 2,
                    taxi.getSeat() - 1, waitTimeDes);
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
