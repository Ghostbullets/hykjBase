package com.hykj.base.bean;

/**
 * 富文本加载WebView传递对象
 */
public class RichTextInfo {
    private String content;//要加载的富文本内容
    private String title;//标题
    private boolean isFullScreen;//是否宽度100%,高度自适应auto
    private boolean isRemoveInterval;//是否去除<p>跟<p>图片的间隔

    public RichTextInfo(String content, String title, boolean isFullScreen, boolean isRemoveInterval) {
        this.content = content;
        this.title = title;
        this.isFullScreen = isFullScreen;
        this.isRemoveInterval = isRemoveInterval;
    }


    public RichTextInfo(String content, String title) {
        this.content = content;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public boolean isRemoveInterval() {
        return isRemoveInterval;
    }

    public void setRemoveInterval(boolean removeInterval) {
        isRemoveInterval = removeInterval;
    }
}
