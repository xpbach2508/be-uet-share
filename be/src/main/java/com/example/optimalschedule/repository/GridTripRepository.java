package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.GridTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GridTripRepository extends JpaRepository<GridTrip, Integer> {

}
