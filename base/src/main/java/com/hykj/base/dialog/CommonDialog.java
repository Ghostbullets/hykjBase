package com.hykj.base.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DisplayUtils;


/**
 * 显示权限提示框
 * Created by LZR on 2017/11/6.
 */

public class CommonDialog extends DialogFragment {

    private CharSequence mTitle;// 标题
    private CharSequence mMessage;// 内容

    private CharSequence mPositiveButtonText;
    private CharSequence mNegativeButtonText;
    private OnSelectClickListener mListener;
    private boolean isCancel;

    public CommonDialog setData(CharSequence mTitle, CharSequence mMessage, CharSequence mPositiveButtonText, CharSequence mNegativeButtonText) {
        this.mTitle = mTitle;
        this.mMessage = mMessage;
        this.mPositiveButtonText = mPositiveButtonText;
        this.mNegativeButtonText = mNegativeButtonText;
        return this;
    }

    public CommonDialog setListener(OnSelectClickListener listener) {
        this.mListener = listener;
        return this;
    }

    private Object tagEx;

    public Object getTagEx() {
        return tagEx;
    }

    public void setTagEx(Object tagEx) {
        this.tagEx = tagEx;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getContext(), R.style.CustomDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        DisplayUtils displayUtils = new DisplayUtils(getContext());
        window.setLayout((int) (displayUtils.screenWidth() * 0.7f), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm, container);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);

        if (mTitle != null)
            tvTitle.setText(mTitle);
        if (mMessage != null)
            tvContent.setText(mMessage);
        if (mNegativeButtonText != null)
            tvCancel.setText(mNegativeButtonText);
        if (mPositiveButtonText != null)
            tvConfirm.setText(mPositiveButtonText);

        tvCancel.setOnClickListener(onClickListener);
        tvConfirm.setOnClickListener(onClickListener);

        return view;
    }

    private SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View v) {
            int i = v.getId();
            if (i == R.id.tv_cancel) {
                isCancel = true;
                dismiss();
            } else if (i == R.id.tv_confirm) {
                dismiss();
                if (mListener != null)
                    mListener.onConfirm(v);
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null && isCancel) {
            mListener.onCancel(null);
            isCancel = false;
        }
    }

    public interface OnSelectClickListener {
        void onConfirm(View v);

        void onCancel(View v);
    }

    public abstract static class OnConfirmClickListener implements OnSelectClickListener {

        @Override
        public void onCancel(View v) {

        }
    }

}
