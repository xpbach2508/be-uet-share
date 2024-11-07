package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.GroupFrequent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupFrequentRepository extends JpaRepository<GroupFrequent, Integer> {

    @Query(value = "select * " +
            "from group_frequent " +
            "where id not in (select group_id from request_ride)", nativeQuery = true)
    List<GroupFrequent> findAllOnlyFrequent();

    List<GroupFrequent> findAllByType(int type);
    GroupFrequent findByDriverId(int driverId);

}
