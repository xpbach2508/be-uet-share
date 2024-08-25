package com.example.optimalschedule.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExpectedTimeRequest {

    private double expectedTimeOrigin;
    private double expectedTimeDes;

    public ExpectedTimeRequest(double expectedTimeOrigin, double expectedTimeDes) {
        this.expectedTimeOrigin = expectedTimeOrigin;
        this.expectedTimeDes = expectedTimeDes;
    }
}
