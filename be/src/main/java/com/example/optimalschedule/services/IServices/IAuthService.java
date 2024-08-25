package com.example.optimalschedule.services.IServices;

import com.example.optimalschedule.common.exception.NotFoundException;
import com.example.optimalschedule.entity.Admin;
import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.OTP;
import com.example.optimalschedule.entity.Passenger;
import com.example.optimalschedule.model.request.ChangePassRequest;
import com.example.optimalschedule.model.request.DriverRequest;
import com.example.optimalschedule.model.request.LoginRequest;
import com.example.optimalschedule.model.response.LoginResponse;

public interface IAuthService {

    void signupPassenger(Passenger signup);

    void signupDriver(DriverRequest signup);

    LoginResponse login(String email, String password);

    Admin getInfoAccountAdmin();

    Driver getInfoAccountDriver();

    Passenger getInfoAccountPassenger();

    void changeInfoAdmin(Admin admin);

    void changeInfoDriver(Driver driver);

    void changeInfoPassenger(Passenger passenger);

    void createOTP(String email) throws NotFoundException;

    void verifyOTP(OTP otp) throws NotFoundException;

    LoginResponse changePassword(ChangePassRequest data) throws NotFoundException;
}
