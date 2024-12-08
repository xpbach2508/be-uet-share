package com.example.optimalschedule.controller;

import com.example.optimalschedule.model.response.ScheduleAdminProphetResponse;
import com.example.optimalschedule.model.response.ScheduleAdminResponse;
import com.example.optimalschedule.model.response.ScheduleDriverResponse;
import com.example.optimalschedule.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin
@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> scheduleOfDriver() {
        List<ScheduleDriverResponse> schedules = scheduleService.scheduleOfDriver();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> scheduleAllDriver() {
        List<ScheduleAdminResponse> schedules = scheduleService.scheduleAllDriver();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/admin-prophet")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> scheduleAllProphet() {
        List<ScheduleAdminProphetResponse> schedules = scheduleService.scheduleAllDriverProphet();
        return ResponseEntity.ok(schedules);
    }

}
