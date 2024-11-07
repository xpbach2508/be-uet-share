package com.example.optimalschedule.model.request;

import com.example.optimalschedule.gripmap.MapUtility;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PredictedRequest {

    private Double pickUpTime;
    // Server tự sinh từ pickUpTime mà không cần trong request của client
    private Double pickUpTimeLate;

    // thời gian request xuat hien, mac dinh la expectedTime - 0.5
    private Double showTime;

    private Integer capacity;

    private Double latOrigin;
    private Double lngOrigin;

    private Double latDestination;
    private Double lngDestination;

    private String addressStart;
    private String addressEnd;

    private Double length;

    public PredictedRequest(Double pickUpTime, Integer capacity, Double latOrigin, Double lngOrigin,
                            Double latDestination, Double lngDestination, String addressStart, String addressEnd,
                            Double length) {
        this.pickUpTime = pickUpTime;
        this.capacity = capacity;
        this.latOrigin = latOrigin;
        this.lngOrigin = lngOrigin;
        this.latDestination = latDestination;
        this.lngDestination = lngDestination;
        this.addressStart = addressStart;
        this.addressEnd = addressEnd;
        this.length = length;
        this.showTime = MapUtility.showTime(pickUpTime);
    }

    public void initializePickUpTimeLate() {
        this.pickUpTimeLate = MapUtility.timeLate(pickUpTime);
    }

    public void initShowTime() {
        this.showTime = MapUtility.showTime(pickUpTime);
    }

    public String checkInput() {
        if (pickUpTime == null || capacity == null || latOrigin == null || lngOrigin == null || latDestination == null ||
                lngDestination == null || addressStart == null || addressEnd == null || length == null) return "Input need full!";
        if (latOrigin < MapUtility.START_LATITUDE || latOrigin > MapUtility.END_LATITUDE ||
                lngOrigin < MapUtility.START_LONGITUDE || lngOrigin > MapUtility.END_LONGITUDE) return "Latitude, Longitude out of scope!";
        return null;
    }

}
