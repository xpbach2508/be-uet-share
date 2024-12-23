package com.example.optimalschedule.controller;

import com.example.optimalschedule.entity.Admin;
import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.OTP;
import com.example.optimalschedule.entity.Passenger;
import com.example.optimalschedule.model.request.ChangePassRequest;
import com.example.optimalschedule.model.request.DriverRequest;
import com.example.optimalschedule.model.request.LoginRequest;
import com.example.optimalschedule.model.response.LoginResponse;
import com.example.optimalschedule.model.response.MessageResponse;
import com.example.optimalschedule.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup_driver")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signupDriver(@RequestBody DriverRequest driver) {
        authService.signupDriver(driver);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @PostMapping("/signup_passenger")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> signupPassenger(@RequestBody Passenger passenger) {
        authService.signupPassenger(passenger);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest authEntity) {
        LoginResponse response = authService.login(authEntity.getEmail(), authEntity.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info_admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getInfoAdmin() {
        Admin admin = authService.getInfoAccountAdmin();
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/info_driver")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    public ResponseEntity<?> getInfoDriver() {
        Driver driver = authService.getInfoAccountDriver();
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/info_passenger")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getInfoPassenger() {
        Passenger passenger = authService.getInfoAccountPassenger();
        return ResponseEntity.ok(passenger);
    }

    @PostMapping("/change_info_admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeInfoAdmin(@RequestBody Admin admin) {
        authService.changeInfoAdmin(admin);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @PostMapping("/change_info_driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> changeInfoDriver(@RequestBody Driver driver) {
        authService.changeInfoDriver(driver);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @PostMapping("/change_info_passenger")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> changeInfoPassenger(@RequestBody Passenger passenger) {
        authService.changeInfoPassenger(passenger);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @GetMapping("/create_otp")
    public ResponseEntity<?> createOTP(@RequestParam("email") String email) {
        authService.createOTP(email);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @GetMapping("/verify_otp")
    public ResponseEntity<?> verifyOTP(@RequestParam("otp") int otp, @RequestParam("email") String email) {
        authService.verifyOTP(new OTP(email, otp));
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassRequest data) {
        LoginResponse result = authService.changePassword(data);
        return ResponseEntity.ok(result);
    }

}
