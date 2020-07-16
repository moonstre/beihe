package com.yiliao.chat.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期工具类
 * Created by ysj on 2016/11/18.
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
    // 时间格式
    private SimpleDateFormat mSimpleDateFormat;
    // 当前时间时间
    private Calendar cal = Calendar.getInstance();

    /**
     * 获取当前时间YYDateUtil类型
     */
    public DateUtil() {
    }

    /**
     * 根据字符串获取YYDateUtil类型时间
     *
     * @param dateString 时间字符串(格式：yyyy-MM-dd)
     */
    public DateUtil(String dateString) {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = mSimpleDateFormat.parse(dateString);
            cal.setTime(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据字符串获取YYDateUtil类型时间
     *
     * @param sDate   时间字符串
     * @param sFormat 传入时间字符串格式(例如：yyyy-MM-dd HH:mm:ss)
     */
    public DateUtil(String sDate, String sFormat) {
        mSimpleDateFormat = new SimpleDateFormat(sFormat);
        try {
            Date mDate = mSimpleDateFormat.parse(sDate);
            cal.setTime(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据系统Calendar类型获取YYDateUtil类型时间
     *
     * @param calendar 时间
     */
    public DateUtil(Calendar calendar) {
        cal = calendar;
    }

    /**
     * 根据系统Date类型获取YYDateUtil类型时间
     *
     * @param date 时间
     */
    public DateUtil(Date date) {
        cal.setTime(date);
    }

    /**
     * 获取年份
     */
    public int getYear() {
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取月份
     */
    public int getMonth() {
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取一月当中的第几天
     */
    public int getDayToMonth() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取一周当中的第几天 <br>
     * (0-6,星期天为0)
     */
    public int getDayToWeek() {
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获取一周当中的第几个星期
     */
    public int getDayToWeekInMonth() {
        return cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获取一个月当中的第几天
     */
    public int getDayToYear() {
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取小时
     */
    public int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分钟
     */
    public int getMinute() {
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 获取秒钟
     */
    public int getSecond() {
        return cal.get(Calendar.SECOND);
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return 时间戳
     */
    public long getTimestamp() {
        return cal.getTimeInMillis();
    }

    /**
     * 根据格式返回时间字符串
     *
     * @param sFormat 时间返回格式(如：yyyy-MM-dd HH:mm:ss SSSS)
     */
    public String getDateToFormat(String sFormat) {
        mSimpleDateFormat = new SimpleDateFormat(sFormat);
        return mSimpleDateFormat.format(cal.getTime());
    }

    /**
     * YYDateUtil转Date类型
     */
    public Date toDate() {
        return cal.getTime();
    }

    /**
     * YYDateUtil转Calendar类型
     */
    public Calendar toCalendar() {
        return cal;
    }

    /**
     * 计算时间
     *
     * @param dateMill 计算时间的毫秒
     */
    public void onCalculation(long dateMill) {
        cal.setTime(new Date(cal.getTime().getTime() + dateMill));
    }

    /**
     * 获取某月的最大天数
     *
     * @return
     */
    public int getMaxDayToMonth() {
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 减1秒
     */
    public void reduceOneSecond() {
        cal.add(Calendar.SECOND, -1);
    }

    /**
     * 加1秒
     */
    public void addOneSecond() {
        cal.add(Calendar.SECOND, 1);
    }

    /**
     * 减1天
     */
    public void reduceOneDay() {
        cal.add(Calendar.DAY_OF_MONTH, -1);
    }

    /**
     * 加1天
     */
    public void addOneDay() {
        cal.add(Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * 减1个月
     */
    public void reduceOneMonth() {
        cal.add(Calendar.MONTH, -1);
    }

    /**
     * 加1个月
     */
    public void addOneMonth() {
        cal.add(Calendar.MONTH, 1);
    }

    /**
     * 加几个月
     */
    public void addMonths(int number) {
        cal.add(Calendar.MONTH, number);
    }

    /**
     * @param mss 毫秒
     * @return days/hours/minutes/seconds
     */
    public static List<Long> getTimeList(long mss) {
        if (mss > 1000 || mss < -1000) {
            List<Long> duration = new ArrayList<>();
            duration.add(mss / (1000 * 60 * 60 * 24));
            duration.add((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            duration.add((mss % (1000 * 60 * 60)) / (1000 * 60));
            duration.add((mss % (1000 * 60)) / 1000);
            return duration;
        } else {
            return null;
        }
    }

    /**
     * @return *天*小时*分*秒
     */
    public static String formatDuring(DateUtil begin, DateUtil end) {
        return formatDuring(Math.abs(begin.toDate().getTime() - (end.toDate().getTime())));
    }

    /**
     * @return *天*小时*分*秒
     */
    public static String formatDuring(long mss) {
        List<Long> duration = getTimeList(mss);
        if (duration != null) {
            return (duration.get(0) > 0 ? (duration.get(0) + "天") : "") + (duration.get(1) > 0 ? (duration.get(1) + "小时") : "")
                    + (duration.get(2) > 0 ? (duration.get(2) + "分") : "") + (duration.get(3) > 0 ? (duration.get(3) + "秒") : "");
        } else {
            return "0秒";
        }
    }

    /**
     * @return 00:00:00
     */
    public static String formatDuring2(long mss) {
        List<Long> duration = getTimeList(mss);
        if (duration != null) {
            String str = "";
            if (duration.get(0) > 0 || duration.get(1) > 0) {
                str += (24 * duration.get(0) + duration.get(1)) + ":";
            }
            if (duration.get(2) > 0) {
                if (duration.get(2) >= 10) {
                    str += duration.get(2) + ":";
                } else {
                    str += "0" + duration.get(2) + ":";
                }
            } else {
                str += "00:";
            }
            if (duration.get(3) > 0) {
                if (duration.get(3) >= 10) {
                    str += duration.get(3);
                } else {
                    str += "0" + duration.get(3);
                }
            } else {
                str += "00";
            }
            return str;
        } else {
            return "00:00";
        }
    }

    /**
     * @return 小时：分
     */
    public static String formatDuring3(DateUtil begin, DateUtil end) {
        return formatDuring3(Math.abs(begin.toDate().getTime() - (end.toDate().getTime())));
    }

    /**
     * @return 小时：分
     */
    public static String formatDuring3(long mss) {
        List<Long> duration = getTimeList(mss);
        if (duration != null) {
            long hours = duration.get(1) + 24 * duration.get(0);
            String s = "";
            if (hours > 0) {
                s += hours + "小时";
            }
            if (duration.get(2) > 0) {
                s += duration.get(2) + "分";
            }
            if (TextUtils.isEmpty(s)) {
                s = "0分";
            }
            return s;
        } else {
            return "0分";
        }
    }

    /**
     * 间隔天数
     */
    public static int formatDuringForDay(DateUtil begin, DateUtil end) {
        long mss = begin.toDate().getTime() - (end.toDate().getTime());
        if (mss >= 0) {
            return 0;
        }
        mss = Math.abs(mss);
        return (int) (mss / (1000 * 60 * 60 * 24)) + 1;
    }

    /**
     * 是否为闰年
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /**
     * UTC时间转换为本地时间
     *
     * @param utcTime         2014-09-11 07:33
     * @param utcTimePatten   yyyy-MM-dd HH:mm
     * @param localTimePatten yyyy-MM-dd HH:mm
     * @return 2014-09-11 15:33
     */
    public static String utc2Local(String utcTime, String utcTimePatten, String localTimePatten) {
        DateFormat utcFormat = new SimpleDateFormat(utcTimePatten, Locale.getDefault());
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormat.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat localFormat = new SimpleDateFormat(localTimePatten, Locale.getDefault());
        localFormat.setTimeZone(TimeZone.getDefault());
        if (gpsUTCDate == null) {
            return utcTime;
        } else {
            return localFormat.format(gpsUTCDate.getTime());
        }
    }
}
