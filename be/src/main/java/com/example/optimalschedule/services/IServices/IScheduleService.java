package com.example.optimalschedule.services.IServices;

import com.example.optimalschedule.common.exception.NotFoundException;
import com.example.optimalschedule.model.response.ScheduleAdminResponse;
import com.example.optimalschedule.model.response.ScheduleDriverResponse;

import java.util.List;

public interface IScheduleService {

    List<ScheduleDriverResponse> scheduleOfDriver() throws NotFoundException;

    List<ScheduleAdminResponse> scheduleAllDriver();
}
