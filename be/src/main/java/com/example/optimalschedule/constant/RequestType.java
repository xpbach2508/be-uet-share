package com.example.optimalschedule.constant;

public enum RequestType {
    PREDICTED(3),
    ONLINE(2);

    private final int value;

    RequestType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
