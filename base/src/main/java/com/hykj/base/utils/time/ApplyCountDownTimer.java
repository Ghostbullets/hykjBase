package com.hykj.base.utils.time;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Created by cjf on 2017/11/19.
 * 时间计时器
 */

public class ApplyCountDownTimer extends CountDownTimer {
    private TextView mTvTime;
    private @TimeType
    int timeType;

    public ApplyCountDownTimer(long millisInFuture, long countDownInterval, @NonNull TextView mTvTime) {
        super(millisInFuture, countDownInterval);
        this.mTvTime = mTvTime;
        if (millisInFuture > 24 * 60 * 60 * 1000) {
            this.timeType = TimeType.DAY;
        } else if (millisInFuture > 60 * 60 * 1000) {
            this.timeType = TimeType.HOUR;
        } else {
            this.timeType = TimeType.MINUTE;
        }
    }

    public ApplyCountDownTimer(long millisInFuture, long countDownInterval, TextView mTvTime, @TimeType int timeType) {
        super(millisInFuture, countDownInterval);
        this.mTvTime = mTvTime;
        this.timeType = timeType;
    }

    @Override
    public void onTick(long l) {
        mTvTime.setText(getTime(l));
    }

    @Override
    public void onFinish() {
        String time = null;
        switch (timeType) {
            case TimeType.DAY:
                time = "0天 00:00:00";
                break;
            case TimeType.HOUR:
                time = "00:00:00";
                break;
            case TimeType.MINUTE:
                time = "00:00";
                break;
        }
        mTvTime.setText(time);
    }

    /**
     * 根据时间、类型返回字符串显示
     *
     * @param millisecond 毫秒为单位
     * @return
     */
    public String getTime(long millisecond) {
        long secondTime = millisecond / 1000;
        String time = null;
        switch (this.timeType) {
            case TimeType.DAY: {
                int day = (int) (secondTime / (24 * 60 * 60));
                int hour = (int) (secondTime % (24 * 60 * 60) / (60 * 60));//4000秒/3600=1.。。400
                int minute = (int) (secondTime % (24 * 60 * 60) % (60 * 60) / 60);
                int second = (int) (secondTime % (24 * 60 * 60) % (60 * 60) % 60);
                time = String.format(Locale.CHINA, "%d天 %02d:%02d:%02d", day, hour, minute, second);
            }
            break;
            case TimeType.HOUR: {
                int hour = (int) (secondTime / (60 * 60));//4000秒/3600=1.。。400
                int minute = (int) (secondTime % (60 * 60) / 60);
                int second = (int) (secondTime % (60 * 60) % 60);
                time = String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
            }
            break;
            case TimeType.MINUTE: {
                int minute = (int) (secondTime / 60);
                int second = (int) (secondTime % 60);
                time = String.format(Locale.CHINA, "%02d:%02d", minute, second);
            }
            break;
        }
        return time;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TimeType.DAY, TimeType.HOUR, TimeType.MINUTE})
    public @interface TimeType {//显示数据类型
        int DAY = 0;
        int HOUR = 1;
        int MINUTE = 2;
    }
}
