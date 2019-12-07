package com.hykj.base.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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


/**
 * 更新中弹窗
 */
public class UpdatingDialogFragment extends BaseDialogFragment {
    private TextView tvUpdateContent;
    private static final int delayMillis = 500;
    private static final int WHAT = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String content = tvUpdateContent.getText().toString();
                    if (content.equals("更新中...")) {
                        content = "更新中.";
                    } else if (content.equals("更新中.")) {
                        content = "更新中..";
                    } else if (content.equals("更新中..")) {
                        content = "更新中...";
                    }
                    tvUpdateContent.setText(content);
                    sendEmptyMessageDelayed(WHAT, delayMillis);
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.dialog_app_updating, container);
        tvUpdateContent = itemView.findViewById(R.id.tv_update_content);
        mHandler.sendEmptyMessageDelayed(WHAT, delayMillis);
        return itemView;
    }
}
