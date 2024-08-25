package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table (name = "driver")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    private String fullName;

    private String phone;

    private String password;

    private String licensePlate;

    private int seat;

    private String nameCar;

    private int carId;

    public Driver(String email, String fullName, String phone, String password, String licensePlate, int seat,
                  String nameCar, int carId) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.licensePlate = licensePlate;
        this.seat = seat;
        this.nameCar = nameCar;
        this.carId = carId;
    }

    public Driver(int id, String email, String fullName, String phone, String password, String licensePlate, int seat,
                  String nameCar, int carId) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.licensePlate = licensePlate;
        this.seat = seat;
        this.nameCar = nameCar;
        this.carId = carId;
    }
}
