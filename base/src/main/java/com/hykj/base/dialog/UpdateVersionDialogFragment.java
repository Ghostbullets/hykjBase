package com.hykj.base.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.bean.AppVersionInfo;
import com.hykj.base.listener.OnSelectClickListener;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.text.StringUtils;

/**
 * 版本更新弹窗
 */
public class UpdateVersionDialogFragment extends BaseDialogFragment {
    private AppVersionInfo versionInfo;
    private CharSequence mPositiveButtonText;
    private CharSequence mNegativeButtonText;
    private OnSelectClickListener mOnSelectClickListener;

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        getDialog().setCanceledOnTouchOutside(!versionInfo.isForcedUpdate);
        getDialog().setCancelable(!versionInfo.isForcedUpdate);
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout((int) (new DisplayUtils(getContext()).screenWidth() * 0.7), WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.dialog_update_version, container);
        ((TextView) itemView.findViewById(R.id.tv_title)).setText(versionInfo.versionTitle);//标题
        ((TextView) itemView.findViewById(R.id.tv_version_name)).setText(String.format("%s:%s", getResources().getString(R.string.versions),versionInfo.versionName));//版本
        ((TextView) itemView.findViewById(R.id.tv_update_content)).setText(StringUtils.getValueByDefault(versionInfo.updateContent, getResources().getString(R.string.No_update_is_available)));//更新内容

        TextView tvVersionDesc = itemView.findViewById(R.id.tv_version_desc);//版本描述
        if (!TextUtils.isEmpty(versionInfo.versionDesc))
            tvVersionDesc.setText(String.format("%s:%s", getResources().getString(R.string.Version_described),versionInfo.versionDesc));
        else
            tvVersionDesc.setVisibility(View.GONE);

        TextView tvPackageSize = itemView.findViewById(R.id.tv_package_size);//包大小
        if (!TextUtils.isEmpty(versionInfo.packageSize))
            tvPackageSize.setText(String.format("%s:%s", getResources().getString(R.string.packet_size),versionInfo.packageSize));
        else
            tvPackageSize.setVisibility(View.GONE);

        TextView tvUpdateTime = itemView.findViewById(R.id.tv_update_time);//更新时间
        if (!TextUtils.isEmpty(versionInfo.updateTime))
            tvUpdateTime.setText(String.format("%s:%s", getResources().getString(R.string.update_time),versionInfo.updateTime));
        else
            tvUpdateTime.setVisibility(View.GONE);

        TextView tvConfirm = itemView.findViewById(R.id.tv_confirm);
        if (!TextUtils.isEmpty(mPositiveButtonText)) {
            tvConfirm.setText(mPositiveButtonText);//确定更新
        }
        tvConfirm.setOnClickListener(onClickListener);
        TextView tvCancel = itemView.findViewById(R.id.tv_cancel);
        if (!TextUtils.isEmpty(mNegativeButtonText)) {
            tvCancel.setText(mNegativeButtonText);//不更新
        }
        tvCancel.setOnClickListener(onClickListener);
        return itemView;
    }

    private SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View v) {
            int i = v.getId();
            dismiss();
            if (i == R.id.tv_confirm) {
                if (mOnSelectClickListener != null)
                    mOnSelectClickListener.onConfirm(v);
            } else if (i == R.id.tv_cancel) {
                if (mOnSelectClickListener != null)
                    mOnSelectClickListener.onCancel(v);
            }
        }
    };

    public UpdateVersionDialogFragment setData(AppVersionInfo versionInfo, CharSequence positiveButtonText, CharSequence negativeButtonText) {
        this.versionInfo = versionInfo;
        mPositiveButtonText = positiveButtonText;
        mNegativeButtonText = negativeButtonText;
        return this;
    }

    public UpdateVersionDialogFragment setOnSelectClickListener(OnSelectClickListener onSelectClickListener) {
        this.mOnSelectClickListener = onSelectClickListener;
        return this;
    }
}
