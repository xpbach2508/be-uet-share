package com.example.optimalschedule.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePassRequest {

    private String email;
    private String newPassword;

    public ChangePassRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }
}
