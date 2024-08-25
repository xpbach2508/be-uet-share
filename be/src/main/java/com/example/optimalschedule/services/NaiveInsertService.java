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
import com.example.optimalschedule.model.response.ExperimentResponse;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.repository.GroupFrequentRepository;
import com.example.optimalschedule.repository.ScheduleRepository;
import com.example.optimalschedule.services.IServices.IInsertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class NaiveInsertService implements IInsertService {

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
            List<Schedule> schedules = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();

            // Initialization slack time
            HashMap<Integer, Double> slackTime = new HashMap<>();
            for (int i = length - 2; i >= 0; i--) {
                Schedule point = schedules.get(i + 1);
                if (i == length - 2) slackTime.put(i, point.getLateTime() - point.getExpectedTime());
                else {
                    double slackTimeAtI = point.getLateTime() - point.getExpectedTime();
                    if (slackTimeAtI < slackTime.get(i + 1)) slackTime.put(i, slackTimeAtI);
                    else slackTime.put(i, slackTime.get(i + 1));
                }
            }

            // Find optimal insert position
            boolean originLastPoint = false;
            for (int i = 0; i < length; i++) {
                if (i == length - 1) originLastPoint = true;

                // Check pick up time origin
                Schedule pointI = schedules.get(i);
                QueryEdge iToOrigin = insertService.findIdGridWhenHaveDesId(pointI.getLat(), pointI.getLng(), gridOriginId);
                if ((pointI.getExpectedTime() + iToOrigin.getDuration()) > data.getPickUpTimeLate()) continue;

                QueryEdge originToI1 = null, iToI1 = null;
                if (!originLastPoint) {
                    // Check capacity origin
                    if (pointI.getCapacityAvailable() < data.getCapacity()) continue;

                    // Check slack time of origin
                        // Calculator increase time when insert origin
                    Schedule pointI1 = schedules.get(i + 1);
                    originToI1 = insertService.findIdGridWhenHaveOriginId(gridOriginId, pointI1.getLat(), pointI1.getLng());
                    iToI1 = insertService.findIdGrid(pointI.getLat(), pointI.getLng(), pointI1.getLat(), pointI1.getLng());
                    if ((iToOrigin.getDuration() + originToI1.getDuration() - iToI1.getDuration()) > slackTime.get(i)) continue;
                }

                // Insert destination
                boolean desLastPoint = false;
                for (int j = i ; j < length; j++) {
                    if (j == length - 1) desLastPoint = true;

                    // Calculator list edge
                    ListEdgeCaseNormal listNormal = null;
                    ListEdgeCaseSpecial listSpecial = null;
                    // Check capacity in case normal
                    if (j > i) {
                        Schedule pointJ = schedules.get(j);
                        if (pointJ.getCapacityAvailable() < data.getCapacity()) break;
                        listNormal = findAllEdgeCaseNormal(schedules, j, gridDesId, iToOrigin, originToI1, iToI1, desLastPoint);
                    } else {
                        listSpecial = findAllEdgeCaseSpecial(iToOrigin, iToI1, schedules, i, gridOriginId, gridDesId, originLastPoint);
                    }
                    double deltaDistance = deltaDistance(listNormal, listSpecial);
                    if (deltaDistanceMin == null || deltaDistance < deltaDistanceMin) {
                        deltaDistanceMin = deltaDistance;
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
        if (deltaDistanceMin == null)
          return insertService.createNewGroup(data, gridOriginId, gridDesId, userDetails.getId());

        return insertService.createAndInsertRequest(data, groupOptimal, indexOrigin, indexDes, scheduleOfOptimal,
            listOptimalNormal, listOptimalSpecial, userDetails.getId());
    }

    // Case origin == destination
    private ListEdgeCaseSpecial findAllEdgeCaseSpecial(QueryEdge iToOrigin, QueryEdge iToI1, List<Schedule> schedules,
                                                       int origin, int originId, int desId, boolean originLastPoint) {
        QueryEdge originToDes = insertService.findIdGridWhenHaveOriginAndDesId(originId, desId);
        if (!originLastPoint) {
            Schedule pointI1 = schedules.get(origin + 1);
            QueryEdge desToI1 = insertService.findIdGridWhenHaveOriginId(desId, pointI1.getLat(), pointI1.getLng());
            return new ListEdgeCaseSpecial(iToOrigin, originToDes, desToI1, iToI1);
        }
        return new ListEdgeCaseSpecial(iToOrigin, originToDes, null, null);
    }

    // Case origin != destination
    private ListEdgeCaseNormal findAllEdgeCaseNormal(List<Schedule> schedules, int destination, int desId,
                                                     QueryEdge iToOrigin, QueryEdge originToI1, QueryEdge iToI1,
                                                     boolean destinationIsLastPoint) {
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

    private double deltaDistance(ListEdgeCaseNormal listNormal, ListEdgeCaseSpecial listSpecial) {
        // origin == destination
        if (listNormal == null) return listSpecial.distanceIncrease();
        else return listNormal.distanceIncrease();
    }

    @Override
    public int experiment(List<BookOnlineRequest> listRequest) {
        int count = 0;
        for (BookOnlineRequest data : listRequest) {
            try {
                insert(data);
                count++;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return count;
    }

}
