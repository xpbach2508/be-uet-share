package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table (name = "trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trip_id;

    private int accountOwner;

    @Temporal(TemporalType.DATE)
    private Date date;

    private int weekday;

    public Trip(int trip_id, int accountOwner, Date date, int weekday) {
        this.trip_id = trip_id;
        this.accountOwner = accountOwner;
        this.date = date;
        this.weekday = weekday;
    }
}
