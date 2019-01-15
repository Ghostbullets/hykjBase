package com.hykj.base.mgr;

public interface UserMgr {

    /**
     * 用户是否登录
     *
     * @return
     */
    boolean isLogin();

    /**
     * 获取用户信息
     *
     * @return
     */
    UserInfo getUserInfo();

    //获取token
    String getToken();

    //获取手机号
    String getPhone();

    /**
     * 更新用户信息
     */
    void updateUserInfo(String phone, String token);

    void setPhone(String phone);

    /**
     * 清空用户信息，退出登录
     */
    void clear();
}
