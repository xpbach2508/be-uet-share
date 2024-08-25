package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    Passenger findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsById(int id);

}
