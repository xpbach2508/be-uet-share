package com.example.optimalschedule.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShareFrequentRequest {

    private Boolean typeShare;
    private int routeId;

    public ShareFrequentRequest(Boolean typeShare, int routeId) {
        this.typeShare = typeShare;
        this.routeId = routeId;
    }
}
