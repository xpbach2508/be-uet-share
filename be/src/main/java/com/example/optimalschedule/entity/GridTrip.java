package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table (name = "grid_trip")
public class GridTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grid_trip_id")
    private int id;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "trip_id")
    private int tripId;

    @Temporal(TemporalType.DATE)
    private Date date;

    private int weekday;

    public GridTrip(int id, int accountId, int tripId, Date date, int weekday) {
        this.id = id;
        this.accountId = accountId;
        this.tripId = tripId;
        this.date = date;
        this.weekday = weekday;
    }
}
