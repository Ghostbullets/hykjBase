package com.hykj.base.dialog;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * 基础通用确认、取消提示框
 * 可以传入一个布局id，应用于该DialogFragment，传null则使用默认布局，注意传入的布局，确认按钮id=tv_confirm 取消id=tv_cancel 标题id=tv_title 内容id=tv_content
 * 也可以重写{@link BasicConfirmDialogFragment#onStart()} 方法重新设置dialog的宽高
 * Created by cjf on 2019/08/02
 */

public class BasicConfirmDialogFragment extends BasicsDialogFragment {

    private CharSequence title;// 标题
    private CharSequence content;// 内容

    private CharSequence positiveText;//确认
    private CharSequence negativeText;//取消
    protected OnSelectClickListener listener;
    protected TextView tvCancel;
    protected TextView tvConfirm;

    private @LayoutRes
    int layoutId = -1;//当继承该类重写getLayout()方法时，该参数设置无效；不重写则该参数设置有效
    protected boolean isConfirmDialog = false;
    protected boolean cancelOnTouchOutSideOrBack = true;//是否可以通过点击返回键、窗口以外的区域dismiss弹窗
    protected int windowWidth = 0;//窗口宽
    protected int windowHeight = 0;//窗口宽
    protected int windowGravity = Gravity.CENTER;//窗口位置

    public BasicConfirmDialogFragment setData(CharSequence title, CharSequence content, CharSequence mPositiveButtonText, CharSequence mNegativeButtonText) {
        this.title = title;
        this.content = content;
        this.positiveText = mPositiveButtonText;
        this.negativeText = mNegativeButtonText;
        return this;
    }

    public BasicConfirmDialogFragment setCancelOnTouchOutSideOrBack(boolean cancelOnTouchOutSideOrBack) {
        this.cancelOnTouchOutSideOrBack = cancelOnTouchOutSideOrBack;
        return this;
    }

    public BasicConfirmDialogFragment setWindowLayout(int windowWidth, int windowHeight, int windowGravity) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowGravity = windowGravity;
        return this;
    }

    public BasicConfirmDialogFragment setLayoutId(@LayoutRes int resource) {
        layoutId = resource;
        return this;
    }

    public BasicConfirmDialogFragment setListener(OnSelectClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCancelable(cancelOnTouchOutSideOrBack);
        getDialog().setCanceledOnTouchOutside(cancelOnTouchOutSideOrBack);
        Window window = getDialog().getWindow();
        if (window != null) {
            if ((windowWidth == -1 || windowWidth == -2 || windowWidth > 0) && (windowHeight == -1 || windowHeight == -2 || windowHeight > 0)) {
                window.setLayout(windowWidth, windowHeight);
            } else {
                DisplayUtils displayUtils = new DisplayUtils(getContext());
                window.setLayout((int) (displayUtils.screenWidth() * 0.7f), WindowManager.LayoutParams.WRAP_CONTENT);
            }
            try {
                window.setGravity(windowGravity);
            } catch (Exception e) {
                e.printStackTrace();
                window.setGravity(Gravity.CENTER);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId() == -1 ? com.hykj.base.R.layout.dialog_simple_confirm : getLayoutId();
        itemView = inflater.inflate(layoutId, null);
        return itemView;
    }

    @Override
    protected int getLayoutId() {
        return layoutId;
    }

    @Override
    protected void init() {
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        TextView tvContent = itemView.findViewById(R.id.tv_content);
        tvCancel = itemView.findViewById(R.id.tv_cancel);
        tvConfirm = itemView.findViewById(R.id.tv_confirm);

        if (title != null)
            tvTitle.setText(title);
        tvTitle.setVisibility(title != null ? View.VISIBLE : View.GONE);
        if (content != null)
            tvContent.setText(content);
        if (negativeText != null)
            tvCancel.setText(negativeText);
        if (positiveText != null)
            tvConfirm.setText(positiveText);

        tvCancel.setOnClickListener(onClickListener);
        tvConfirm.setOnClickListener(onClickListener);
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
    public void dismiss() {
        if (listener != null) {
            if (isConfirmDialog) {
                listener.onConfirm(tvConfirm);
            } else {
                listener.onCancel(tvCancel);
            }
        }
        isConfirmDialog = false;
        super.dismiss();
    }
}
