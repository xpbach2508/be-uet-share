package com.example.optimalschedule.common.secutity.service;

import com.example.optimalschedule.entity.Admin;
import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.Passenger;
import com.example.optimalschedule.repository.AdminRepository;
import com.example.optimalschedule.repository.DriverRepository;
import com.example.optimalschedule.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    PassengerRepository userAndAdminRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    AdminRepository adminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return UserDetailsImpl.build(admin.getId(), null, admin.getEmail(), 1, admin.getPassword());
        }

        Driver driver = driverRepository.findByEmail(email);
        if (driver != null) {
            return UserDetailsImpl.build(driver.getId(), driver.getFullName(), driver.getEmail(), 2, driver.getPassword());
        }

        Passenger passenger = userAndAdminRepository.findByEmail(email);
        if (passenger == null)  throw new UsernameNotFoundException("User Not Found with email: " + email);

        return UserDetailsImpl.build(passenger.getId(), passenger.getFullName(), passenger.getEmail(), 3, passenger.getPassword());
    }

}