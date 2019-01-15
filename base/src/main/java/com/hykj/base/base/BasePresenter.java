package com.hykj.base.base;


/**
 * 作者 沈栋 on 2016/12/5 0005.
 * 邮箱：263808622@qq.com
 */

public abstract class BasePresenter<V> {

    protected V mvpView;

    protected BasePresenter() {

    }

    protected BasePresenter(V view) {
        this.mvpView = view;
    }
}
