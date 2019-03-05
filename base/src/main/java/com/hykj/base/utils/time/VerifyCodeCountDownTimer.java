package com.hykj.base.utils.time;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 获取验证码工具类
 */
public class VerifyCodeCountDownTimer extends CountDownTimer {
    private TextView textView;

    public VerifyCodeCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public void start(TextView textView) {
        this.textView = textView;
        textView.setEnabled(false);
        start();
    }


    @Override
    public void onTick(long l) {
        textView.setText(String.format("%s秒后重新获取", l / 1000));
    }

    @Override
    public void onFinish() {
        textView.setEnabled(true);
        textView.setText("获取验证码");
    }
}
