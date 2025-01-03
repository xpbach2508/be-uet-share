package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.Taxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    @Query(value = "select * " +
            "from driver " +
            "where id not in (select driver_id from group_frequent where type != 0) " +
            "limit 1", nativeQuery = true)
    Driver findOneNewTaxi();

    @Query(value = "select * " +
            "from driver " +
            "where id not in (select driver_id from group_frequent where type = 0) " +
            "limit 1", nativeQuery = true)
    Driver findOneNewHypoTaxi();

    Driver findByEmail(String email);

    Boolean existsByEmail(String email);

    List<Driver> findAllByOrderByIdDesc();

    List<Driver> findAllByOrderByIdAsc();

    Driver findFirstById(int id);
}