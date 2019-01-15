package com.hykj.base.mgr;

public class UserInfo {
    private String phone;
    private String token;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public void clear() {
        this.phone = null;
        this.token = null;
    }
}
