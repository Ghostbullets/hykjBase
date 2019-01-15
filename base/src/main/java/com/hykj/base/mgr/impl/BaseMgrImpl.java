package com.hykj.base.mgr.impl;


import com.hykj.base.mgr.BaseMgr;
import com.hykj.base.mgr.UserMgr;

public class BaseMgrImpl implements BaseMgr {
    private UserMgr mUserMgr;
    private static BaseMgr mInstance;

    public static BaseMgr getInstance() {
        if (mInstance == null) {
            synchronized (BaseMgr.class) {
                if (mInstance == null) {
                    mInstance = new BaseMgrImpl();
                }
            }
        }
        return mInstance;
    }

    private BaseMgrImpl() {
        mUserMgr = new UserMgrImpl();
    }

    @Override
    public UserMgr getUserMgr() {
        return mUserMgr;
    }

    //获取token
    public static String getToken() {
        return getInstance().getUserMgr().getToken();
    }

    //获取手机号
    public static String getPhone() {
        return getInstance().getUserMgr().getPhone();
    }
}
