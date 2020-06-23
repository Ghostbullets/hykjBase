package com.hykj.hykjbase;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.hykj.base.utils.ContextKeep;
import com.hykj.base.utils.storage.FileUtil;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ContextKeep.init(this);
        FileUtil.init();
    }
}
