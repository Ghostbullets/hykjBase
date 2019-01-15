package com.hykj.base.listener;

public class SingleClick {
    private static final long INTERVAL_TIME = 500;
    private long currentTime;

    public boolean canClick() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - currentTime > INTERVAL_TIME) {
            currentTime = timeMillis;
            return true;
        }
        return false;
    }
}
