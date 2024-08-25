package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.TypeLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeLocationRepository extends JpaRepository<TypeLocation, Integer> {
}
