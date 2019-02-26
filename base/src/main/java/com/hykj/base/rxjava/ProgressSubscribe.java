package com.hykj.base.rxjava;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.hykj.base.dialog.ProgressBarDialog;
import com.hykj.base.utils.NetWorkUtils;
import com.hykj.base.utils.text.Tip;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by cjf
 * on:2019/2/26 11:01
 * 处理RxJava+retrofit回调
 */
public abstract class ProgressSubscribe<T> implements ProgressBarDialog.ProgressCancelListener, Observer<T> {
    protected static final String TAG = ProgressSubscribe.class.getName();
    protected FragmentActivity mActivity;
    protected Disposable disposable;
    protected ProgressBarDialog mHub;

    public ProgressSubscribe(FragmentActivity activity) {
        this.mActivity = activity;
        mHub = new ProgressBarDialog().init(activity);
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onComplete() {
        mHub.dismiss();
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage());
        if (!NetWorkUtils.isNetWorkConnected(mActivity)) {
            Tip.showShort("网络不可用");
        } else {
            onFailure(e);
        }
        mHub.dismiss();
    }

    @Override
    public void onNext(T t) {
        onResponse(t);
    }

    @Override
    public void onCancelListener() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    protected abstract void onResponse(T t);

    protected void onFailure(Throwable e) {

    }
}
