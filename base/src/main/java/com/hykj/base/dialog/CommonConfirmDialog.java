package com.hykj.base.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.listener.OnSelectClickListener;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DisplayUtils;


/**
 * 通用确认、取消提示框
 * 可以传入一个布局id，应用于该DialogFragment，传null则使用默认布局，注意传入的布局，确认按钮id=tv_confirm 取消id=tv_cancel 标题id=tv_title 内容id=tv_content
 * Created by cjf on 2019/08/02
 */

public class CommonConfirmDialog extends BaseDialogFragment {

    private CharSequence title;// 标题
    private CharSequence content;// 内容

    private CharSequence positiveText;//确认
    private CharSequence negativeText;//取消
    private OnSelectClickListener listener;
    private @LayoutRes
    int layoutId = -1;
    private TextView tvCancel;
    private TextView tvConfirm;

    private boolean isConfirmDialog = false;
    private boolean cancelOnTouchOutSideOrBack = true;//是否可以通过点击返回键、窗口以外的区域dismiss弹窗

    public CommonConfirmDialog setData(CharSequence title, CharSequence content, CharSequence mPositiveButtonText, CharSequence mNegativeButtonText) {

        this.title = title;
        this.content = content;
        this.positiveText = mPositiveButtonText;
        this.negativeText = mNegativeButtonText;
        return this;
    }

    public void setCancelOnTouchOutSideOrBack(boolean cancelOnTouchOutSideOrBack) {
        this.cancelOnTouchOutSideOrBack = cancelOnTouchOutSideOrBack;
    }

    public CommonConfirmDialog setLayoutId(@LayoutRes int resource) {
        layoutId = resource;
        return this;
    }

    public CommonConfirmDialog setListener(OnSelectClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCancelable(cancelOnTouchOutSideOrBack);
        getDialog().setCanceledOnTouchOutside(cancelOnTouchOutSideOrBack);
        Window window = getDialog().getWindow();
        DisplayUtils displayUtils = new DisplayUtils(getContext());
        window.setLayout((int) (displayUtils.screenWidth() * 0.7f), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId == -1 ? R.layout.dialog_simple_confirm : layoutId, container);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvConfirm = view.findViewById(R.id.tv_confirm);

        if (title != null)
            tvTitle.setText(title);
        if (content != null)
            tvContent.setText(content);
        if (negativeText != null)
            tvCancel.setText(negativeText);
        if (positiveText != null)
            tvConfirm.setText(positiveText);

        tvCancel.setOnClickListener(onClickListener);
        tvConfirm.setOnClickListener(onClickListener);

        return view;
    }

    private SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View v) {
            int i = v.getId();
            if (i == R.id.tv_cancel) {
                isConfirmDialog = false;
                dismiss();
            } else if (i == R.id.tv_confirm) {
                isConfirmDialog = true;
                dismiss();
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null) {
            if (isConfirmDialog) {
                listener.onConfirm(tvConfirm);
            } else {
                listener.onCancel(tvCancel);
            }
        }
        isConfirmDialog = false;
        super.onDismiss(dialog);
    }
}
