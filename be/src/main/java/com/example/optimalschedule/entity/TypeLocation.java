package com.example.optimalschedule.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "type_location")
public class TypeLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*
        1: origin
        2: destination
     */
    private String type;
}
