package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTP, String> {

    OTP findByEmailAndOtp(String email, int otp);

}
