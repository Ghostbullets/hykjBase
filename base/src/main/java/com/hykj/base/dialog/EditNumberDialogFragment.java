package com.hykj.base.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hykj.base.R;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.math.NumEditMathUtils;
import com.hykj.base.utils.math.NumberMathUtils;

public class EditNumberDialogFragment extends BaseDialogFragment {
    private int curNum;
    private OnConfirmListener mListener;
    private NumEditMathUtils mathUtils;

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout((int) (new DisplayUtils().screenWidth() * 0.6), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.dialog_edit_number, container);
        mathUtils = new NumEditMathUtils((TextView) itemView.findViewById(R.id.tv_sub), (TextView) itemView.findViewById(R.id.tv_add), (EditText) itemView.findViewById(R.id.et_num));
        mathUtils.setCurNum(curNum);
        itemView.findViewById(R.id.tv_cancel).setOnClickListener(onClickListener);
        itemView.findViewById(R.id.tv_confirm).setOnClickListener(onClickListener);
        return itemView;
    }

    private SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View v) {
            int i = v.getId();
            if (i == R.id.tv_cancel) {
                dismiss();

            } else if (i == R.id.tv_confirm) {
                dismiss();
                if (mListener != null)
                    mListener.OnConfirm(mathUtils.getCurNum());

            }
        }
    };

    public EditNumberDialogFragment setCurNum(int curNum) {
        this.curNum = curNum;
        return this;
    }

    public EditNumberDialogFragment setListener(OnConfirmListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnConfirmListener {
        void OnConfirm(int curNum);
    }
}
