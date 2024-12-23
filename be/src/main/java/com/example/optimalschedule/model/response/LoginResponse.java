package com.example.optimalschedule.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {

    private String jwt;
    /*
        1: admin
        2: driver
        3: passenger
     */
    private int role;
    private String username;
    private int id;

    public LoginResponse(String jwt, int id, String username, int role) {
        this.jwt = jwt;
        this.role = role;
        this.username = username;
        this.id = id;
    }
}
