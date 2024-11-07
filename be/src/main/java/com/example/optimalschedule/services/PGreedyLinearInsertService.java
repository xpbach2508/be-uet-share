package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.NotImplementedException;
import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.entity.GroupFrequent;
import com.example.optimalschedule.entity.Schedule;
import com.example.optimalschedule.gripmap.MapUtility;
import com.example.optimalschedule.model.ListEdgeCaseNormal;
import com.example.optimalschedule.model.ListEdgeCaseSpecial;
import com.example.optimalschedule.model.QueryEdge;
import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.repository.GroupFrequentRepository;
import com.example.optimalschedule.repository.ScheduleRepository;
import com.example.optimalschedule.services.IServices.IInsertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.List;

@Service
public class PGreedyLinearInsertService implements IInsertService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private InsertService insertService;

    @Autowired
    private GroupFrequentRepository gfRepository;

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
        QueryEdge originToDes = insertService.findIdGridWhenHaveOriginAndDesId(gridOriginId, gridDesId);
        double startTimeDes = data.getPickUpTime() + originToDes.getDuration();
        double lateTimeDes = data.getPickUpTimeLate() + originToDes.getDuration();

        // If not have taxi ready
        List<GroupFrequent> listGroup = gfRepository.findAll();
        if (listGroup.size() == 0)
            return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId());

        Double deltaDistanceMin = null;
        int indexOrigin = 0, indexDes = 0;
        GroupFrequent groupOptimal = null;
        List<Schedule> scheduleOfOptimal = null;
        ListEdgeCaseNormal listOptimalNormal = null;
        ListEdgeCaseSpecial listOptimalSpecial = null;

        for (GroupFrequent group : listGroup) {
            if (scheduleRepository.existsByGroupIdAndPassengerId(group.getId(), userDetails.getId())) continue;
            List<Schedule> schedules = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();

            // Find start index of origin
            int startOrigin = findIndexStartOrigin(schedules, data, length, gridDesId);
            if (startOrigin == -1) continue;

            // Initialization detour and plc
            double[] detour = new double[length]; // Với destination cố định, time tăng lên min khi insert origin
            int[] plc = new int[length];
            detour[startOrigin] = Double.MAX_VALUE;
            plc[startOrigin] = -1;

            // Initialization slack time
            HashMap<Integer, Double> slackTime = new HashMap<>();
            for (int i = length - 2; i >= startOrigin; i--) {
                Schedule point = schedules.get(i + 1);
                if (i == length - 2) slackTime.put(i, point.getLateTime() - point.getExpectedTime());
                else {
                    double slackTimeAtI = point.getLateTime() - point.getExpectedTime();
                    if (slackTimeAtI < slackTime.get(i + 1)) slackTime.put(i, slackTimeAtI);
                    else slackTime.put(i, slackTime.get(i + 1));
                }
            }

            // Duyệt các point destination
            boolean desLastPoint = false;
            for (int j = startOrigin; j < length; j++) {
                if (j == length - 1) desLastPoint = true;

                // Case special: origin == destination
                ListEdgeCaseSpecial listSpecial = findAllEdgeCaseSpecial(schedules, j, gridOriginId, gridDesId,
                        desLastPoint, originToDes);
                double increaseDuration = listSpecial.timeIncrease();
                double expectedOrigin = listSpecial.getIToOrigin().getDuration() + schedules.get(j).getExpectedTime();
                if (expectedOrigin <= data.getPickUpTimeLate() && expectedOrigin >= data.getPickUpTime()
                        && (desLastPoint || increaseDuration < slackTime.get(j))) {
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
                Schedule pointJ = schedules.get(j);
                QueryEdge jToDes = insertService.findIdGridWhenHaveDesId(pointJ.getLat(), pointJ.getLng(), gridDesId);
                if (j > startOrigin && plc[j] != -1 && plc[j] != j) {
                    ListEdgeCaseNormal listNormal = findAllEdgeCaseNormal(schedules, plc[j], j, gridOriginId, gridDesId,
                            listSpecial, desLastPoint, jToDes);
                    if (checkCondition(schedules, data, slackTime, detour, j, listNormal, desLastPoint, lateTimeDes, startTimeDes)) {
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
                Schedule pointJ1 = schedules.get(j + 1);
                if (pointJ.getCapacityAvailable() < data.getCapacity()) {
                    detour[j + 1] = Double.MAX_VALUE;
                    plc[j + 1] = -1;
                } else {
                    QueryEdge jToOrigin = listSpecial.getIToOrigin();
                    QueryEdge originToJ1 = insertService.findIdGridWhenHaveOriginId(gridOriginId, pointJ1.getLat(),
                            pointJ1.getLng());
                    QueryEdge jToJ1 = listSpecial.getIToI1();
                    double increaseTime = jToOrigin.getDuration() + originToJ1.getDuration() - jToJ1.getDuration();
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
        if (deltaDistanceMin == null)
            return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId());

        return insertService.createAndInsertRequest(data, groupOptimal, indexOrigin, indexDes, scheduleOfOptimal,
                listOptimalNormal, listOptimalSpecial, userDetails.getId());
    }

    private int findIndexStartOrigin(List<Schedule> schedules, BookOnlineRequest data, int length, int gridDesId) {
        // Find range of origin
        int start = 0, end = length - 1;
        int mid = -1;
        while (start < end) {
            mid = start + (end - start) / 2;
            Schedule pointMid = schedules.get(mid);
            QueryEdge midToOrigin = insertService.findIdGridWhenHaveDesId(pointMid.getLat(), pointMid.getLng(), gridDesId);
            double expectedOrigin = pointMid.getExpectedTime() + midToOrigin.getDuration();
            // Case <
            if (expectedOrigin < data.getPickUpTime()) {
                start = mid + 1;
                continue;
            }
            // Case >=
            break;
        }

        // Find start index of origin
        for (int i = start; i <= end; i++) {
            // Check pick up time
            Schedule pointI = schedules.get(i);
            QueryEdge iToOrigin = insertService.findIdGridWhenHaveDesId(pointI.getLat(), pointI.getLng(), gridDesId);
            double expectedOrigin = pointI.getExpectedTime() + iToOrigin.getDuration();
            if (expectedOrigin < data.getPickUpTime()) continue;

            // Check pick up time late
            if (expectedOrigin > data.getPickUpTimeLate()) return -1;

            // Check capacity
            if (i == end) return i;
            if (schedules.get(i + 1).getCapacityAvailable() >= data.getCapacity()) return i;
        }
        return -1;
    }

    private boolean checkCondition(List<Schedule> schedules, BookOnlineRequest data, HashMap<Integer, Double> slackTime,
                                   double[] detour, int destination, ListEdgeCaseNormal listNormal,
                                   boolean desLastPoint, double lateTimeDes, double startTimeDes) {
        // Check capacity
        Schedule pointJ = schedules.get(destination);
        if (pointJ.getCapacityAvailable() < data.getCapacity()) return false;

        // Check late time destination
        double expectedTimeDes = pointJ.getExpectedTime() + detour[destination] + listNormal.getJToDes().getDuration();
        if (expectedTimeDes > lateTimeDes || expectedTimeDes < startTimeDes) return false;

        // Check slack time
        if (desLastPoint) return true;
        QueryEdge jToDes = listNormal.getJToDes();
        QueryEdge desToJ1 = listNormal.getDesToJ1();
        QueryEdge jToJ1 = listNormal.getJToJ1();
        double increaseTimeTotal = detour[destination] + (jToDes.getDuration() + desToJ1.getDuration() - jToJ1.getDuration());
        return increaseTimeTotal <= slackTime.get(destination);
    }

    // Case origin == destination
    private ListEdgeCaseSpecial findAllEdgeCaseSpecial(List<Schedule> schedules, int origin, int originId, int desId,
                                                       boolean desLastPoint, QueryEdge originToDes) {
        Schedule pointI = schedules.get(origin);
        QueryEdge iToOrigin = insertService.findIdGridWhenHaveDesId(pointI.getLat(), pointI.getLng(), originId);
        if (!desLastPoint) {
            Schedule pointI1 = schedules.get(origin + 1);
            QueryEdge desToI1 = insertService.findIdGridWhenHaveOriginId(desId, pointI1.getLat(), pointI1.getLng());
            QueryEdge iToI1 = insertService.findIdGrid(pointI.getLat(), pointI.getLng(), pointI1.getLat(),  pointI1.getLng());
            return new ListEdgeCaseSpecial(iToOrigin, originToDes, desToI1, iToI1);
        }
        return new ListEdgeCaseSpecial(iToOrigin, originToDes, null, null);
    }

    // Case origin != destination
    private ListEdgeCaseNormal findAllEdgeCaseNormal(List<Schedule> schedules, int origin, int destination,
                                                     int originId, int desId, ListEdgeCaseSpecial listSpecial,
                                                     boolean desLastPoint, QueryEdge jToDes) {
        // origin
        Schedule pointI = schedules.get(origin);
        Schedule pointI1 = schedules.get(origin + 1);
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

    @Override
    public int experiment(List<BookOnlineRequest> listRequest) {
        int count = 0;
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
        System.out.println(stopWatch.getTotalTimeMillis());

        return count;
    }

}
