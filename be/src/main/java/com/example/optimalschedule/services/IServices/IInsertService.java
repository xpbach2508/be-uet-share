package com.example.optimalschedule.services.IServices;

import com.example.optimalschedule.common.exception.BadRequestException;
import com.example.optimalschedule.common.exception.NotImplementedException;
import com.example.optimalschedule.model.request.BookOnlineRequest;
import com.example.optimalschedule.model.response.ExperimentResponse;
import com.example.optimalschedule.model.response.RideResponse;

import java.util.List;

public interface IInsertService {

    RideResponse insert(BookOnlineRequest data) throws BadRequestException, NotImplementedException;

    int experiment(List<BookOnlineRequest> listRequest);
}
