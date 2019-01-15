package com.hykj.base.mgr.impl;

import android.text.TextUtils;

import com.hykj.base.mgr.UserInfo;
import com.hykj.base.mgr.UserMgr;
import com.hykj.base.utils.storage.SPUtils;

/**
 * 用户管理器
 */
public class UserMgrImpl implements UserMgr {
    private UserInfo mUserInfo;

    public UserMgrImpl() {
        mUserInfo = new UserInfo();
        mUserInfo.setToken((String) SPUtils.get(SPUtils.TOKEN, ""));
        mUserInfo.setPhone((String) SPUtils.get(SPUtils.PHONE, ""));
    }

    @Override
    public boolean isLogin() {
        return !TextUtils.isEmpty(mUserInfo.getToken());
    }

    @Override
    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    @Override
    public String getToken() {
        return mUserInfo.getToken();
    }

    @Override
    public String getPhone() {
        return mUserInfo.getPhone();
    }

    @Override
    public void setPhone(String phone) {
        SPUtils.put(SPUtils.PHONE, phone);
        mUserInfo.setPhone(phone);
    }

    @Override
    public void updateUserInfo(String phone, String token) {
        SPUtils.put(SPUtils.TOKEN, token);
        SPUtils.put(SPUtils.PHONE, phone);
        mUserInfo.setPhone(phone);
        mUserInfo.setToken(token);
    }

    @Override
    public void clear() {
        SPUtils.remove(SPUtils.TOKEN);
        SPUtils.remove(SPUtils.PHONE);
        mUserInfo.clear();
    }
}
