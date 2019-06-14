package com.hykj.base.listener;

public class SingleClick {
    protected static long INTERVAL_TIME = 500;
    protected long currentTime;

    public boolean canClick() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - currentTime > INTERVAL_TIME) {
            currentTime = timeMillis;
            return true;
        }
        return false;
    }
}
