package com.hykj.base.listener;

import android.view.View;

/**
 * 短时间内不允许点击
 */
public abstract class SingleOnClickListener extends SingleClick implements View.OnClickListener {
    public SingleOnClickListener() {
    }

    public SingleOnClickListener(int intervalTime) {
        INTERVAL_TIME=intervalTime;
    }

    @Override
    public void onClick(View v) {
        if (canClick(v)) {
            onClickSub(v);
        }
    }

    public abstract void onClickSub(View v);
}
