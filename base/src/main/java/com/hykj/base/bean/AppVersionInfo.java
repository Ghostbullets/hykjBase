package com.hykj.base.bean;

/**
 * app版本信息
 */
public class AppVersionInfo {
    public String versionTitle;//版本更新标题
    public String versionName;//版本名称
    public String versionDesc;//版本描述
    public String packageSize;//包大小
    public String updateContent;//更新内容
    public String updateTime;//更新时间
    public String url;//下载链接
    public boolean isForcedUpdate;//是否强制更新，即弹窗是否可点击外部、返回键取消

    public AppVersionInfo(String versionTitle, String versionName, String versionDesc, String packageSize, String updateContent, String updateTime, String url, boolean isForcedUpdate) {
        this.versionTitle = versionTitle;
        this.versionName = versionName;
        this.versionDesc = versionDesc;
        this.packageSize = packageSize;
        this.updateContent = updateContent;
        this.updateTime = updateTime;
        this.url = url;
        this.isForcedUpdate = isForcedUpdate;
    }
}
