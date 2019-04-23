package com.hykj.base.utils.math;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hykj.base.listener.SingleOnClickListener;

/**
 * 数学加减器
 * 请在frgament的onResume中使用该方法，不然会出现textWatcher多次调用
 * 不要再列表中使用EditText
 */
public class NumEditMathUtils {
    private TextView tvSub;//减
    private TextView tvAdd;//加
    private EditText etNum;//数量

    private int MIN_NUM = 1;//默认数量最少为1
    private int MAX_NUM = 99;//默认数量最大99
    private int curNum = 1;//当前数量，默认1
    private OnNumberChangeListener mListener;

    public NumEditMathUtils(TextView tvSub, TextView tvAdd, EditText etNum) {
        init(tvSub, tvAdd, etNum, null);
    }

    public NumEditMathUtils(TextView tvSub, TextView tvAdd, EditText etNum, OnNumberChangeListener listener) {
        init(tvSub, tvAdd, etNum, listener);
    }

    private void init(TextView tvSub, TextView tvAdd, EditText etNum, OnNumberChangeListener listener) {
        this.tvSub = tvSub;
        this.tvSub.setTag(NumChangeStatus.SUB);
        this.tvAdd = tvAdd;
        this.tvAdd.setTag(NumChangeStatus.ADD);
        this.etNum = etNum;
        this.etNum.setKeyListener(keyListener);
        this.mListener = listener;
        this.tvSub.setOnClickListener(onClickListener);
        this.tvAdd.setOnClickListener(onClickListener);
        this.etNum.addTextChangedListener(textWatcher);
    }

    //点击监听
    private SingleOnClickListener onClickListener = new SingleOnClickListener() {
        @Override
        public void onClickSub(View v) {
            boolean isSub = (int) v.getTag() == NumChangeStatus.SUB;
            int temp = curNum;
            if (isSub && curNum > MIN_NUM)
                curNum--;
            else if (!isSub && curNum < MAX_NUM)
                curNum++;
            if (temp != curNum) {
                etNum.setText(String.valueOf(curNum));
                etNum.setSelection(etNum.getText().toString().length() - 1);
            }
        }
    };

    /**
     * EditText文本监听
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String s1 = s.toString();
            int temp = curNum;
            boolean empty = TextUtils.isEmpty(s1);
            curNum = empty ? 1 : Integer.valueOf(s1);
            if (empty) {
                updateNumSelection();
            }
            if (mListener != null && !(empty && temp == 1))//原来的值为1，并且删除导致数字还是1的情况下不通知
                mListener.onNumChange(curNum);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateNumSelection() {
        this.etNum.removeTextChangedListener(textWatcher);
        this.etNum.setText(String.valueOf(curNum));
        this.etNum.addTextChangedListener(textWatcher);
        this.etNum.setSelection(etNum.getText().toString().length());
    }

    /**
     * 设置只能输入正整数
     */
    private KeyListener keyListener = new NumberKeyListener() {
        @Override
        protected char[] getAcceptedChars() {
            return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        }

        @Override
        public int getInputType() {
            return InputType.TYPE_CLASS_PHONE;
        }
    };

    public NumEditMathUtils setListener(OnNumberChangeListener listener) {
        this.mListener = listener;
        return this;
    }

    public NumEditMathUtils setMinOrMaxNum(int minNum, int maxNum) {
        if (minNum > maxNum)
            throw new RuntimeException("最小值不能大于最大值");
        this.MIN_NUM = minNum;
        this.MAX_NUM = maxNum;
        return this;
    }

    public int getCurNum() {
        return curNum;
    }

    public NumEditMathUtils setCurNum(int curNum) {
        this.curNum = curNum;
        updateNumSelection();
        return this;
    }

    //改变数量监听
    public interface OnNumberChangeListener {
        void onNumChange(int number);
    }

    @IntDef({NumChangeStatus.SUB, NumChangeStatus.ADD})
    public @interface NumChangeStatus {
        int SUB = 0;
        int ADD = 1;
    }
}
