package com.hykj.base.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

}
