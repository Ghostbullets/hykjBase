package com.hykj.base.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hykj.base.R;


/**
 * created by cjf
 * on: 2019/9/3
 * 基础DialogFragment
 */
public abstract class BasicsDialogFragment extends DialogFragment {
    // 宿主对象
    protected FragmentActivity mActivity;
    protected View itemView;
    protected boolean isViewCreated;
    private Object tagEx;

    public Object getTagEx() {
        return tagEx;
    }

    public void setTagEx(Object tagEx) {
        this.tagEx = tagEx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemView = inflater.inflate(getLayoutId(), null);
        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        init();
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract @LayoutRes
    int getLayoutId();

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 获取控件
     *
     * @param id
     * @return
     */
    public <T extends View> T findViewById(int id) {
        return (T) itemView.findViewById(id);
    }
}
