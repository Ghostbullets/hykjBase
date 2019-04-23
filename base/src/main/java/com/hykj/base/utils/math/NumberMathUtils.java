package com.hykj.base.utils.math;

import android.support.annotation.IntDef;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.hykj.base.dialog.EditNumberDialogFragment;
import com.hykj.base.listener.SingleOnClickListener;


/**
 * 数学加减器
 * 千万不要在列表中使用EditText
 */
public class NumberMathUtils {
    private TextView tvSub;//减
    private TextView tvAdd;//加
    private TextView tvNum;//数量

    private int MIN_NUM = 1;//默认数量最少为1
    private int MAX_NUM = 99;//默认数量最大99
    private int curNum = 1;//当前数量，默认1
    private OnNumberChangeListener mListener;

    public NumberMathUtils(FragmentActivity activity, TextView tvSub, TextView tvAdd, TextView tvNum) {
        init(activity, tvSub, tvAdd, tvNum, null);
    }

    public NumberMathUtils(FragmentActivity activity, TextView tvSub, TextView tvAdd, TextView tvNum, OnNumberChangeListener listener) {
        init(activity, tvSub, tvAdd, tvNum, listener);
    }

    private void init(final FragmentActivity activity, TextView tvSub, TextView tvAdd, TextView tvNum, OnNumberChangeListener listener) {
        this.tvSub = tvSub;
        this.tvSub.setTag(NumChangeStatus.SUB);
        this.tvAdd = tvAdd;
        this.tvAdd.setTag(NumChangeStatus.ADD);
        this.tvNum = tvNum;
        this.mListener = listener;
        this.tvSub.setOnClickListener(onClickListener);
        this.tvAdd.setOnClickListener(onClickListener);
        this.tvNum.setOnClickListener(new SingleOnClickListener() {
            @Override
            public void onClickSub(View v) {
                new EditNumberDialogFragment().setCurNum(curNum).setListener(new EditNumberDialogFragment.OnConfirmListener() {
                    @Override
                    public void OnConfirm(int curNum) {
                        changeNumber(curNum, true);
                    }
                }).show(activity.getSupportFragmentManager(), "EditNumberDialogFragment");
            }
        });
    }

    private void changeNumber(int num, boolean isChange) {
        curNum = num;
        tvNum.setText(String.valueOf(curNum));
        if (isChange && mListener != null)
            mListener.onNumChange(curNum);
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
                changeNumber(curNum, true);
            }
        }
    };

    public void setListener(OnNumberChangeListener listener) {
        this.mListener = listener;
    }

    public NumberMathUtils setMinOrMaxNum(int minNum, int maxNum) {
        if (minNum > maxNum)
            throw new RuntimeException("最小值不能大于最大值");
        this.MIN_NUM = minNum;
        this.MAX_NUM = maxNum;
        if (curNum < MIN_NUM || curNum > MAX_NUM) {
            if (curNum < MIN_NUM)
                curNum = MIN_NUM;
            if (curNum > MAX_NUM)
                curNum = MAX_NUM;
            changeNumber(curNum, true);
        }
        return this;
    }

    public int getCurNum() {
        return curNum;
    }

    public NumberMathUtils setCurNum(int curNum) {
        return setCurNum(curNum, false);
    }

    public NumberMathUtils setCurNum(int curNum, boolean isChange) {
        if (curNum < MIN_NUM)
            curNum = MIN_NUM;
        if (curNum > MAX_NUM)
            curNum = MAX_NUM;
        changeNumber(curNum, isChange);
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
