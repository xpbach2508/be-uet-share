package com.example.optimalschedule.services.IServices;

import com.example.optimalschedule.entity.Route;

import java.util.List;

public interface IFrequentService {

    List<Route> getAllFrequentByAccountId();
}
