package com.example.optimalschedule.model.request;

import lombok.Data;

@Data
public class DriverRequest {

    private String email;

    private String fullName;

    private String password;

    private String phone;

    private int carId;

    public DriverRequest(String email, String fullName, String password, String phone, int carId) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.phone = phone;
        this.carId = carId;
    }

}
