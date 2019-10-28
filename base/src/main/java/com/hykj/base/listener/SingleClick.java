package com.hykj.base.listener;

import android.view.View;

public class SingleClick {
    protected static long INTERVAL_TIME = 500;
    protected long currentTime;

    public boolean canClick(View view) {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - currentTime > INTERVAL_TIME) {
            currentTime = timeMillis;
            return true;
        }
        return false;
    }
}
