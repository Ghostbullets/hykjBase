package com.hykj.base.utils.time;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

public abstract class CountDownTimer {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;//总时间

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;//间隔多少秒动一次，默认1000

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;

    private long currentTime;//当前时间

    private boolean isResume;//是否继续而不是重新开始


    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized final CountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }

        currentTime = mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    /**
     * 继续
     * @param currentTime 设置当前时间的同时继续
     * @return
     */
    public synchronized final CountDownTimer resume(long currentTime) {
        mCancelled = false;
        this.currentTime = currentTime;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    //继续
    public synchronized final CountDownTimer resume() {
        mCancelled = false;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }


    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    private static final int MSG = 1;


    // handles counting down
    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            synchronized (CountDownTimer.this) {
                if (mCancelled) {
                    return;
                }
                if (currentTime <= 0) {
                    onFinish();
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(currentTime);

                    // take into account user's onTick taking time to execute
                    long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
                    long delay;

                    if (currentTime < mCountdownInterval) {
                        // just delay until done
                        delay = currentTime - lastTickDuration;

                        // special case: user's onTick took more than interval to
                        // complete, trigger onFinish without delay
                        if (delay < 0) delay = 0;
                    } else {
                        delay = mCountdownInterval - lastTickDuration;

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval;
                    }
                    currentTime = currentTime - mCountdownInterval;
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };

    public long getCurrentTime() {
        return currentTime;
    }

    public void setResume(boolean resume) {
        isResume = resume;
    }

    public boolean isResume() {
        return isResume;
    }
}
