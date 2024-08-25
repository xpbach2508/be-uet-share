package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.FrequentPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrequentPointRepository extends JpaRepository<FrequentPoint, Integer> {

    List<FrequentPoint> findByFrequentRouteId(int frequentRouteId);
}
