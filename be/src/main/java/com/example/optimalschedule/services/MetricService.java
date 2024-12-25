package com.example.optimalschedule.services;

import com.example.optimalschedule.constant.GroupType;
import com.example.optimalschedule.entity.GroupFrequent;
import com.example.optimalschedule.entity.GuidanceSchedule;
import com.example.optimalschedule.entity.RequestRide;
import com.example.optimalschedule.entity.Schedule;
import com.example.optimalschedule.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetricService {
    @Autowired
    private GuidanceScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleRepository schedulePlainRepository;

    @Autowired
    private ProphetInsertService insertService;

    @Autowired
    private GroupFrequentRepository gfRepository;

    @Autowired
    private RequestRideRepository rqRepository;

    @Autowired
    private DriverRepository driverRepository;

    public String getAllScheduleMetricsProphet(long totalRunningTime, long count) {
        List<GroupFrequent> listGroup = gfRepository.findAllByType(GroupType.GUIDANCE.getValue());
        List<Integer> groupIdDontServeOnline = new ArrayList<>();
        for (GroupFrequent group : listGroup) {
            List<GuidanceSchedule> schedules = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();
            boolean servedOnline = false;
            for (int i = 0; i <= length - 1; i++) {
                if (schedules.get(i).getScheduleId() == 2) {
                    servedOnline = true;
                    break;
                }
            }
            if (!servedOnline) {groupIdDontServeOnline.add(group.getId());}
        }
        //get total time running in roads - cost
        double totalTime = 0.0;
        for (GroupFrequent group : listGroup) {
            if (groupIdDontServeOnline.contains(group.getId())) {
                continue;
            }
            List<GuidanceSchedule> schedules = scheduleRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();
            for (int i = 0; i <= length - 2; i++) {
                totalTime += (schedules.get(i + 1).getExpectedTime() - schedules.get(i).getExpectedTime());
            }
        }
        List<RequestRide> requestNotServed = rqRepository.findByStatusId(4); // 4 is cannot served
        double betaForUnifiedCost = 10.0;
        double unifiedCost = requestNotServed.size() * betaForUnifiedCost + totalTime;
        return "Number of request served: " + count + "\n Number of groups: " + (listGroup.size() - groupIdDontServeOnline.size())
                + "\n Total running time: " + totalRunningTime
                + "\n Total cost: " + totalTime
                + "\n Number of request not served: " + requestNotServed.size() ;
    };
    public String getAllScheduleMetricsPlain(long totalRunningTime, long count) {
        List<GroupFrequent> listGroup = gfRepository.findAllByType(GroupType.ONLY_ONLINE.getValue());
        //get total time running in roads - cost
        double totalTime = 0.0;
        for (GroupFrequent group : listGroup) {
            List<Schedule> schedules = schedulePlainRepository.findByGroupIdOrderByExpectedTime(group.getId());
            int length = schedules.size();
            for (int i = 0; i <= length - 2; i++) {
                totalTime += (schedules.get(i + 1).getExpectedTime() - schedules.get(i).getExpectedTime());
            }
        }
        List<RequestRide> requestNotServed = rqRepository.findByStatusId(4); // 4 is cannot served
        double betaForUnifiedCost = 10.0;
        double unifiedCost = requestNotServed.size() * betaForUnifiedCost + totalTime;
        return "Number of request served: " + count + "\n Number of groups: " + (listGroup.size())
                + "\n Total running time: " + totalRunningTime
                + "\n Total cost: " + totalTime
                + "\n Number of request not served: " + requestNotServed.size() ;
    };
}
