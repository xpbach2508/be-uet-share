package com.example.optimalschedule.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table (name = "frequent_route")
public class FrequentRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int accountId;

    public FrequentRoute(int id, int accountId) {
        this.id = id;
        this.accountId = accountId;
    }
}
