package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.TypeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeStatusRepository extends JpaRepository<TypeStatus, Integer> {
}
