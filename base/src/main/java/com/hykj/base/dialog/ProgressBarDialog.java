package com.hykj.base.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.utils.ContextKeep;
import com.hykj.base.utils.ReflectionUtils;


public class ProgressBarDialog extends BaseDialogFragment {
    private static final String TAG = ProgressBarDialog.class.getSimpleName();
    FragmentActivity mActivity;
    private String message;

    public ProgressBarDialog init(FragmentActivity activity) {
        this.mActivity = activity;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        int size = size2px(ContextKeep.getContext(), TypedValue.COMPLEX_UNIT_DIP, 100);
        window.setLayout(size, size);
        window.setGravity(Gravity.CENTER);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    public ProgressBarDialog setData(String message) {
        this.message = message;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress_bar_circle, container, false);
        if (message != null)
            ((TextView) view.findViewById(R.id.tv_msg)).setText(message);
        view.findViewById(R.id.tv_msg).setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
        return view;
    }

    /**
     * 显示弹窗
     *
     * @param message
     */
    public void showProgress(String message) {
        this.message = message;
        FragmentManager manager = mActivity.getSupportFragmentManager();
        if (!isAdded()) {
            if (manager.findFragmentByTag(getClass().getSimpleName()) == null) {
                ReflectionUtils.setFieldValue(this, "mDismissed", false);
                ReflectionUtils.setFieldValue(this, "mShownByMe", true);
                ReflectionUtils.setFieldValue(this, "mViewDestroyed", false);
                FragmentTransaction ft = manager.beginTransaction();
                ft.add(this, TAG);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void dismiss() {
        if (mActivity != null && !mActivity.isFinishing())
            super.dismiss();
    }

    public static int size2px(Context context, int unit, int size) {
        return (int) TypedValue.applyDimension(unit, size, context.getResources().getDisplayMetrics());
    }
}
