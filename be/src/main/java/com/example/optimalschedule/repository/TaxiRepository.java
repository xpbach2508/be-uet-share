package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Taxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaxiRepository extends JpaRepository<Taxi, Integer> {

    @Query(value = "select * " +
            "from taxi " +
            "where id not in (select car_id from driver) " +
            "order by id desc", nativeQuery = true)
    List<Taxi> getAllTaxiEmpty();

    Taxi findFirstById(int taxiId);

    List<Taxi> findAll();
}
