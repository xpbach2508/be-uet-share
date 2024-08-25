package com.example.optimalschedule.services;

import com.example.optimalschedule.common.secutity.service.UserDetailsImpl;
import com.example.optimalschedule.entity.Route;
import com.example.optimalschedule.repository.RouteRepository;
import com.example.optimalschedule.services.IServices.IFrequentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrequentService implements IFrequentService {

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public List<Route> getAllFrequentByAccountId() {
        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return routeRepository.findByAccountId(userDetails.getId());
    }

}
