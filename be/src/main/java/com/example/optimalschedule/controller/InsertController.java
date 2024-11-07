package com.example.optimalschedule.controller;

import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.request.PredictedRequest;
import com.example.optimalschedule.model.response.RideResponse;
import com.example.optimalschedule.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/insert")
// @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
public class InsertController {

    @Autowired
    private BasicInsertService basicInsertService;

    @Autowired
    private NaiveInsertService naiveInsertService;

    @Autowired
    private LinearInsertService linearInsertService;

    @Autowired
    private PGreedyLinearInsertService pGreedyLinearInsertService;

    @Autowired
    private ProphetLinearInsertService predictedInsertService;

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

    @PostMapping("/naive_dp_based")
    public ResponseEntity<?> naiveDPInsert(@RequestBody BookOnlineRequest data) {
        data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(naiveInsertService.insert(data));
    }

    @PostMapping("/experiment_naive_dp_based")
    public ResponseEntity<?> experimentNaiveDPInsert(@RequestBody List<BookOnlineRequest> listRequest) {
        for (BookOnlineRequest data : listRequest) data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(naiveInsertService.experiment(listRequest));
    }

    @PostMapping("/linear_dp")
    public ResponseEntity<?> linearDPInsert(@RequestBody BookOnlineRequest data) {
        data.initializePickUpTimeLate();
        return ResponseEntity.ok().body(linearInsertService.insert(data));
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
        return ResponseEntity.ok().body(predictedInsertService.insertOnlineProphet(data));
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
