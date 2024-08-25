package com.example.optimalschedule.controller;

import com.example.optimalschedule.entity.Trip;
import com.example.optimalschedule.entity.Waypoint;
import com.example.optimalschedule.model.request.ShareFrequentRequest;
import com.example.optimalschedule.model.response.MessageResponse;
import com.example.optimalschedule.services.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/share")
@PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @PostMapping("/share_frequent")
    public ResponseEntity<?> shareFrequent(@RequestBody ShareFrequentRequest data) {
        if (data.getTypeShare()) {
            if (shareService.shareFrequent(data.getRouteId())) return ResponseEntity.ok().body(new MessageResponse("Thành công!"));
            else return ResponseEntity.ok().body(new MessageResponse("Hiện chưa có taxi phù hợp!"));
        } else {
            shareService.cancelShareFrequent(data.getRouteId());
            return ResponseEntity.ok().body(new MessageResponse("Tắt chia sẻ thành công!"));
        }
    }

    @PostMapping("/waypoint")
    @ResponseBody
    public ResponseEntity<?> addWaypoint(@RequestBody Waypoint waypoint) {
        return ResponseEntity.ok().body(shareService.addWaypoint(waypoint));
    }

    @PostMapping("/trip")
    @ResponseBody
    public ResponseEntity<?> addTrip(@RequestBody Trip trip) {
        return ResponseEntity.ok().body(shareService.addTrip(trip));
    }

}
