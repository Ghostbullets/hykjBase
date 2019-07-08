package com.hykj.base.bean;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.hykj.base.R;

import java.io.File;

/**
 * 更新服务携带数据
 */
public class UpdateTransInfo {
    public String appName;//通知显示的app名称
    public String downloadUrl;//下载链接
    @DrawableRes
    public Integer icon;//通知显示图标
    public boolean isAutoInstall;//是否下载完自动跳转到安装页面
    public String saveName;//保存文件名
    public String savePath;//保存路径
    public File saveFile;//文件保存地址

    public UpdateTransInfo(String appName, @NonNull String downloadUrl, @DrawableRes Integer icon, boolean isAutoInstall, String saveName, String savePath) {
        this.appName = appName;
        this.downloadUrl = downloadUrl;
        this.icon = icon;
        this.isAutoInstall = isAutoInstall;
        this.saveName = saveName;
        this.savePath = savePath;
        this.saveFile = new File(savePath + saveName);
    }

    public @DrawableRes
    int getIcon() {
        return icon != null && icon != -1 && icon != 0 ? icon : R.drawable.ic_launcher;
    }
}
