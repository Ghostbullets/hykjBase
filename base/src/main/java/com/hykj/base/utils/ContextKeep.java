package com.hykj.base.utils;

import android.content.Context;

public class ContextKeep {
    private Context context;

    private static ContextKeep mInstance;

    private ContextKeep() {

    }

    public static synchronized ContextKeep getInstance() {
        if (mInstance == null)
            mInstance = new ContextKeep();
        return mInstance;
    }

    public static Context getContext() {
        return getInstance().context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
