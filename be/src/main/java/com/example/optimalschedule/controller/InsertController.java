package com.example.optimalschedule.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.request.PredictedRequest;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.services.*;
import com.example.optimalschedule.socketio.SocketIOEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.optimalschedule.socketio.SocketIOEvents.MESSAGE_FROM_SERVER;

@Controller
@CrossOrigin
@RequestMapping("/insert")
@PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
public class InsertController {

    @Autowired
    private BasicInsertService basicInsertService;


    @Autowired
    private LinearInsertService linearInsertService;

    @Autowired
    private PGreedyLinearInsertService pGreedyLinearInsertService;

    @Autowired
    private ProphetLinearInsertService predictedInsertService;

    @Autowired
    private SocketIOEvents socketIOEvents;

    @PostMapping("/basic")
    public ResponseEntity<?> basicInsertRequest(@RequestBody BookOnlineRequest data) {
        data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(basicInsertService.insert(data));
    }

    @PostMapping("/experiment_basic")
    public ResponseEntity<?> experimentBasicInsertRequest(@RequestBody List<BookOnlineRequest> listRequest) {
        for (BookOnlineRequest data : listRequest) data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(basicInsertService.experiment(listRequest));
    }

    @PostMapping("/linear_dp")
    public ResponseEntity<?> linearDPInsert(@RequestBody BookOnlineRequest data) {
        data.initializePickUpTimeLate();
        RideResponse request = linearInsertService.insert(data);
        this.socketIOEvents.server.getBroadcastOperations().sendEvent(MESSAGE_FROM_SERVER, request.getGroupId());
        return ResponseEntity.ok().body(request);
    }

    @PostMapping("/testsocket")
    public ResponseEntity<?> testSocket(@RequestBody int data) {
        this.socketIOEvents.server.getBroadcastOperations().sendEvent("testsocket", data);
        return ResponseEntity.ok().body("testsocket");
    }

    @PostMapping("/experiment_linear_dp")
    public ResponseEntity<?> experimentLinearDPInsert(@RequestBody List<BookOnlineRequest> listRequest) {
        for (BookOnlineRequest data : listRequest) data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(linearInsertService.experiment(listRequest));
    }

    @PostMapping("/prune_greedy_linear_dp")
    public ResponseEntity<?> pGreedyLinearDPInsert(@RequestBody BookOnlineRequest data) {
        data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(pGreedyLinearInsertService.insert(data));
    }

    @PostMapping("/experiment_prune_greedy_linear_dp")
    public ResponseEntity<?> experimentPGreedyLinearDPInsert(@RequestBody List<BookOnlineRequest> listRequest) {
        for (BookOnlineRequest data : listRequest) data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(pGreedyLinearInsertService.experiment(listRequest));
    }

    @PostMapping("/prophet_linear_dp")
    public ResponseEntity<?> prophetLinearDPInsert(@RequestBody PredictedRequest data) {
        data.initializePickUpTimeLate();
        data.initShowTime();
        RideResponse request = predictedInsertService.insertOnlineProphet(data);
        this.socketIOEvents.server.getBroadcastOperations().sendEvent(MESSAGE_FROM_SERVER, request.getGroupId());
        return ResponseEntity.ok().body(request);
    }

    @PostMapping("/experiment_prophet_linear_dp")
    public ResponseEntity<?> experimentOnlineProphetLinearDPInsert(@RequestBody List<PredictedRequest> listRequest) {
        for (PredictedRequest data : listRequest) {
            data.initializePickUpTimeLate();
            data.initShowTime();
        }
        return ResponseEntity.ok().body(predictedInsertService.experimentOnlineProphet(listRequest));
    }

    @PostMapping("/experiment_predicted_linear_dp")
    public ResponseEntity<?> experimentPredictedLinearDPInsert(@RequestBody List<PredictedRequest> listRequest) {
        for (PredictedRequest data : listRequest) {
            data.initializePickUpTimeLate();
            data.initShowTime();
        }
        return ResponseEntity.ok().body(predictedInsertService.experimentPredict(listRequest));
    }

    @PostMapping("/predicted_linear_dp")
    public ResponseEntity<?> predictedLinearDPInsert(@RequestBody PredictedRequest data) {
        data.initializePickUpTimeLate();
        data.initShowTime();
        return ResponseEntity.ok().body(predictedInsertService.insertPredict(data));
    }
}
