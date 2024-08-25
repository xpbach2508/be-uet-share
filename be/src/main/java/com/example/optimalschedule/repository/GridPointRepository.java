package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.GridPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GridPointRepository extends JpaRepository<GridPoint, Integer> {

}
