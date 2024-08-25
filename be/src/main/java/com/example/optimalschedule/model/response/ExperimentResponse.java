package com.example.optimalschedule.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExperimentResponse {

    private int numberOfMatch;
    private int numberOfNewCreate;

    public ExperimentResponse(int numberOfMatch, int numberOfNewCreate) {
        this.numberOfMatch = numberOfMatch;
        this.numberOfNewCreate = numberOfNewCreate;
    }

}
