package com.example.optimalschedule.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse {

    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
