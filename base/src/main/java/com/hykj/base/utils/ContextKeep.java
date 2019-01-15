package com.hykj.base.utils;

import android.content.Context;

public class ContextKeep {
    private Context context;

    private static ContextKeep mInstance;

    private ContextKeep(Context context) {
        this.context = context;
    }

    public static synchronized ContextKeep init(Context context) {
        if (mInstance == null)
            mInstance = new ContextKeep(context);
        return mInstance;
    }

    public static Context getContext() {
        return mInstance.context;
    }
}
