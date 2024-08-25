package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.ForbiddenException;
import com.example.optimalschedule.common.exception.NotFoundException;
import com.example.optimalschedule.common.exception.NotImplementedException;
import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.entity.*;
import com.example.optimalschedule.gripmap.MapUtility;
import com.example.optimalschedule.repository.*;
import com.example.optimalschedule.services.IServices.IShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.optimalschedule.gripmap.MapUtility.timeLate;

@Service
public class ShareService implements IShareService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private GroupFrequentRepository gfRepository;

    @Autowired
    private FrequentPointRepository fpRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private RequestRideRepository rideRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private WaypointRepository waypointRepository;

    @Override
    public boolean shareFrequent(int routeId) throws NotFoundException, ForbiddenException, BadRequestException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) throw new NotFoundException("routeId not exist!");
        if (route.getAccountId() != userDetails.getId()) throw new ForbiddenException("routeId is not of userId!");
        if (route.isShared()) throw new BadRequestException("Route was shared!");

        List<FrequentPoint> listPointUser = fpRepository.findByFrequentRouteId(route.getFrequentRouteId());
        List<String> userString = new ArrayList<>();
        MapUtility.covertFrequentPointToString(listPointUser, userString);

        // Get all taxi only have frequent passenger
        List<GroupFrequent> listDriverRoute = gfRepository.findAllOnlyFrequent();
        // If đang có group frequent và ghép đc => return true
        if (listDriverRoute.size() != 0 && matchWithDriver(userString, listDriverRoute, userDetails.getId(), route)) return true;
        return createNewGroup(routeId, route);
    }

    private boolean createNewGroup(int routeId, Route route) {
        // Create group
        Driver taxi = driverRepository.findOneNewTaxi();
        if (taxi == null) throw new NotImplementedException("Not have taxi ready now!");
        GroupFrequent newGF = new GroupFrequent(taxi.getId(), routeId);
        gfRepository.save(newGF);

        // Update schedule
        double expected_time_origin = MapUtility.convertTimeFromStringToDouble(route.getTimeStart());
        double time_late_origin = timeLate(expected_time_origin);
        Schedule start = new Schedule(newGF.getId(), route.getAccountId(), route.getLatStart(),
                route.getLngStart(), time_late_origin, expected_time_origin, 1, 1,
                taxi.getSeat() - 2);
        double expected_time_des = MapUtility.convertTimeFromStringToDouble(route.getTimeEnd());
        double time_late_des = timeLate(expected_time_des);
        Schedule end = new Schedule(newGF.getId(), route.getAccountId(), route.getLatEnd(),
                route.getLngEnd(), time_late_des, expected_time_des, 2, 1,
                taxi.getSeat() - 1);
        scheduleRepository.save(start);
        scheduleRepository.save(end);

        // Update group_id, cost and is_shared of current user in route
        double cost = route.getLengthRoute() * MapUtility.COST_OF_KM;
        route.setGroupId(newGF.getId());
        route.setCost(cost);
        route.setShared(true);
        routeRepository.save(route);
        return true;
    }

    private boolean matchWithDriver(List<String> userString, List<GroupFrequent> listDriverRoute, int userId, Route userRoute) {
        for (GroupFrequent gf : listDriverRoute) {
            // Get main route of taxi and list point of this route
            Route mainRouteOfTaxi = routeRepository.findById(gf.getMainRouteId()).orElse(null);
            List<FrequentPoint> driverRoute = fpRepository.findByFrequentRouteId(mainRouteOfTaxi.getFrequentRouteId());
            List<String> driverString = new ArrayList<>();
            MapUtility.covertFrequentPointToString(driverRoute, driverString);
            // Account of taxi
            Driver taxi = driverRepository.findById(gf.getDriverId()).orElse(null);
            if (driverString.containsAll(userString)) {
                if (checkCapacity(taxi, gf.getId())) {
                    addUserIntoRouteDriver(userId, userRoute, gf.getId());
                    return true;
                }
            } else if (userString.containsAll(driverString)) {
                if (checkCapacity(taxi, gf.getId())) {
                    changeMainRouteOfTaxi(userRoute, gf, taxi);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkCapacity(Driver driver, int groupId) {
        List<Route> listParticipant = routeRepository.findByGroupId(groupId);
        return (driver.getSeat() - 1) > listParticipant.size();
    }

    private void changeMainRouteOfTaxi(Route newMainRouteOfTaxi, GroupFrequent gf, Driver taxi) {
        // Update group
        gf.setMainRouteId(newMainRouteOfTaxi.getId());
        gfRepository.save(gf);

        // Update schedule
        scheduleRepository.updateCapacityByGroup(1, gf.getId());

        // Create schedule of newMainRouteOfTaxi
        double expected_time_origin = MapUtility.convertTimeFromStringToDouble(newMainRouteOfTaxi.getTimeStart());
        double time_late_origin = timeLate(expected_time_origin);
        Schedule start = new Schedule(gf.getId(), newMainRouteOfTaxi.getAccountId(), newMainRouteOfTaxi.getLatStart(),
                newMainRouteOfTaxi.getLngStart(), time_late_origin, expected_time_origin, 1, 1,
                taxi.getSeat() - 2);
        double expected_time_des = MapUtility.convertTimeFromStringToDouble(newMainRouteOfTaxi.getTimeEnd());
        double time_late_des = timeLate(expected_time_des);
        Schedule end = new Schedule(gf.getId(), newMainRouteOfTaxi.getAccountId(), newMainRouteOfTaxi.getLatEnd(),
                newMainRouteOfTaxi.getLngEnd(), time_late_des, expected_time_des, 2, 1,
                taxi.getSeat() - 1);
        scheduleRepository.save(start);
        scheduleRepository.save(end);

        // Update group_id, cost and is_shared of newMainRouteOfTaxi
        double cost = newMainRouteOfTaxi.getLengthRoute() * MapUtility.COST_OF_KM;
        newMainRouteOfTaxi.setGroupId(gf.getId());
        newMainRouteOfTaxi.setCost(cost);
        newMainRouteOfTaxi.setShared(true);
        routeRepository.save(newMainRouteOfTaxi);
    }

    // useId, passenger add into groupId
    private void addUserIntoRouteDriver(int userId, Route passenger, int groupId) {
        double expected_time_origin = MapUtility.convertTimeFromStringToDouble(passenger.getTimeStart());
        double expected_time_destination = MapUtility.convertTimeFromStringToDouble(passenger.getTimeEnd());
        // 1.1 Update schedule: origin and destination
        // Update capacity các point trong đó
        List<Schedule> schedules = scheduleRepository.findBetweenOriginAndDes(groupId, expected_time_origin, expected_time_destination);
        scheduleRepository.updateCapacity(groupId, expected_time_origin, expected_time_destination);

        // 1.2 Create origin
        // Tìm point gần nhất với origin point cần insert để lấy ra capacity_available của point gần nhất đó
        Schedule scheduleNearest = scheduleRepository.findByGroupAndSort(groupId, expected_time_origin);
        int capacityOrigin = scheduleNearest.getCapacityAvailable() - 1;
        double time_late_origin = timeLate(expected_time_origin);
        Schedule origin = new Schedule(groupId, userId, passenger.getLatStart(), passenger.getLngStart(),
                time_late_origin, expected_time_origin, 1, 1, capacityOrigin);

        // 1.3 Create destination
        // If ở giữa origin và destination không có point nào thì capacity of destination = capacity of origin + 1
        int capacityDes = capacityOrigin + 1;
        if (schedules.size() > 0) {
            Schedule scheduleNearestDes = scheduleRepository.findByGroupAndSort(groupId, expected_time_destination);
            capacityDes = scheduleNearestDes.getCapacityAvailable() + 1;
        }
        double time_late_destination = timeLate(expected_time_destination);
        Schedule destination = new Schedule(groupId, userId, passenger.getLatEnd(), passenger.getLngEnd(),
                time_late_destination, expected_time_destination, 2, 1, capacityDes);


        // 1.4 Insert origin and destination vào schedule
        scheduleRepository.save(origin);
        scheduleRepository.save(destination);

        // 2. Update group_id, cost and is_shared of current user in route
        double cost = passenger.getLengthRoute() * MapUtility.COST_OF_KM;
        passenger.setGroupId(groupId);
        passenger.setCost(cost);
        passenger.setShared(true);
        routeRepository.save(passenger);
    }

    @Override
    public void cancelShareFrequent(int routeId) throws NotFoundException, ForbiddenException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Update share
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) throw new NotFoundException("routeId not exist!");
        if (route.getAccountId() != userDetails.getId()) throw new ForbiddenException("routeId is not of userId!");
        if (!route.isShared()) throw new BadRequestException("Route was cancel shared!");
        int groupId = route.getGroupId();
        // Case 1: chưa đc serve
        if (groupId == 0) {
            route.setShared(false);
            routeRepository.save(route);
            // Case 2: đã đc serve
        } else {
            // Kiểm tra xem taxi đang có mấy passenger
            List<Schedule> countFrequentPassenger = scheduleRepository.findByGroupIdOrderByExpectedTime(groupId);
            int numberOfFrequentPassenger = countFrequentPassenger.size() / 2;
            List<RequestRide> numberOfOnlinePassenger = rideRepository.findByStatusIdAndGroupId(1, groupId);
            if (numberOfFrequentPassenger + numberOfOnlinePassenger.size() <= 1) gfRepository.deleteById(route.getGroupId());
            else {
                GroupFrequent gf = gfRepository.findById(route.getGroupId()).orElse(null);
                if (gf.getMainRouteId() == routeId) {
                    // Update main route of taxi
                    if (numberOfFrequentPassenger == 1) gf.setMainRouteId(0);
                    else {
                        Schedule scheduleOfNewRoute = countFrequentPassenger.get(1);
                        Route newMainRouteOfTaxi = routeRepository.findByAccountIdAndGroupId(scheduleOfNewRoute.getPassengerId(), groupId);
                        gf.setMainRouteId(newMainRouteOfTaxi.getId());
                    }
                    gfRepository.save(gf);
                }
                // Update schedule
                scheduleRepository.deleteOnePassenger(gf.getId(), userDetails.getId());
                scheduleRepository.updateCapacityByGroup(-1, gf.getId());

                // Update route of user
                route.setShared(false);
                route.setGroupId(0);
                route.setCost(0);
                routeRepository.save(route);
            }
        }
    }

    @Override
    public Trip addTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    @Override
    public Waypoint addWaypoint(Waypoint waypoint) {
        return waypointRepository.save(waypoint);
    }
}
