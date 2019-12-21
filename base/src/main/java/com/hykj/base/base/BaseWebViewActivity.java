package com.hykj.base.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.hykj.base.R;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.WebViewUtils;
import com.hykj.base.utils.text.Tip;
import com.hykj.base.view.TitleView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * WebView基类,带post请求讲解
 * https://blog.csdn.net/ouyangjiluo/article/details/50558149  讲解WebView的post的application/json方式
 * http://cache.baiducontent.com/c?m=9d78d513d9901df918b0cf281a16a6234e0497624c938b423ac3923884642c564616a1e666640705a3823c3916af381dacb06d2e621420c6dc88d65dddca85285e9f26442057c01605d368b8cb3732c050d207a8e90ee6caa661d5fdc6949f0a5c90154338c1e78a291d098f2ab5033194fec21b491e4afdfa3012ae042834dd3e17e64da9b6636906d2f6dd5e&p=882a9644d78502fc57efd3221e4c8f&newp=80769a478d9c0eff57ee957f135fcf231610db2151d7db176b82c825d7331b001c3bbfb423261604d7c1766703ab4b56edf133733c0421a3dda5c91d9fb4c57479d3787b&user=baidu&fm=sc&query=android++WebView%2EpostUrl%28String+url%2C+byte%5B%5D+postData%29&qid=f9e10ee000004f9b&p1=2
 * https://blog.csdn.net/carson_ho/article/details/64904691/
 */
public class BaseWebViewActivity extends TitleActivity {
    protected static final String PARAMETER = "Parameter";

    protected static final String JS = "js";
    protected static final String WEB_VIEW = "webView";

    protected ProgressBar progressBar;
    protected WebView mWebView;
    protected WebViewUtils webViewUtils;
    protected boolean isFinished;
    protected Builder parameter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_web_view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init(TitleView title) {
        String json = getIntent().getStringExtra(PARAMETER);
        if (TextUtils.isEmpty(json)) {
            finish();
            return;
        }
        parameter = new Gson().fromJson(json, Builder.class);

        title.setTitle(parameter.title);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.getLayoutParams().height = DisplayUtils.size2px(TypedValue.COMPLEX_UNIT_DIP, parameter.progressHeight);
        progressBar.setProgressDrawable(getResources().getDrawable(parameter.progressDrawable));
        progressBar.setVisibility(parameter.isShowProgress ? View.VISIBLE : View.GONE);

        mWebView = findViewById(R.id.wv_content);
        webViewUtils = new WebViewUtils(mWebView);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);

        //通过addJavascriptInterface()将Java对象映射到JS对象，参数1：Javascript对象名，参数2：Java对象名
        //mWebView.addJavascriptInterface(new AndroidToJs(), "test");//AndroidToJS类对象映射到js的test对象
        // 载入JS代码,格式规定为:file:///android_asset/文件名.html
        mWebView.loadUrl(parameter.url);
    }

    /**
     * WebView post请求，当后台需要 Content-type=application/json时使用
     * 因为WebView.post(String url,byte[] data)中后者使用的Content-type=application/x-www-form-urlencoded
     *
     * @param url
     * @return
     * @throws Exception
     */
    private WebResourceResponse postUrl(String url) throws Exception {
        WebResourceResponse resp = null;
        if (url.contains(parameter.url)) {
            HttpURLConnection coon = (HttpURLConnection) new URL(url).openConnection();
            coon.setRequestMethod("POST");
            coon.setRequestProperty("Content-type", "application/json");
           /* String token = "fgkdgkdkdg";
            String request = "{\"token\":\"" + token + "\"}";*/
            coon.getOutputStream().write(parameter.postJson.getBytes());
            resp = new WebResourceResponse("text/plain", coon.getHeaderField("encoding"), coon.getInputStream());
        }
        return resp;
    }


    /**
     * webView页面加载过程监听
     */
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isFinished = false;//WebView开始加载网页时调用，设置false
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (parameter.isPost) {
                try {
                    return postUrl(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (parameter.isPost) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        return postUrl(request.getUrl().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return manageUrlLoading(Uri.parse(url));
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // 步骤2：根据协议的参数，判断是否是所需要的url
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //假定传入进来的 url = "js://webView?name=张三&age=11"（同时也是约定好的需要拦截的）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return manageUrlLoading(request.getUrl());
            }
            return super.shouldOverrideUrlLoading(view, request);
        }
    };

    private boolean manageUrlLoading(Uri url) {
        if (JS.equals(url.getScheme())) {
            // 如果 authority =预先约定协议里的webView，即代表都符合约定的协议,拦截url
            if (WEB_VIEW.equals(url.getAuthority())) {
                Iterator<String> iterator = url.getQueryParameterNames().iterator();
                StringBuilder builder = new StringBuilder();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = url.getQueryParameter(key);
                    builder.append(String.format("%s=%s", key, value));
                }
                String result = "js调用了Android的方法成功啦";
                mWebView.loadUrl("javascript:returnResult(" + result + ")");//JS获取Android方法的返回值
            }
            return true;
        }
        return false;
    }

    // 由于设置了弹窗检验调用结果,所以需要支持js对话框
    // webView只是载体，内容的渲染需要使用webViewChromeClient类去实现
    // 通过设置WebChromeClient对象处理JavaScript的对话框
    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {//没有返回值
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Alert").setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {//返回true、false
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {//可返回任意字符
            Uri uri = Uri.parse(url);
            if (JS.equals(uri.getScheme())) {
                if (WEB_VIEW.equals(uri.getAuthority())) {
                    StringBuilder builder = new StringBuilder();
                    Iterator<String> iterator = uri.getQueryParameterNames().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = uri.getQueryParameter(key);
                        builder.append(String.format("%s=%s", key, value));
                    }
                    result.confirm("js调用了Android的方法成功啦");

                }
                return true;
            }

            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100)
                isFinished = true;
            if (parameter.isShowProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (progressBar.getVisibility() == View.GONE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
            }
        }
    };

    private ValueCallback<String> valueCallback = new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String value) {
            //此处为 js 返回的结果
            Tip.showShort("返回结果为" + value);
        }
    };

    @Override
    protected View createTitleRight() {
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageResource(R.drawable.ic_refresh);
        imageView.setPadding(0, 0, DisplayUtils.size2px(TypedValue.COMPLEX_UNIT_DIP, 10), 0);
        imageView.setOnClickListener(new SingleOnClickListener() {
            @Override
            public void onClickSub(View v) {
                mWebView.loadUrl(parameter.url);//点击右上角刷新页面
                //Android通过WebView调用 JS 代码
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//该方法4.4版本可用
                    mWebView.evaluateJavascript("javascript:callJS()", valueCallback);
                } else {
                    mWebView.loadUrl("javascript:callJS()");
                }*/
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
        webViewUtils.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webViewUtils.onDestroy();
        super.onDestroy();
    }

    public static void start(Context context, String url, String title, boolean isPost, String json) {
        start(context, url, title, isPost, json, BaseWebViewActivity.class);
    }

    public static void start(Context context, String url, String title) {
        start(context, url, title, false, null);
    }

    public static void start(Context context, String url, String title, Class<? extends BaseWebViewActivity> cls) {
        start(context, url, title, false, null, cls);
    }

    /**
     * 开启画面
     *
     * @param context 上下文
     * @param url     请求的地址
     * @param title   标题
     * @param isPost  是否是post请求,这里由于Content-type格式为application/json，所以字符串需要以下格式
     * @param json    post需要携带的json字符串 格式为"{"title":"啦啦啦"&"phone":"啦啦啦啦啦啦啦"}"
     * @param cls     继承BaseWebViewActivity类的Activity
     */
    public static void start(Context context, String url, String title, boolean isPost, String json, Class<? extends BaseWebViewActivity> cls) {
        new Builder(url, title).isPost(isPost).postJson(json).build(context, cls);
    }

    private static void start(Context context, Class<? extends BaseWebViewActivity> cls, Builder parameter) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(PARAMETER, new Gson().toJson(parameter));
        context.startActivity(intent);
    }

    public static class Builder {
        private String url;//请求的地址
        private String title;//标题
        private boolean isPost;//是否是post请求,这里由于Content-type格式为application/json，所以字符串需要以下格式
        private String postJson;//post需要携带的json字符串 格式为"{"title":"啦啦啦","phone":"啦啦啦啦啦啦啦"}"
        private boolean isShowProgress;//是否显示进度条
        private @DrawableRes
        int progressDrawable = R.drawable.progress_drawable_base_web_view;//ProgressBar的progressDrawable属性
        private int progressHeight = 2;//进度条高度,单位dp
        private Map<String, Object> extraParams;//附加字段

        public Builder(String url, String title) {
            this.url = url;
            this.title = title;
            extraParams = new LinkedHashMap<>();
        }

        public Builder isPost(boolean isPost) {
            this.isPost = isPost;
            return this;
        }

        public Builder postJson(String postJson) {
            this.postJson = postJson;
            return this;
        }

        public Builder isShowProgress(boolean isShowProgress) {
            this.isShowProgress = isShowProgress;
            return this;
        }

        public Builder progressDrawable(@DrawableRes int progressDrawable) {
            this.progressDrawable = progressDrawable;
            return this;
        }

        public Builder progressHeight(int progressHeight) {
            if (progressHeight >= 0)
                this.progressHeight = progressHeight;
            return this;
        }

        public Builder addParam(String key, Object value) {
            extraParams.put(key, value);
            return this;
        }

        public Builder addParam(String... params) {
            if (params != null && params.length % 2 == 0) {
                for (int i = 0; i < params.length; i += 2) {
                    extraParams.put(params[i], params[i + 1]);
                }
            }
            return this;
        }

        public Builder addParams(Map<String, Object> params) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                extraParams.put(param.getKey(), param.getValue());
            }
            return this;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public boolean isPost() {
            return isPost;
        }

        public String getPostJson() {
            return postJson;
        }

        public boolean isShowProgress() {
            return isShowProgress;
        }

        public int getProgressDrawable() {
            return progressDrawable;
        }

        public int getProgressHeight() {
            return progressHeight;
        }

        public Map<String, Object> getExtraParams() {
            return extraParams;
        }

        public Object getParam(String key) {
            for (Map.Entry<String, Object> param : extraParams.entrySet()) {
                if (param.getKey().equals(key)) {
                    return param.getValue();
                }
            }
            return null;
        }

        public void build(Context context) {
            build(context, BaseWebViewActivity.class);
        }

        public void build(Context context, Class<? extends BaseWebViewActivity> cls) {
            BaseWebViewActivity.start(context, cls, this);
        }
    }

    //WebSettings   下面三个最常用，基本都需要设置
  /*  setCacheMode 设置缓存的模式 eg: settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    setJavaSciptEnabled 设置是否支持Javascript eg: settings.setJavaScriptEnabled(true);
    setDefaultTextEncodingName 设置在解码时使用的默认编码 eg: settings.setDefaultTextEncodingName(“utf-8”);
    setAllowFileAccess 启用或禁止WebView访问文件数据
    setBlockNetworkImage 是否显示网络图像
    setBuiltInZoomControls 设置是否支持缩放
    setDefaultFontSize 设置默认的字体大小
    setFixedFontFamily 设置固定使用的字体
    setLayoutAlgorithm 设置布局方式
    setLightTouchEnabled 设置用鼠标激活被选项
    setSupportZoom 设置是否支持变焦*/

    //WebViewClient
   /* onPageStarted 网页开始加载
    onReceivedError 报告错误信息
    onLoadResource 加载指定地址提供的资源
    shouldOverrideUrlLoading 控制新的连接在当前WebView中打开
    onPageFinished 网页加载完毕，此方法并没有方法名表现的那么美好，调用时机很不确定。如需监听网页加载完成可以使用onProgressChanged，当int progress返回100时表示网页加载完毕。
    doUpdate VisitedHistory 更新历史记录
    onFormResubmission 应用程序重新请求网页数据
    onScaleChanged WebView发生改变*/

    //WebChromeClient
    /*onProgressChanged 加载进度条改变
    onJsPrompt 用在解决4.2以下addJavascriptInterface漏洞问题
    onCloseWindow 关闭WebView
    onCreateWindow 创建WebView
    onJsAlert 处理Javascript中的Alert对话框
    onJsConfirm处理Javascript中的Confirm对话框
            onJsPrompt处理Javascript中的Prompt对话框
    onReceivedlcon 网页图标更改
    onReceivedTitle 网页Title更改
    onRequestFocus WebView显示焦点
    onConsoleMessage 在Logcat中输出javascript的日志信息*/
}
