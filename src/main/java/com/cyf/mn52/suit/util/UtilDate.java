package com.cyf.mn52.suit.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UtilDate {

    public static String dateToYYYYMMDDHHMMSS(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String dateToYYYYMMDD(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String dateToHHMMSS(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static Date stringToDate(String str) {
        try {
            SimpleDateFormat formatter = null;
            if (str.contains("-") && str.contains(":")) {
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            } else {
                if (str.contains("-")) {
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                }
                if (str.contains(":")) {
                    formatter = new SimpleDateFormat("HH:mm:ss");
                }
            }
            Date date = formatter.parse(str);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date addYear(Date date, int year) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        date = calendar.getTime();
        return date;
    }

    public static Date addMonth(Date date, int month) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        date = calendar.getTime();
        return date;
    }

    public static Date addDay(Date date, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        date = calendar.getTime();
        return date;
    }

    public static int getYear(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

    public static int getDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        return day;
    }

    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minute = calendar.get(Calendar.MINUTE);
        return minute;
    }

    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int second = calendar.get(Calendar.SECOND);
        return second;
    }

    public static Date getDayStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();
        return date;
    }

    public static Date getDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        date = calendar.getTime();
        return date;
    }

    public static String secondToHHMMSS(int seconds) {
        int temp = 0;
        String str = "";
        temp = seconds / 3600;
        str += (temp < 10) ? "0" + temp + ":" : "" + temp + ":";
        temp = seconds % 3600 / 60;
        str += (temp < 10) ? "0" + temp + ":" : "" + temp + ":";
        temp = seconds % 3600 % 60;
        str += (temp < 10) ? "0" + temp : "" + temp;
        return str;
    }

}
