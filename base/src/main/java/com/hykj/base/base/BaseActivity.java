package com.hykj.base.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hykj.base.dialog.ProgressBarDialog;

public abstract class BaseActivity extends FragmentActivity {
    protected boolean isCreated = false;
    protected BaseActivity mActivity;
    protected ProgressBarDialog mHub;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //让应用的主体内容占用系统状态栏的空间，最后再调用Window的setStatusBarColor()方法将状态栏设置成透明色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mActivity = this;
        if (needLogin()) {
            return;
        }
        onCreateSub(savedInstanceState);
        //加载前处理
        if (preLoad())
            return;
        mHub = new ProgressBarDialog().init(mActivity);
        isCreated = true;
        //初始化内容
        onCreateSub();
    }

    public void showProDialog() {
        showProDialog(null);
    }

    public void showProDialog(boolean cancelable) {
        showProDialog(cancelable, null);
    }

    public void showProDialog(String msg) {
        showProDialog(false, msg);
    }

    public void showProDialog(boolean cancelable, String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            if (!TextUtils.isEmpty(msg)) {
                progressDialog.setMessage(msg);
            }
            progressDialog.setCancelable(cancelable);
            progressDialog.show();
        }
    }

    public void dismissProDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    protected void onCreateSub(Bundle savedInstanceState) {

    }

    /**
     * 初始化内容
     */
    protected void onCreateSub() {
        // 载入内容
        loadContentView();

        // 初始化
        init();
    }

    /**
     * 载入内容
     */
    protected void loadContentView() {
        if (getLayoutId() != -1)
            setContentView(getLayoutId());
    }

    /**
     * 载入前准备
     *
     * @return 是否中断操作
     */
    protected boolean preLoad() {
        return false;
    }

    /**
     * 是否需要登录
     *
     * @return
     */
    protected boolean needLogin() {
        return false;
    }

    /**
     * 布局
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * @return 是否可以标题栏半透明
     */
    public boolean checkTransStatus() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams params = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            params.flags |= bits;
//        }
//        else
//            params.flags &= bits;
//        win.setAttributes(params);
    }

    /**
     * 设置标题栏图标颜色,android6.0以上才有用
     *
     * @param setDark 是否设置为黑色
     */
    public void setStatusIconColor(boolean setDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int vis = decorView.getSystemUiVisibility();
            if (setDark) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }

    /**
     * 打开页面
     *
     * @param cls
     */
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 打开页带参数
     *
     * @param cls
     * @param bundle
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(mActivity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 结果反馈
     *
     * @param cls
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, requestCode, null);
    }

    /**
     * 结果反馈
     *
     * @param cls
     * @param requestCode
     * @param bundle
     */
    public void startActivityForResult(Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(mActivity, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /***
     * 关软件盘  调用该方法传入一个view
     *
     * @param view
     */
    public void HideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软件盘
     */
    public void ShowKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
