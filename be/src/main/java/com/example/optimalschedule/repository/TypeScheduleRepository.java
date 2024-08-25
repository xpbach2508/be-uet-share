package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.TypeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeScheduleRepository extends JpaRepository<TypeSchedule, Integer> {
}
