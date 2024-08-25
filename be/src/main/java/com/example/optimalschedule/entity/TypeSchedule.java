package com.example.optimalschedule.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "type_schedule")
public class TypeSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*
        1: frequent
        2: online
     */
    private String type;
}
