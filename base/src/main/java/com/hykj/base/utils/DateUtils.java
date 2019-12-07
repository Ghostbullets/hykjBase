package com.hykj.base.utils;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 日期处理实用对象
 *
 * @author LZR 2017.06.13
 * @version 1.0
 */

public class DateUtils {

    private static SimpleDateFormat df_normal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static SimpleDateFormat df_time = new SimpleDateFormat("HH:mm", Locale.US);
    private static SimpleDateFormat df_day_time = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
    private static SimpleDateFormat df_year_day = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static SimpleDateFormat df_year_month = new SimpleDateFormat("yyyy-MM", Locale.US);
    private static SimpleDateFormat df_year_day_time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    @IntDef({DateFormatType.DF_NORMAL, DateFormatType.DF_TIME, DateFormatType.DF_DAY_TIME,
            DateFormatType.DF_YEAR_DAY, DateFormatType.DF_YEAR_MONTH, DateFormatType.DF_YEAR_DAY_TIME})
    public @interface DateFormatType {
        int DF_NORMAL = 0;
        int DF_TIME = 1;
        int DF_DAY_TIME = 2;
        int DF_YEAR_DAY = 3;
        int DF_YEAR_MONTH = 4;
        int DF_YEAR_DAY_TIME = 5;
    }

    public static String getFormatDate(Date date, @DateFormatType Integer formatType) {
        if (date == null)
            date = new Date(System.currentTimeMillis());
        if (formatType == null)
            formatType = DateFormatType.DF_YEAR_DAY;
        String formatDate = null;
        switch (formatType) {
            case DateFormatType.DF_TIME:
                formatDate = df_time.format(date);
                break;
            case DateFormatType.DF_DAY_TIME:
                formatDate = df_day_time.format(date);
                break;
            case DateFormatType.DF_YEAR_DAY:
                formatDate = df_year_day.format(date);
                break;
            case DateFormatType.DF_YEAR_MONTH:
                formatDate = df_year_month.format(date);
                break;
            case DateFormatType.DF_YEAR_DAY_TIME:
                formatDate = df_year_day_time.format(date);
                break;
            case DateFormatType.DF_NORMAL:
                formatDate = df_normal.format(date);
                break;
        }
        return formatDate;
    }

    public static String getFormatDate(long time, @DateFormatType Integer formatType) {
        return getFormatDate(new Date(time), formatType);
    }

    /**
     * 时间间隔
     *
     * @param checkTime
     * @return
     */
    public static String checkTimeInterval(Long checkTime) {
        return checkTimeInterval(null, checkTime);
    }

    /**
     * 时间间隔
     *
     * @param currentDate
     * @param checkTime
     * @return
     */
    public static String checkTimeInterval(Date currentDate, Long checkTime) {

        if (checkTime == null)
            return "";

        long infoTimeMillis = checkTime / 10000000000l > 1 ? checkTime
                : checkTime * 1000;

        long currentTime = currentDate == null ? System.currentTimeMillis() : currentDate.getTime();
        long diff = (currentTime - infoTimeMillis) / (1000 * 60);// 分钟为单位进行判断
        if (diff <= 1) {
            return "刚刚";
        } else if (diff <= 60) {
            return diff + "分钟之前";
        } else {
            Calendar calendar = Calendar.getInstance();
            int nowDay = calendar.get(Calendar.DAY_OF_YEAR);
            int nowYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(infoTimeMillis);
            int infoDay = calendar.get(Calendar.DAY_OF_YEAR);
            int intoYear = calendar.get(Calendar.YEAR);
            int diffDay = nowDay - infoDay;

            Date infoDate = new Date(infoTimeMillis);

            if (diffDay < 1) {
                return "今天" + df_time.format(infoDate);
            } else if (diffDay < 2) {
                return "昨天" + df_time.format(infoDate);
            } else {
                int diffYear = nowYear - intoYear;
                if (diffYear < 1)
                    return df_day_time.format(infoDate);
                else
                    return df_year_day_time.format(infoDate);
            }
        }
    }

    /**
     * 时间字符串常规转换
     *
     * @param strDate
     * @return
     */
    public static Date parseNormalTime(String strDate) {
        Date date;
        try {
            date = df_normal.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }

        return date;
    }

    /**
     * 时间格式统一化
     *
     * @param strDate 格式 yyyy-MM-dd HH:mm:ss
     * @return 返回yyyy-MM-dd格式字符串
     */
    public static String formatDate(String strDate) {
        return df_year_day.format(parseNormalTime(strDate));
    }

    /**
     * 统一时间戳
     *
     * @param timestamp 时间戳
     * @return 13位时间戳
     */
    public static long unityTimestamp(Long timestamp) {
        if (timestamp == null)
            return System.currentTimeMillis();
        return timestamp / 10000000000l > 1 ? timestamp
                : timestamp * 1000;
    }

    //移除时间保留日期
    public static Date removeTimeDate(Date date) {
        try {
            return df_year_day.parse(df_year_day.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    /**
     * 转换时间类型
     *
     * @param format 希望转换的格式
     * @param time   对应格式的时间字符串
     * @return
     */
    public static Date parseTime(String format, String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    //转换日期类型
    public static Date parseSimpleDate(String time) {
        return parseTime("yyyy-MM-dd", time);
    }

    //格式化时间
    public static String formatTime(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    //是否同一天
    public static boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    //获取日期差名称
    public static DiffDayInfo checkDiffDay(Date ori, Date diff) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(ori);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(diff);

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return new DiffDayInfo(Integer.MAX_VALUE, "");
        int diffDay = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        switch (diffDay) {
            case 0:
                return new DiffDayInfo(diffDay, "今天");
            case 1:
                return new DiffDayInfo(diffDay, "明天");
            case 2:
                return new DiffDayInfo(diffDay, "后天");
            case -1:
                return new DiffDayInfo(diffDay, "昨天");
            case -2:
                return new DiffDayInfo(diffDay, "前天");
            default:
                return new DiffDayInfo(diffDay, "");
        }
    }

    public static class DiffDayInfo {
        int diffDay;
        String diffName;

        public DiffDayInfo(int diffDay, String diffName) {
            this.diffDay = diffDay;
            this.diffName = diffName;
        }

        public int getDiffDay() {
            return diffDay;
        }

        public String getDiffName() {
            return diffName;
        }
    }

    /**
     * 时间转换为时间戳
     *
     * @param timeStr 时间 例如: 2016-03-09
     * @param format  时间对应格式  例如: yyyy-MM-dd
     * @return
     */
    public static long getTimeStamp(String timeStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将日期时间进行排序
     *
     * @param o1     时间1 例如: 2016-03-09
     * @param o2     时间2 例如: 2016-03-09
     * @param format 时间对应格式  例如: yyyy-MM-dd
     * @return
     */
    public static int diffValue(String o1, String o2, String format) {
        return (int) (getTimeStamp(o1, format) - getTimeStamp(o2, format));
    }

    /**
     * @param timeStamp 用于存储图片获取时间
     * @return
     */
    public static String parseStoreTimeStamp(long timeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        return dateFormat.format(new Date(timeStamp));
    }

    /**
     * 用于判断时间是否小于目标时间
     *
     * @param checkTime  要判断的时间  格式yyyy-MM-dd   2018-08-01
     * @param targetTime 被当做目标来判断的时间  格式yyyy-MM-dd   2019-06-01
     * @param type       判断类型
     * @return 如果checkTime小于targetTime，返回true，否者返回false
     */
    public static boolean isCheckTimeLessTargetTime(String checkTime, String targetTime, @DistanceDateType int type) {
        if (TextUtils.isEmpty(checkTime)) {
            throw new RuntimeException("checkTime不能为空");
        }
        if (TextUtils.isEmpty(targetTime)) {
            targetTime = df_year_day.format(new Date(System.currentTimeMillis()));
        }
        String[] splitCheck = checkTime.split("-");
        String[] splitTarget = targetTime.split("-");
        if (splitCheck.length < 3 || splitTarget.length < 3)
            throw new RuntimeException("传入的时间格式不对,请传入yyyy-MM-dd格式时间字符串");
        SimpleDateFormat sdf = df_year_day;
        switch (type) {
            case DistanceDateType.YEAR:
                checkTime = splitCheck[0];
                targetTime = splitTarget[0];
                sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
                break;
            case DistanceDateType.YEAR_MONTH:
                checkTime = String.format("%s-%s", splitCheck[0], splitCheck[1]);
                targetTime = String.format("%s-%s", splitTarget[0], splitTarget[1]);
                sdf = df_year_month;
                break;
            case DistanceDateType.YEAR_MONTH_DAY:
                break;
        }
        try {
            return sdf.parse(targetTime).after(sdf.parse(checkTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取一段时间的字符串时间组
     *
     * @param startTime 起始时间 格式yyyy-MM-dd   2018-08-01
     * @param endTime   结束时间  格式yyyy-MM-dd   2019-06-01
     * @param type      类型
     * @return
     */
    public static List<String> getDistanceDates(String startTime, String endTime, @DistanceDateType int type) {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            throw new RuntimeException("起始时间、结束时间不能为空");
        }
        String[] splitStart = startTime.split("-");
        String[] splitEnd = endTime.split("-");
        if (splitStart.length < 3 || splitEnd.length < 3)
            throw new RuntimeException("传入的时间格式不对");
        SimpleDateFormat sdf;
        switch (type) {
            case DistanceDateType.YEAR:
                startTime = splitStart[0];
                endTime = splitEnd[0];
                sdf = new SimpleDateFormat("yyyy", Locale.US);
                break;
            case DistanceDateType.YEAR_MONTH:
                startTime = String.format("%s-%s", splitStart[0], splitStart[0]);
                endTime = String.format("%s-%s", splitEnd[0], splitEnd[0]);
                sdf = df_year_month;
                break;
            case DistanceDateType.YEAR_MONTH_DAY:
            default:
                sdf = df_year_day;
                break;
        }
        List<String> list = new ArrayList<>();
        list.add(startTime);
        try {
            Date dStart = sdf.parse(startTime);
            Date dEnd = sdf.parse(endTime);
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(dStart);
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(dEnd);
            //如果结束时间大于开始时间，则循环
            while (dEnd.after(calStart.getTime())) {
                switch (type) {
                    case DistanceDateType.YEAR:
                        calStart.add(Calendar.YEAR, 1);
                        break;
                    case DistanceDateType.YEAR_MONTH:
                        calStart.add(Calendar.MONTH, 1);
                        break;
                    case DistanceDateType.YEAR_MONTH_DAY:
                        calStart.add(Calendar.DAY_OF_MONTH, 1);
                        break;
                }
                list.add(sdf.format(calStart));
            }
        } catch (Exception e) {
        }
        return list;
    }

    @IntDef({DistanceDateType.YEAR, DistanceDateType.YEAR_MONTH, DistanceDateType.YEAR_MONTH_DAY})
    public @interface DistanceDateType {//获取一段时间的字符串时间组  0按年份   1按年月   按年月日
        int YEAR = 0;
        int YEAR_MONTH = 1;
        int YEAR_MONTH_DAY = 2;
    }
}
