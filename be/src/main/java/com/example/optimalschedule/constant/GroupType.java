package com.example.optimalschedule.constant;

public enum GroupType {
    GUIDANCE(0),
    ONLY_ONLINE(1);

    private final int value;

    GroupType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
