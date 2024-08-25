package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findByEmail(String email);

    Boolean existsByEmail(String email);

}
