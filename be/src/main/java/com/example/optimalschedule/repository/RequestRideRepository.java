package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.RequestRide;
import com.example.optimalschedule.model.response.RideResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRideRepository extends JpaRepository<RequestRide, Integer> {

    List<RequestRide> findByStatusIdAndGroupId(int statusId, int groupId);

    @Query(nativeQuery = true, name = "getAllOnlineByAccountId")
    List<RideResponse> getAllOnlineByAccountId(int accountId);

    List<RequestRide> findByStatusId(int statusId);
}
