package com.example.optimalschedule.controller;

import com.example.optimalschedule.services.FrequentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@Controller
@RequestMapping("/frequent")
@PreAuthorize("hasRole('PASSENGER')")
public class FrequentController {

    @Autowired
    private FrequentService frequentService;

    @GetMapping("")
    public ResponseEntity<?> getAllFrequentByAccountId() {
        return ResponseEntity.ok(frequentService.getAllFrequentByAccountId());
    }

}
