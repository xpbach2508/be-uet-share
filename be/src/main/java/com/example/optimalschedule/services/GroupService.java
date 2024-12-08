package com.example.optimalschedule.services;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.GroupFrequent;
import com.example.optimalschedule.entity.GuidanceSchedule;
import com.example.optimalschedule.entity.Schedule;
import com.example.optimalschedule.repository.DriverRepository;
import com.example.optimalschedule.repository.GroupFrequentRepository;
import com.example.optimalschedule.repository.GuidanceScheduleRepository;
import com.example.optimalschedule.services.IServices.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupFrequentRepository gfRepository;

    @Autowired
    private GuidanceScheduleRepository scheduleRepository;

    public int getAllOnlineGroup() {
        List<GroupFrequent> listGroup = gfRepository.findAllByType(0);
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
        return listGroup.size() - groupIdDontServeOnline.size();
    }
}
