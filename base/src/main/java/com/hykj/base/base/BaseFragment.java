package com.hykj.base.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public abstract class BaseFragment extends Fragment {
    // 宿主对象
    protected FragmentActivity mActivity;
    // 内容
    protected View mView;
    protected boolean isViewCreated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), null);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();
    }

    @Override
    public final void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        // 为了能隐藏
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }

        if (mView != null) {
            mView.setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

    //是否可以标题栏半透明
    public boolean checkTransStatus() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setTranslucentStatus(boolean on) {

    }

    /**
     * 设置标题栏图标颜色,android6.0以上才有用
     *
     * @param setDark 是否设置为黑色
     */
    public void setStatusIconColor(boolean setDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = mActivity.getWindow().getDecorView();
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
     * 是否需要登录
     *
     * @return
     */
    public boolean needLogin() {
        return false;
    }

    /**
     * 获取控件
     *
     * @param id
     * @return
     */
    public <T extends View> T findViewById(int id) {
        return (T) mView.findViewById(id);
    }

    /**
     * 打开页面
     *
     * @param cls
     */
    protected void startActivity(Class<?> cls) {
        startActivity(new Intent(mActivity, cls));
    }

    /**
     * 打开页带参数
     *
     * @param cls
     * @param bundle
     */
    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 打开页带返回
     *
     * @param cls
     * @param requestCode
     */
    private void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(getContext(), cls);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 打开页带返回、带参数
     *
     * @param cls
     * @param requestCode
     * @param bundle
     */
    private void startActivityForResult(Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(getContext(), cls);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 二次点击
     */
    public void onSecondClick() {

    }

    /***
     * 关软件盘  调用该方法传入一个view
     *
     * @param view
     */
    protected void HideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软件盘
     */
    protected void ShowKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

}
