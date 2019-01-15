package com.hykj.base.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class WeakHandler<T> extends Handler {
    private WeakReference<T> mRef;

    public WeakHandler(T mActivity) {
        this.mRef = new WeakReference<>(mActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t = this.mRef.get();
        if (t != null) {
            handlerMessageEx(msg, t);
        }
    }

    public abstract void handlerMessageEx(Message msg, T t);
}
