package com.hykj.base.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hykj.base.R;
import com.hykj.base.bean.RichTextInfo;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.WebViewUtils;
import com.hykj.base.view.TitleView;

/**
 * 富文本基础页面
 */
public class RichTextWebViewActivity extends TitleActivity {
    private static final String INFO = "richTextInfo";
    private RichTextInfo info;
    private WebView mWebView;
    private WebViewUtils webViewUtils;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_web_view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init(TitleView title) {
        String json = getIntent().getStringExtra(INFO);
        if (TextUtils.isEmpty(json)) {
            finish();
            return;
        }
        info = new Gson().fromJson(json, RichTextInfo.class);
        title.setTitle(info.getTitle());

        mWebView = findViewById(R.id.wv_content);
        webViewUtils = new WebViewUtils(mWebView);
        loadDataWithUrl();
    }

    public void loadDataWithUrl() {
        // 载入JS代码,格式规定为:file:///android_asset/文件名.html
        String content = info.getContent();
        if (info.isFullScreen())
            content = content.replace("<img", "<img style=\"height:auto;width:100%;\"");
        if (info.isRemoveInterval())
            content = content.replace("<p>", "<p style=\"margin:0 auto\">");
        mWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
    }

    @Override
    protected View createTitleRight() {
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageResource(R.drawable.ic_refresh);
        imageView.setPadding(0, 0, DisplayUtils.size2px(TypedValue.COMPLEX_UNIT_DIP, 10), 0);
        imageView.setOnClickListener(new SingleOnClickListener() {
            @Override
            public void onClickSub(View v) {
                loadDataWithUrl();
            }
        });
        return imageView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        webViewUtils.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webViewUtils.onDestroy();
        super.onDestroy();
    }

    public static void start(Context context, RichTextInfo info) {
        start(context, info, RichTextWebViewActivity.class);
    }

    /**
     * 开启画面
     *
     * @param context 上下文
     * @param info    富文本信息
     * @param cls     继承RichTextWebViewActivity类的Activity
     */
    public static void start(Context context, RichTextInfo info, Class<? extends RichTextWebViewActivity> cls) {
        Intent intent = new Intent(context, cls);
        if (info != null)
            intent.putExtra(INFO, new Gson().toJson(info));
        context.startActivity(intent);
    }
}
