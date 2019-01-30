package com.hykj.hykjbase;

import android.app.Application;

import com.hykj.base.utils.ContextKeep;
import com.hykj.base.utils.storage.FileUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextKeep.init(this);
        FileUtil.init();
    }
}
