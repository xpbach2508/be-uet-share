package com.example.optimalschedule.services;

import com.example.optimalschedule.common.exception.NotFoundException;
import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.constant.Message;
import com.example.optimalschedule.entity.GroupFrequent;
import com.example.optimalschedule.model.response.ScheduleAdminResponse;
import com.example.optimalschedule.model.response.ScheduleDriverResponse;
import com.example.optimalschedule.repository.GroupFrequentRepository;
import com.example.optimalschedule.repository.ScheduleRepository;
import com.example.optimalschedule.services.IServices.IScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService implements IScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GroupFrequentRepository gfRepository;

    @Override
    public List<ScheduleDriverResponse> scheduleOfDriver() throws NotFoundException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GroupFrequent gf = gfRepository.findByDriverId(userDetails.getId());
        if (gf == null) throw new NotFoundException(Message.NOT_FOUND_GROUP);
        List<ScheduleDriverResponse> result = scheduleRepository.getScheduleByGroupIdOrderByExpectedTime(gf.getId());
        if (result == null || result.size() == 0) throw new NotFoundException(Message.NOT_FOUND_SCHEDULE);
        return result;
    }

    @Override
    public List<ScheduleAdminResponse> scheduleAllDriver() {
        List<ScheduleAdminResponse> result = scheduleRepository.getAllScheduleOrderByExpectedTime();
        if (result == null || result.size() == 0) throw new NotFoundException(Message.NOT_FOUND_ALL_SCHEDULE);
        return result;
    }

}
