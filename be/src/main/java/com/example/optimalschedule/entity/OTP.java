package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "otp")
public class OTP {

    @Id
    private String email;
    private int otp;

    public OTP(String email, int otp) {
        this.email = email;
        this.otp = otp;
    }
}
