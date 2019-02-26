/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hykj.base.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.hykj.base.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple dialog containing an {@link DatePicker}.
 *
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 * 注意，Calendar.set(Calendar.MONTH, month-1)需要将传入的月份-1，因为Calendar月份是从0-11的，0代表1月份
 * Calendar.get(Calendar.MONTH)+1 需要将拿到的月份+1
 *
 * 使用：new DoubleDatePickerDialog.Builder().setXXX().build().show();
 */
public class DoubleDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {
    private static final String START_YEAR = "start_year";
    private static final String END_YEAR = "end_year";
    private static final String START_MONTH = "start_month";
    private static final String END_MONTH = "end_month";
    private static final String START_DAY = "start_day";
    private static final String END_DAY = "end_day";
    /*  private int beginStartYear = 2010, beginStartMonth = 1, beginStartDay = 1, beginEndYear = 2020, beginEndMonth = 12, beginEndDay = 31;
      private int overStartYear = 2010, overStartMonth = 1, overStartDay = 1, overEndYear = 2020, overEndMonth = 12, overEndDay = 31;*/
    //开始日期选中的年、月、日                                                     结束日期选中的年、月、日
    private int selectBeginYear, selectBeginMonth, selectBeginDay, selectOverYear, selectOverMonth, selectOverDay;

    private final DatePicker mDatePicker_start;//开始日期选择器
    private final DatePicker mDatePicker_end;//结束日期选择器
    private OnDateSetListener mDateSetListener;//确定按钮监听
    private OnDateChangeListener mDateChangeListener;//改变日期监听

    /**
     * @param context  The context the dialog is to run in.
     * @param theme    the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param builder  建筑者模式
     */
    public DoubleDatePickerDialog(Context context, int theme, OnDateSetListener callBack, Builder builder, boolean isDayVisible) {
        super(context, theme);
        selectBeginYear = builder.selectBeginYear;
        selectBeginMonth = builder.selectBeginMonth;
        selectBeginDay = builder.selectBeginDay;
        selectOverYear = builder.selectOverYear;
        selectOverMonth = builder.selectOverMonth;
        selectOverDay = builder.selectOverDay;
        mDateSetListener = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);
        setIcon(0);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.date_picker_dialog, null);
        setView(view);
        mDatePicker_start = view.findViewById(R.id.datePickerStart);
        mDatePicker_end = view.findViewById(R.id.datePickerEnd);
        initDatePicker(builder, isDayVisible);
    }

    //初始化选择器
    private void initDatePicker(Builder builder, boolean isDayVisible) {
        mDatePicker_start.init(selectBeginYear, selectBeginMonth - 1, selectBeginDay, this);
        Calendar cStart = Calendar.getInstance();
        cStart.set(Calendar.YEAR, builder.beginStartYear);
        cStart.set(Calendar.MONTH, builder.beginStartMonth - 1);
        cStart.set(Calendar.DATE, builder.beginStartDay);
        mDatePicker_start.setMinDate(cStart.getTime().getTime());
        cStart.set(Calendar.YEAR, builder.beginEndYear);
        cStart.set(Calendar.MONTH, builder.beginEndMonth - 1);
        cStart.set(Calendar.DATE, builder.beginEndDay);
        mDatePicker_start.setMaxDate(cStart.getTime().getTime());

        mDatePicker_end.init(selectOverYear, selectOverMonth - 1, selectOverDay, this);
        Calendar cEnd = Calendar.getInstance();
        cEnd.set(Calendar.YEAR, builder.overStartYear);
        cEnd.set(Calendar.MONTH, builder.overStartMonth - 1);
        cEnd.set(Calendar.DATE, builder.overStartDay);
        mDatePicker_end.setMinDate(cEnd.getTime().getTime());
        cEnd.set(Calendar.YEAR, builder.overEndYear);
        cEnd.set(Calendar.MONTH, builder.overEndMonth - 1);
        cEnd.set(Calendar.DATE, builder.overEndDay);
        mDatePicker_end.setMaxDate(cEnd.getTime().getTime());

        resizePicker(mDatePicker_start);
        resizePicker(mDatePicker_end);

        // 如果要隐藏当前日期，则使用下面方法。
        if (!isDayVisible) {
            hidDay(mDatePicker_start);
            hidDay(mDatePicker_end);
        }
    }

    /**
     * 隐藏DatePicker中的日显示
     *
     * @param datePicker
     */
    private void hidDay(DatePicker datePicker) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int daySpinnerId = getContext().getResources().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0) {
                View daySpinner = datePicker.findViewById(daySpinnerId);
                if (daySpinner != null)
                    daySpinner.setVisibility(View.GONE);
            }
        } else {
            try {
                Field field = datePicker.getClass().getDeclaredField("mDaySpinner");
                field.setAccessible(true);
                ((View) field.get(datePicker)).setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) // 如果是"取 消"按钮，则返回，如果是"确 定"按钮，则往下执行
            tryNotifyDateSet();
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {//监听日期更改
        boolean isBeginPicker = view.getId() == R.id.datePickerStart;
        if (isBeginPicker) {
            selectBeginYear = year;
            selectBeginMonth = monthOfYear;
            selectBeginDay = dayOfMonth;
        } else {
            selectOverYear = year;
            selectOverMonth = monthOfYear;
            selectOverDay = dayOfMonth;
        }
        if (mDateChangeListener != null)
            mDateChangeListener.onDateChanged(view, String.valueOf(year), formatMonthOrDay(monthOfYear), formatMonthOrDay(dayOfMonth), isBeginPicker);
    }

    /**
     * 获得开始日期的DatePicker
     *
     * @return The calendar view.
     */
    public DatePicker getDatePickerStart() {
        return mDatePicker_start;
    }

    /**
     * 获得结束日期的DatePicker
     *
     * @return The calendar view.
     */
    public DatePicker getDatePickerEnd() {
        return mDatePicker_end;
    }

    public void setBeginSelectedItem(int year, int monthOfYear, int dayOfMonth) {
        setBeginSelectedItem(year, monthOfYear, dayOfMonth, false);
    }

    /**
     * Sets the start date.
     *
     * @param year        The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth  The date day of month.
     * @param isListener  是否监听这次的设置动作
     */
    public void setBeginSelectedItem(int year, int monthOfYear, int dayOfMonth, boolean isListener) {
        mDatePicker_start.updateDate(year, monthOfYear, dayOfMonth);
        selectBeginYear = year;
        selectBeginMonth = monthOfYear;
        selectBeginDay = dayOfMonth;
        if (mDateChangeListener != null && isListener)
            mDateChangeListener.onDateChanged(mDatePicker_start, String.valueOf(year), formatMonthOrDay(monthOfYear), formatMonthOrDay(dayOfMonth), true);
    }

    public void setOverSelectedItem(int year, int monthOfYear, int dayOfMonth) {
        setOverSelectedItem(year, monthOfYear, dayOfMonth, false);
    }

    /**
     * Sets the end date.
     *
     * @param year        The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth  The date day of month.
     * @param isListener  是否监听这次的设置动作
     */
    public void setOverSelectedItem(int year, int monthOfYear, int dayOfMonth, boolean isListener) {
        mDatePicker_end.updateDate(year, monthOfYear, dayOfMonth);
        selectOverYear = year;
        selectOverMonth = monthOfYear;
        selectOverDay = dayOfMonth;
        if (mDateChangeListener != null && isListener)
            mDateChangeListener.onDateChanged(mDatePicker_end, String.valueOf(year), formatMonthOrDay(monthOfYear), formatMonthOrDay(dayOfMonth), false);
    }

    private void tryNotifyDateSet() {
        if (verifyStartDateMoreThanEndDate()) {
            Toast.makeText(getContext(), "开始日期不能大于结束日期", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mDateSetListener != null) {
            mDatePicker_start.clearFocus();
            mDatePicker_end.clearFocus();
            mDateSetListener.onDateSet(mDatePicker_start, String.valueOf(mDatePicker_start.getYear()), formatMonthOrDay(mDatePicker_start.getMonth() + 1),
                    formatMonthOrDay(mDatePicker_start.getDayOfMonth()), mDatePicker_end, String.valueOf(mDatePicker_end.getYear()),
                    formatMonthOrDay(mDatePicker_end.getMonth() + 1), formatMonthOrDay(mDatePicker_end.getDayOfMonth()));
        }
    }

    //检测开始日期是否小于结束日期
    public boolean verifyStartDateMoreThanEndDate() {
        Calendar cStart = Calendar.getInstance();
        cStart.set(Calendar.YEAR, mDatePicker_start.getYear());
        cStart.set(Calendar.MONTH, mDatePicker_start.getMonth());
        cStart.set(Calendar.DAY_OF_MONTH, mDatePicker_start.getDayOfMonth());
        Calendar cEnd = Calendar.getInstance();
        cEnd.set(Calendar.YEAR, mDatePicker_end.getYear());
        cEnd.set(Calendar.MONTH, mDatePicker_end.getMonth());
        cEnd.set(Calendar.DAY_OF_MONTH, mDatePicker_end.getDayOfMonth());
        return cStart.getTime().after(cEnd.getTime());
    }

    //格式化月、日
    public String formatMonthOrDay(int time) {
        String formatTime = String.valueOf(time);
        if (formatTime.length() == 1)
            formatTime = "0" + formatTime;
        return formatTime;
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(START_YEAR, mDatePicker_start.getYear());
        state.putInt(START_MONTH, mDatePicker_start.getMonth());
        state.putInt(START_DAY, mDatePicker_start.getDayOfMonth());
        state.putInt(END_YEAR, mDatePicker_end.getYear());
        state.putInt(END_MONTH, mDatePicker_end.getMonth());
        state.putInt(END_DAY, mDatePicker_end.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int start_year = savedInstanceState.getInt(START_YEAR);
        int start_month = savedInstanceState.getInt(START_MONTH);
        int start_day = savedInstanceState.getInt(START_DAY);
        mDatePicker_start.init(start_year, start_month, start_day, this);

        int end_year = savedInstanceState.getInt(END_YEAR);
        int end_month = savedInstanceState.getInt(END_MONTH);
        int end_day = savedInstanceState.getInt(END_DAY);
        mDatePicker_end.init(end_year, end_month, end_day, this);
    }

    /**
     * 调整FrameLayout的大小
     */
    private void resizePicker(FrameLayout tp) {        //DatePicker和TimePicker继承自FrameLayout
        List<NumberPicker> npList = findNumberPicker(tp);  //找到组成的NumberPicker
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);      //调整每个NumberPicker的宽度
        }
    }

    /**
     * 得到viewGroup 里面的NumberPicker组件
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<>();
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        npList.addAll(result);
                    }
                }
            }
        }
        return npList;
    }

    /**
     * 调整numberpicker大小
     */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }


    public static final class Builder {
        //开始日期的起始年、月、日，结束年、月、日
        private int beginStartYear = 1900;
        private int beginStartMonth = 1;
        private int beginStartDay = 1;
        private int beginEndYear = 2100;
        private int beginEndMonth = 12;
        private int beginEndDay = 31;

        //结束日期的起始年、月、日，结束年、月、日
        private int overStartYear = 1900;
        private int overStartMonth = 1;
        private int overStartDay = 1;
        private int overEndYear = 2100;
        private int overEndMonth = 12;
        private int overEndDay = 31;

        //开始日期选中的年、月、日
        private int selectBeginYear = 0, selectBeginMonth = 0, selectBeginDay = 0;
        //结束日期选中的年、月、日
        private int selectOverYear = 0, selectOverMonth = 0, selectOverDay = 0;

        public Builder() {
        }

        //设置开始日期的起始范围
        public Builder setBeginStartRange(int startYear, int startMonth, int startDay) {
            beginStartYear = startYear;
            beginStartMonth = startMonth;
            beginStartDay = startDay;
            return this;
        }

        //设置开始日期的结束范围
        public Builder setBeginEndRange(int endYear, int endMonth, int endDay) {
            beginEndYear = endYear;
            beginEndMonth = endMonth;
            beginEndDay = endDay;
            return this;
        }

        //设置结束日期的起始范围
        public Builder setOverStartRange(int startYear, int startMonth, int startDay) {
            overStartYear = startYear;
            overStartMonth = startMonth;
            overStartDay = startDay;
            return this;
        }

        //设置结束日期的结束范围
        public Builder setOverEndRange(int endYear, int endMonth, int endDay) {
            overEndYear = endYear;
            overEndMonth = endMonth;
            overEndDay = endDay;
            return this;
        }

        public Builder setBeginSelectedItem(int year, int month) {
            return setBeginSelectedItem(year, month, 1);
        }

        //设置开始日期当前选中日期
        public Builder setBeginSelectedItem(int year, int month, int day) {
            selectBeginYear = year;
            selectBeginMonth = month;
            selectBeginDay = day;
            return this;
        }

        public Builder setOverSelectedItem(int year, int month) {
            return setOverSelectedItem(year, month, 1);
        }

        //设置结束日期当前选中日期
        public Builder setOverSelectedItem(int year, int month, int day) {
            selectOverYear = year;
            selectOverMonth = month;
            selectOverDay = day;
            return this;
        }

        public DoubleDatePickerDialog build(Context context, OnDateSetListener callBack) {
            return build(context, DatePickerDialog.THEME_HOLO_LIGHT, callBack);
        }

        public DoubleDatePickerDialog build(Context context, int theme, OnDateSetListener callBack) {
            return build(context, theme, true, callBack);
        }

        public DoubleDatePickerDialog build(Context context, boolean isDayVisible, OnDateSetListener callBack) {
            return build(context, DatePickerDialog.THEME_HOLO_LIGHT, isDayVisible, callBack);
        }

        public DoubleDatePickerDialog build(Context context, int theme, boolean isDayVisible, OnDateSetListener callBack) {
            if (beginStartMonth < 1 || beginEndMonth < 1 || overStartMonth < 1 || overEndMonth < 1 || beginStartMonth > 12 || beginEndMonth > 12 || overStartMonth > 12 || overEndMonth > 12) {
                throw new IllegalArgumentException("Month out of range [1-12]");
            }
            //如果有不符合的月份，则将选中日期改为今天
            if (selectBeginMonth < 1 || selectOverMonth < 1 || selectBeginMonth > 12 || selectOverMonth > 12) {
                Calendar c = Calendar.getInstance();
                selectBeginYear = c.get(Calendar.YEAR);
                selectBeginMonth = c.get(Calendar.MONTH) + 1;
                selectBeginDay = c.get(Calendar.DATE);
                selectOverYear = c.get(Calendar.YEAR);
                selectOverMonth = c.get(Calendar.MONTH) + 1;
                selectOverDay = c.get(Calendar.DATE);
            }
            return new DoubleDatePickerDialog(context, theme, callBack, this, isDayVisible);
        }
    }

    public void setDateSetListener(OnDateSetListener dateSetListener) {
        this.mDateSetListener = dateSetListener;
    }

    public void setDateChangeListener(OnDateChangeListener dateChangeListener) {
        this.mDateChangeListener = dateChangeListener;
    }

    /**
     * The callback used to indicate the user is done filling in the date.
     * 用户点击确定按钮时的回调
     */
    public interface OnDateSetListener {
        /**
         * @param startDatePicker  开始时间选择器
         * @param startYear        开始时间年
         * @param startMonthOfYear 开始时间月
         * @param startDayOfMonth  开始时间日
         * @param endDatePicker    结束时间选择器
         * @param endYear          结束时间年
         * @param endMonthOfYear   结束时间月
         * @param endDayOfMonth    结束时间日
         */
        void onDateSet(DatePicker startDatePicker, String startYear, String startMonthOfYear, String startDayOfMonth,
                       DatePicker endDatePicker, String endYear, String endMonthOfYear, String endDayOfMonth);
    }

    /**
     * The callback used to indicate the user is done filling in the date.
     * 滚动时选中监听
     */
    public interface OnDateChangeListener {
        /**
         * @param view          时间选择器
         * @param year          选中年 格式yyyy
         * @param month         选中月 格式MM
         * @param day           选中日 格式dd
         * @param isBeginPicker 是否是开始日期选中监听，true代表在选择开始日期，false代表在选择结束日期
         */
        void onDateChanged(DatePicker view, String year, String month, String day, boolean isBeginPicker);
    }
}
