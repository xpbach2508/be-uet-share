package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Route;
import com.example.optimalschedule.model.response.RideResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    List<Route> findByGroupId(int groupId);

    List<Route> findByAccountId(int accountId);

    Route findByAccountIdAndGroupId(int accountId, int groupId);

    @Query(nativeQuery = true, name = "getAllFrequentSharedByAccountId")
    List<RideResponse> getAllFrequentSharedByAccountId(int accountId);

}
