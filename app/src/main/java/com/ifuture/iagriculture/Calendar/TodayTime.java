package com.ifuture.iagriculture.Calendar;

import android.text.format.Time;

import java.util.Calendar;

/**
 * Created by feather on 2016/4/2.
 */
public class TodayTime {
    int year;  //2016准确
    int month; //0~11
    int day;  //1开始
    int hour; // 0-23
    int minute;
    int second;
    int week; //1开始
    public void update()
    {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        week = c.get(Calendar.DAY_OF_WEEK);
    }
    public int getYear()
    {
        return year;
    }
    public int getMonth()
    {
        return month;
    }
    public int getDay()
    {
        return day;
    }
    public int getHour()
    {
        return hour;
    }
    public int getMinute()
    {
        return minute;
    }
    public int getSecond()
    {
        return second;
    }
    public int getWeek()
    {
        return week;
    }
}
