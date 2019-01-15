package com.hykj.base.listener;

import android.view.View;

/**
 * 短时间内不允许点击
 */
public abstract class SingleOnClickListener extends SingleClick implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        if (canClick()) {
            onClickSub(v);
        }
    }

    public abstract void onClickSub(View v);
}
