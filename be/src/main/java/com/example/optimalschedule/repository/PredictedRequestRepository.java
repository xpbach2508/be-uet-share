package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.PredictedRequest;
import com.example.optimalschedule.model.response.RideResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictedRequestRepository extends JpaRepository<PredictedRequest, Integer> {

    List<PredictedRequest> findByStatusIdAndGroupId(int statusId, int groupId);

    @Query(nativeQuery = true, name = "getAllPredictedByAccountId")
    List<RideResponse> getAllPredictedByAccountId(int accountId);
}
