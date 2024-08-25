package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "taxi")
public class Taxi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String licensePlate;

    private int seat;

    private String nameCar;

    public Taxi(String licensePlate, int seat, String nameCar) {
        this.licensePlate = licensePlate;
        this.seat = seat;
        this.nameCar = nameCar;
    }

    public Taxi(int id, String licensePlate, int seat, String nameCar) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.seat = seat;
        this.nameCar = nameCar;
    }
}
