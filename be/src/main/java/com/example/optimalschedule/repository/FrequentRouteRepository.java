package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.FrequentRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrequentRouteRepository extends JpaRepository<FrequentRoute, Integer> {


}
