package com.hykj.base.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.hykj.base.utils.ContextKeep;
import com.hykj.base.utils.DisplayUtils;

/**
 * 显示权限提示框
 * Created by LZR on 2017/11/6.
 */

public class ShowPermissionDialog extends DialogFragment {

    private CharSequence mTitle;// 标题
    private CharSequence mMessage;// 内容

    private CharSequence mPositiveButtonText;
    private CharSequence mNegativeButtonText;

    public ShowPermissionDialog setData(CharSequence mTitle, CharSequence mMessage, CharSequence mPositiveButtonText, CharSequence mNegativeButtonText) {
        this.mTitle = mTitle;
        this.mMessage = mMessage;
        this.mPositiveButtonText = mPositiveButtonText;
        this.mNegativeButtonText = mNegativeButtonText;
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
                dismiss();

            } else if (i == R.id.tv_confirm) {
                dismiss();
                //前往设置
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.setData(Uri.fromParts("package", ContextKeep.getContext().getPackageName(), null));
                startActivity(localIntent);
            }
        }
    };

}
