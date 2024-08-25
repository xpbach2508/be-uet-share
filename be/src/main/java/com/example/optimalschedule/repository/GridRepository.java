package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Grid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GridRepository extends JpaRepository<Grid, Integer> {

}
