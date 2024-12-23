package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.NotImplementedException;
import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.entity.GroupFrequent;
import com.example.optimalschedule.entity.GuidanceSchedule;
import com.example.optimalschedule.entity.RequestRide;
import com.example.optimalschedule.entity.Schedule;
import com.example.optimalschedule.gripmap.MapUtility;
import com.example.optimalschedule.model.ListEdgeCaseNormal;
import com.example.optimalschedule.model.ListEdgeCaseSpecial;
import com.example.optimalschedule.model.QueryEdge;
import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.response.ExperimentResponse;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.repository.GroupFrequentRepository;
import com.example.optimalschedule.repository.RequestRideRepository;
import com.example.optimalschedule.repository.ScheduleRepository;
import com.example.optimalschedule.services.IServices.IInsertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;

@Service
public class BasicInsertService implements IInsertService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private InsertService insertService;

    @Autowired
    private GroupFrequentRepository gfRepository;

    @Autowired
    private RequestRideRepository rqRepository;

    @Autowired
    private MetricService metricService;

    @Override
    public RideResponse insert(BookOnlineRequest data) throws BadRequestException, NotImplementedException {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Check input
        String check = data.checkInput();
        if (check != null) throw new BadRequestException(check);

        // Grid id of origin and destination
        int gridOriginId = MapUtility.convertToGridId(data.getLatOrigin(), data.getLngOrigin());
        int gridDesId = MapUtility.convertToGridId(data.getLatDestination(), data.getLngDestination());

        // If not have taxi ready
        List<GroupFrequent> listGroup = gfRepository.findAll();
        if (listGroup.isEmpty()) return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId());

        Double deltaDistanceMin = null;
        int indexOrigin = 0, indexDes = 0;
        GroupFrequent groupOptimal = null;
        List<Schedule> scheduleOfOptimal = null;
        ListEdgeCaseNormal listOptimalNormal = null;
        ListEdgeCaseSpecial listOptimalSpecial = null;

        for (GroupFrequent group : listGroup) {
            List<Schedule> schedules = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();
            boolean originLastPoint = false;
            for (int i = 0; i < length; i++) {
                if (i == length - 1) originLastPoint = true;
                boolean desLastPoint = false;
                for (int j = i ; j < length; j++) {
                    if (!checkCapacity(schedules, data.getCapacity(), i, j)) continue;
                    if (j == length - 1) desLastPoint = true;

                    // Calculator list edge
                    ListEdgeCaseNormal listNormal = null;
                    ListEdgeCaseSpecial listSpecial = null;
                    if (i == j) listSpecial = findAllEdgeCaseSpecial(schedules, i, gridOriginId, gridDesId, originLastPoint);
                    else listNormal = findAllEdgeCaseNormal(schedules, i, j, gridOriginId, gridDesId, desLastPoint);

                    if (!checkTime(schedules, i, j, listNormal, listSpecial, originLastPoint, desLastPoint, data)) continue;
                    double distanceIncrease = deltaDistance(listNormal, listSpecial);
                    if (deltaDistanceMin == null || distanceIncrease < deltaDistanceMin) {
                        deltaDistanceMin = distanceIncrease;
                        groupOptimal = group;
                        indexOrigin = i;
                        indexDes = j;
                        scheduleOfOptimal = schedules;
                        listOptimalSpecial = listSpecial;
                        listOptimalNormal = listNormal;
                    }
                }
            }
        }
        if (deltaDistanceMin == null) return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId());

        return insertService.createAndInsertRequest(data, groupOptimal, indexOrigin, indexDes, scheduleOfOptimal,
                listOptimalNormal, listOptimalSpecial, userDetails.getId());
    }

    private double deltaDistance(ListEdgeCaseNormal listNormal, ListEdgeCaseSpecial listSpecial) {
        // origin == destination
        if (listNormal == null) return listSpecial.distanceIncrease();
        else return listNormal.distanceIncrease();
    }

    private boolean checkCapacity(List<Schedule> schedules, int capacityRequest, int origin, int destination) {
        List<Schedule> listSchedule = schedules.subList(origin + 1, destination + 1);
        for (Schedule point : listSchedule) if (point.getCapacityAvailable() < capacityRequest) return false;
        return true;
    }

    private boolean checkTime(List<Schedule> schedules, int origin, int destination, ListEdgeCaseNormal listNormal,
                              ListEdgeCaseSpecial listSpecial, boolean originLastPoint, boolean desLastPoint,
                              BookOnlineRequest req) {
        // origin == destination
        if (listNormal == null) {
            if ((schedules.get(origin).getExpectedTime() + listSpecial.getIToOrigin().getDuration()) > req.getPickUpTimeLate()) return false;
            if (originLastPoint) return true;
            double timeIncrease = listSpecial.timeIncrease();
            for (int i = origin + 1; i < schedules.size(); i++) {
                if ((schedules.get(i).getExpectedTime() + timeIncrease) > schedules.get(i).getLateTime()) return false;
            }
        } else {
            // Check pick up late time
            if ((schedules.get(origin).getExpectedTime() + listNormal.getIToOrigin().getDuration()) > req.getPickUpTimeLate()) return false;

            // Check origin
            double timeIncreaseOrigin = listNormal.timeIncreaseOrigin();
            for (int i = origin + 1; i <= destination; i++) {
                if ((schedules.get(i).getExpectedTime() + timeIncreaseOrigin) > schedules.get(i).getLateTime()) return false;
            }

            // Check destination
            if (desLastPoint) return true;
            double timeIncreaseTotal = listNormal.timeIncrease();
            for (int i = destination + 1; i < schedules.size(); i++) {
                if ((schedules.get(i).getExpectedTime() + timeIncreaseTotal) > schedules.get(i).getLateTime()) return false;
            }
        }
        return true;
    }

    // Case origin == destination
    private ListEdgeCaseSpecial findAllEdgeCaseSpecial(List<Schedule> schedules, int origin, int originId, int desId,
                                                       boolean isLastPoint) {
        Schedule pointI = schedules.get(origin);
        QueryEdge iToOrigin = insertService.findIdGridWhenHaveDesId(pointI.getLat(), pointI.getLng(), originId);
        QueryEdge originToDes = insertService.findIdGridWhenHaveOriginAndDesId(originId, desId);
        if (!isLastPoint) {
            Schedule pointI1 = schedules.get(origin + 1);
            QueryEdge desToI1 = insertService.findIdGridWhenHaveOriginId(desId, pointI1.getLat(), pointI1.getLng());
            QueryEdge iToI1 = insertService.findIdGrid(pointI.getLat(), pointI.getLng(), pointI1.getLat(), pointI1.getLng());
            return new ListEdgeCaseSpecial(iToOrigin, originToDes, desToI1, iToI1);
        }
        return new ListEdgeCaseSpecial(iToOrigin, originToDes, null, null);
    }

    // Case origin != destination
    private ListEdgeCaseNormal findAllEdgeCaseNormal(List<Schedule> schedules, int origin, int destination,
                                                     int originId, int desId, boolean destinationIsLastPoint) {
        // origin
        Schedule pointI = schedules.get(origin);
        Schedule pointI1 = schedules.get(origin + 1);
        QueryEdge iToOrigin = insertService.findIdGridWhenHaveDesId(pointI.getLat(), pointI.getLng(), originId);
        QueryEdge originToI1 = insertService.findIdGridWhenHaveOriginId(originId, pointI1.getLat(), pointI1.getLng());
        QueryEdge iToI1 = insertService.findIdGrid(pointI.getLat(), pointI.getLng(), pointI1.getLat(), pointI1.getLng());

        // destination
        Schedule pointJ = schedules.get(destination);
        QueryEdge jToDes = insertService.findIdGridWhenHaveDesId(pointJ.getLat(), pointJ.getLng(), desId);
        if (!destinationIsLastPoint) {
            Schedule pointJ1 = schedules.get(destination + 1);
            QueryEdge desToJ1 = insertService.findIdGridWhenHaveOriginId(desId, pointJ1.getLat(), pointJ1.getLng());
            QueryEdge jToJ1 = insertService.findIdGrid(pointJ.getLat(), pointJ.getLng(), pointJ1.getLat(), pointJ1.getLng());
            return new ListEdgeCaseNormal(iToOrigin, originToI1, iToI1, jToDes, desToJ1, jToJ1);
        }
        return new ListEdgeCaseNormal(iToOrigin, originToI1, iToI1, jToDes, null, null);
    }


    @Override
    public String experiment(List<BookOnlineRequest> listRequest) {
        insertService.clearData();
        long count = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (BookOnlineRequest data : listRequest) {
            try {
                insert(data);
                count++;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        stopWatch.stop();
        return metricService.getAllScheduleMetricsPlain(stopWatch.getTotalTimeMillis(), count);
    }

}
