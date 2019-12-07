package com.hykj.base.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hykj.base.R;
import com.hykj.base.listener.SingleOnClickListener;
import com.hykj.base.utils.DisplayUtils;
import com.hykj.base.utils.math.NumEditMathUtils;

import java.util.Locale;

public class EditNumberDialogFragment extends BaseDialogFragment {
    private int curNum;
    private int MIN_NUM = 1;//默认数量最少为1
    private int MAX_NUM = 99;//默认数量最大99
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
        mathUtils.setMinOrMaxNum(MIN_NUM, MAX_NUM).setCurNum(curNum);
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
                if (mathUtils.getCurNum() > MAX_NUM || mathUtils.getCurNum() < MIN_NUM) {
                    String tip;
                    if (mathUtils.getCurNum() > MAX_NUM)
                        tip = String.format(Locale.getDefault(), "最大不能大于%d", MAX_NUM);
                    else
                        tip = String.format(Locale.getDefault(), "最小不能小于%d", MIN_NUM);
                    Toast.makeText(getContext(), tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if (mListener != null)
                    mListener.OnConfirm(mathUtils.getCurNum());
            }
        }
    };

    public EditNumberDialogFragment setCurNum(int curNum) {
        if (curNum < MIN_NUM)
            curNum = MIN_NUM;
        if (curNum > MAX_NUM)
            curNum = MAX_NUM;
        this.curNum = curNum;
        return this;
    }

    public EditNumberDialogFragment setMinOrMaxNum(int minNum, int maxNum) {
        if (minNum > maxNum)
            throw new RuntimeException("The minimum cannot be greater than the maximum");
        this.MIN_NUM = minNum;
        this.MAX_NUM = maxNum;
        if (curNum < MIN_NUM || curNum > MAX_NUM) {
            if (curNum < MIN_NUM)
                curNum = MIN_NUM;
            if (curNum > MAX_NUM)
                curNum = MAX_NUM;
        }
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
