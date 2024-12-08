package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.NotImplementedException;
import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.entity.*;
import com.example.optimalschedule.gripmap.MapUtility;
import com.example.optimalschedule.model.ListEdgeCaseNormal;
import com.example.optimalschedule.model.ListEdgeCaseSpecial;
import com.example.optimalschedule.model.QueryEdge;
import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.request.PredictedRequest;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ProphetLinearInsertService {

    @Autowired
    private GuidanceScheduleRepository scheduleRepository;

    @Autowired
    private ProphetInsertService insertService;

    @Autowired
    private GroupFrequentRepository gfRepository;

    @Autowired
    private RequestRideRepository rqRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private MetricService metricService;

    public RideResponse insertPredict(PredictedRequest data) throws BadRequestException, NotImplementedException {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check input
        String check = data.checkInput();
        if (check != null) throw new BadRequestException(check);

        int gridOriginId = MapUtility.convertToGridId(data.getLatOrigin(), data.getLngOrigin());
        int gridDesId = MapUtility.convertToGridId(data.getLatDestination(), data.getLngDestination());
        // If not have taxi ready
        List<GroupFrequent> listGroup = gfRepository.findAllByType(0);
        if (listGroup.isEmpty())
          return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId(), 0, 3);
        RideResponse newRide = insertProphet(userDetails, data, listGroup, gridOriginId, gridDesId, 3); //3 is predicted
        List<GroupFrequent> listHypoGroup = gfRepository.findAllByType(0);
        for (GroupFrequent group : listHypoGroup) {
            Driver driver = driverRepository.findFirstById(group.getDriverId());
            List<GuidanceSchedule> schedulePredict = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int capacityAvailable = driver.getSeat() - 1;
            for (GuidanceSchedule schedule : schedulePredict) {
                schedule.setCapacityAvailable(capacityAvailable);
                scheduleRepository.save(schedule);
            }
        }
        return newRide;
    }

    public RideResponse insertOnlineProphet(PredictedRequest data) throws BadRequestException, NotImplementedException {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check input
        String check = data.checkInput();
        if (check != null) throw new BadRequestException(check);

        int gridOriginId = MapUtility.convertToGridId(data.getLatOrigin(), data.getLngOrigin());
        int gridDesId = MapUtility.convertToGridId(data.getLatDestination(), data.getLngDestination());
        // If not have taxi ready
        List<GroupFrequent> listGroup = gfRepository.findAllByType(0);
        if (listGroup.isEmpty())
            return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId(), 0, 2);
        return insertProphet(userDetails, data, listGroup, gridOriginId, gridDesId, 2);
    }

    private RideResponse insertProphet(UserDetailsImpl userDetails, PredictedRequest data, List<GroupFrequent> listGroup, int gridOriginId, int gridDesId, int requestType) { // 2 is online, 3 is predicted
        // Grid id of origin and destination
        QueryEdge originToDes = insertService.findIdGridWhenHaveOriginAndDesId(gridOriginId, gridDesId);
        double startTimeDes = data.getPickUpTime() + originToDes.getDuration();
        double lateTimeDes = data.getPickUpTimeLate() + originToDes.getDuration(); //er

        Double deltaDistanceMin = null;
        int indexOrigin = 0, indexDes = 0;
        GroupFrequent groupOptimal = null;
        List<GuidanceSchedule> scheduleOfOptimal = null;
        ListEdgeCaseNormal listOptimalNormal = null;
        ListEdgeCaseSpecial listOptimalSpecial = null;

        for (GroupFrequent group : listGroup) {
            List<GuidanceSchedule> schedules = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();

            // Initialization detour and plc
            double[] detour = new double[length]; // Với destination cố định, time tăng lên min khi insert origin
            int[] plc = new int[length];
            detour[0] = Double.MAX_VALUE;
            plc[0] = -1;

            int startDes = 0;
            int endDes = length - 1;

//            List<Integer> boundDestination = findBeginAndEndJ(schedules, data, length, gridDesId, data.getPickUpTime(), lateTimeDes);
//            startDes = boundDestination.get(0);
//            endDes = boundDestination.get(1);
//            if (startDes == -1) startDes = 0;
//            if (endDes < 0) endDes = length - 1;
////            startDes = 0; // because the latetime doesn sorted ascending, cause by distance length from origin to destination
//            System.out.println("Find startDes + endDes: " + startDes + " - " + endDes);


            // Initialization slack time
            HashMap<Integer, Double> slackTime = new HashMap<>();
            for (int i = length - 2; i >= 0; i--) {
                GuidanceSchedule point = schedules.get(i + 1);
                double slackTimeK = 0.0;
                if (i == length - 2) slackTimeK = point.getLateTime() - point.getExpectedTime();
                else {
                    double slackTimeAtI = point.getLateTime() - point.getExpectedTime();
                    if (slackTimeAtI < slackTime.get(i + 1)) slackTimeK = slackTimeAtI;
                    else slackTimeK = slackTime.get(i + 1);
                }
                slackTime.put(i, slackTimeK + point.getWait());
            }

            //Init swait array
            HashMap<Integer, Double> swait = new HashMap<>();
            swait.put(length, 0.0);
            swait.put(length - 1, schedules.get(length - 1).getWait());
            for (int i = length - 2; i >= 0; i--) {
                GuidanceSchedule point = schedules.get(i);
                swait.put(i, point.getWait() + swait.get(i + 1));
            }

            // Duyệt các point destination
            boolean desLastPoint = false;
            for (int j = startDes; j <= endDes; j++) {
                if (j == length - 1) desLastPoint = true;

                // Case special: origin == destination
                ListEdgeCaseSpecial listSpecial = findAllEdgeCaseSpecial(schedules, j, gridOriginId, gridDesId,
                        desLastPoint, originToDes);
                double increaseDuration = listSpecial.timeIncrease();
                double expectedOrigin = listSpecial.getIToOrigin().getDuration() + schedules.get(j).getExpectedTime() + schedules.get(j).getWait();
                double waitR = Math.max(0.0, data.getPickUpTime() - (expectedOrigin));
                if (requestType == 2) waitR = 0.0;
                if (expectedOrigin <= data.getPickUpTimeLate() && (desLastPoint || increaseDuration + waitR < slackTime.get(j))) {  //page 25 still lack lemma 11-3 conditio
                    double increaseDistance = listSpecial.distanceIncrease();
                    if (deltaDistanceMin == null || increaseDistance < deltaDistanceMin) {
                        deltaDistanceMin = increaseDistance;
                        indexOrigin = j;
                        indexDes = j;
                        groupOptimal = group;
                        scheduleOfOptimal = schedules;
                        listOptimalSpecial = listSpecial;
                        listOptimalNormal = null;
                    }
                }

                // Case normal
                GuidanceSchedule pointJ = schedules.get(j);
                QueryEdge jToDes = insertService.findIdGridWhenHaveDesId(pointJ.getLat(), pointJ.getLng(), gridDesId);
                if (j > 0 && plc[j] != -1 && plc[j] != j) {
                    ListEdgeCaseNormal listNormal = findAllEdgeCaseNormal(schedules, plc[j], j, gridOriginId, gridDesId,
                            listSpecial, desLastPoint, jToDes);
                    if (checkCondition(schedules, data, slackTime, detour, j, listNormal, desLastPoint, lateTimeDes, startTimeDes, swait)) {
                        double increaseDistance = listNormal.distanceIncrease();
                        if (deltaDistanceMin == null || increaseDistance < deltaDistanceMin) {
                            deltaDistanceMin = increaseDistance;
                            indexOrigin = plc[j];
                            indexDes = j;
                            groupOptimal = group;
                            scheduleOfOptimal = schedules;
                            listOptimalSpecial = null;
                            listOptimalNormal = listNormal;
                        }
                    }
                }
                if (desLastPoint) continue;
                if ((schedules.get(j).getExpectedTime() + jToDes.getDuration()) > lateTimeDes) break;

                // Update detour and plc index j + 1
                GuidanceSchedule pointJ1 = schedules.get(j + 1);
                if (pointJ.getCapacityAvailable() < data.getCapacity()) {
                    detour[j + 1] = Double.MAX_VALUE;
                    plc[j + 1] = -1;
                } else {
                    QueryEdge jToOrigin = listSpecial.getIToOrigin();
                    QueryEdge originToJ1 = insertService.findIdGridWhenHaveOriginId(gridOriginId, pointJ1.getLat(),
                            pointJ1.getLng());
                    QueryEdge jToJ1 = listSpecial.getIToI1();
                    double increaseTime = jToOrigin.getDuration() + originToJ1.getDuration() - jToJ1.getDuration() - swait.get(j + 1);
                    double previousDetour = detour[j];
                    if (increaseTime > slackTime.get(j)) {
                        detour[j + 1] = previousDetour;
                        plc[j + 1] = plc[j];
                    } else if (previousDetour < increaseTime) {
                        detour[j + 1] = previousDetour;
                        plc[j + 1] = plc[j];
                    } else {
                        detour[j + 1] = increaseTime;
                        plc[j + 1] = j;
                    }
                }
            }
        }
        if (deltaDistanceMin == null) {
            return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId(), 0, requestType);
//            if (requestType == 3) return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId(), 0, requestType);
//            return new RideResponse(1, data.getAddressStart(), data.getAddressEnd(),
//                    0.0, 0.0, "0.0",
//                    "0.0", "0.0", 0, "0", 0.0,
//                    0, false, 1, LocalDate.now());

        }
        return insertService.createAndInsertRequest(data, groupOptimal, indexOrigin, indexDes, scheduleOfOptimal,
            listOptimalNormal, listOptimalSpecial, userDetails.getId(), requestType);
    }

    private boolean checkCondition(List<GuidanceSchedule> schedules, PredictedRequest data, HashMap<Integer, Double> slackTime,
                                   double[] detour, int destination, ListEdgeCaseNormal listNormal,
                                   boolean desLastPoint, double lateTimeDes, double startTimeDes, HashMap<Integer, Double> swait) {
        // Check capacity
        GuidanceSchedule pointJ = schedules.get(destination);
        if (pointJ.getCapacityAvailable() < data.getCapacity()) return false;

        // Check late time destination
        double expectedTimeDes = pointJ.getExpectedTime() + + pointJ.getWait() + Math.max(detour[destination] + swait.get(destination + 1), 0.0) + listNormal.getJToDes().getDuration();
//        if (pointJ.getScheduleId() == 3) lateTimeDes = ;
//        if (expectedTimeDes > lateTimeDes || expectedTimeDes < startTimeDes) return false;
        if (expectedTimeDes > lateTimeDes) return false;


        // Check slack time
        if (desLastPoint) return true;
        QueryEdge jToDes = listNormal.getJToDes();
        QueryEdge desToJ1 = listNormal.getDesToJ1();
        QueryEdge jToJ1 = listNormal.getJToJ1();
        double increaseTimeTotalDes = Math.max(detour[destination] + swait.get(destination + 1), 0) + (jToDes.getDuration() + desToJ1.getDuration() - jToJ1.getDuration());
        return increaseTimeTotalDes <= slackTime.get(destination);
    }

    // Case origin == destination
    private ListEdgeCaseSpecial findAllEdgeCaseSpecial(List<GuidanceSchedule> schedules, int origin, int originId, int desId,
                                                       boolean desLastPoint, QueryEdge originToDes) {
        GuidanceSchedule pointI = schedules.get(origin);
        QueryEdge iToOrigin = insertService.findIdGridWhenHaveDesId(pointI.getLat(), pointI.getLng(), originId);
        if (iToOrigin == null) {
            System.out.println(schedules.size());
            System.out.println(origin);
            System.out.println(pointI);
            System.out.println(originId);
        }
        if (!desLastPoint) {
            GuidanceSchedule pointI1 = schedules.get(origin + 1);
            QueryEdge desToI1 = insertService.findIdGridWhenHaveOriginId(desId, pointI1.getLat(), pointI1.getLng());
            QueryEdge iToI1 = insertService.findIdGrid(pointI.getLat(), pointI.getLng(), pointI1.getLat(),  pointI1.getLng());
            return new ListEdgeCaseSpecial(iToOrigin, originToDes, desToI1, iToI1);
        }
        return new ListEdgeCaseSpecial(iToOrigin, originToDes, null, null);
    }

    // Case origin != destination
    private ListEdgeCaseNormal findAllEdgeCaseNormal(List<GuidanceSchedule> schedules, int origin, int destination,
                                                     int originId, int desId, ListEdgeCaseSpecial listSpecial,
                                                     boolean desLastPoint, QueryEdge jToDes) {
        // origin
        GuidanceSchedule pointI = schedules.get(origin);
        GuidanceSchedule pointI1 = schedules.get(origin + 1);
        QueryEdge iToOrigin = insertService.findIdGridWhenHaveDesId(pointI.getLat(), pointI.getLng(), originId);
        QueryEdge originToI1 = insertService.findIdGridWhenHaveOriginId(originId, pointI1.getLat(), pointI1.getLng());
        QueryEdge iToI1 = insertService.findIdGrid(pointI.getLat(), pointI.getLng(), pointI1.getLat(), pointI1.getLng());

        if (!desLastPoint) {
            QueryEdge desToJ1 = listSpecial.getDesToI1();
            QueryEdge jToJ1 = listSpecial.getIToI1();
            return new ListEdgeCaseNormal(iToOrigin, originToI1, iToI1, jToDes, desToJ1, jToJ1);
        }
        return new ListEdgeCaseNormal(iToOrigin, originToI1, iToI1, jToDes, null, null);
    }

    private List<Integer> findBeginAndEndJ(List<GuidanceSchedule> schedules, PredictedRequest data, int length, int gridDesId, Double showTimeR, Double deadlineR) {
        // Find beginJ
        int beginJ = -1;
        int start = 0, end = length - 1;
        int mid = -1;
        while (start <= end) {
            mid = start + (end - start) / 2;
            GuidanceSchedule pointMid = schedules.get(mid);
            Double deadlineMid = pointMid.getLateTime();
            // Case <
           if (deadlineMid > showTimeR) {
                end = mid - 1;
                beginJ = mid;
            } else {
               start = mid + 1;
           }
        }

        int endJ = -1;
        start = 0;
        end = length - 1;
        while (start <= end) {
            mid = start + (end - start) / 2;
            GuidanceSchedule pointMid = schedules.get(mid);
            Double expectedTime = pointMid.getExpectedTime();
            // Case <
            if (expectedTime >= deadlineR) {
                end = mid - 1;
                endJ = mid;
            } else {
                start = mid + 1;
            }
        }
        
        return new ArrayList<>(List.of(beginJ, endJ - 1));
    }

    public String experimentPredict(List<PredictedRequest> listRequest) {
        insertService.clearData();
        long count = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (PredictedRequest data : listRequest) {
            try {
                insertPredict(data);
                count++;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        stopWatch.stop();
        List<GroupFrequent> listHypoGroup = gfRepository.findAllByType(0);
        for (GroupFrequent group : listHypoGroup) {
            Driver driver = driverRepository.findFirstById(group.getDriverId());
            List<GuidanceSchedule> schedulePredict = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int capacityAvailable = driver.getSeat() - 1;
            for (GuidanceSchedule schedule : schedulePredict) {
                schedule.setCapacityAvailable(capacityAvailable);
                schedule.setLateTime(99.9);
                scheduleRepository.save(schedule);
            }
        }
        List<GroupFrequent> listGroup = gfRepository.findAllByType(0);
        //get total time running in roads - cost
        double totalTime = 0.0;
        for (GroupFrequent group : listGroup) {
            List<GuidanceSchedule> schedules = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();
            for (int i = 0; i <= length - 2; i++) {
                totalTime += (schedules.get(i + 1).getExpectedTime() - schedules.get(i).getExpectedTime());
            }
        }
        List<RequestRide> requestNotServed = rqRepository.findByStatusId(4);
        double betaForUnifiedCost = 10.0;
        double unifiedCost = requestNotServed.size() * betaForUnifiedCost + totalTime;
        return "Number of request served: " + count + "\n Number of groups: " + listGroup.size()
                + "\n Total running time: " + stopWatch.getTotalTimeMillis()
                + "\n Total cost: " + unifiedCost
                + "\n Number of request not served: " + requestNotServed.size() ;
    }

    public String experimentOnlineProphet(List<PredictedRequest> listRequest) {
        long count = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (PredictedRequest data : listRequest) {
            try {
                RideResponse result = insertOnlineProphet(data);
                if (result.getCost() != 0.0 ) count++;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        stopWatch.stop();
        return metricService.getAllScheduleMetricsProphet(stopWatch.getTotalTimeMillis(), count); // 0 is prophet
    }

}

















