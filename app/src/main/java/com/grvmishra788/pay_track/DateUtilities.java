package com.grvmishra788.pay_track;

import java.util.Calendar;
import java.util.Date;

public class DateUtilities {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + DateUtilities.class.getName();

    public static Date getStartDateOfMonthWithDefaultTime(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set day as 1st day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getRandomDateFromFuture() {
        //create date object
        Calendar calendar = Calendar.getInstance();
        //set year part of date from future
        calendar.set(Calendar.YEAR, 25000);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getRandomDateFromPast() {
        //create date object
        Calendar calendar = Calendar.getInstance();
        //set year part of date from past
        calendar.set(Calendar.YEAR, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getTodayDateWithDefaultTime() {
        // set Today as default date
        //create date object
        Calendar calendar = Calendar.getInstance();
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getOneYearBackwardDate(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set time part of date as 0
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
        return calendar.getTime();
    }

    public static Date getOneYearForwardDate(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set time part of date as 0
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
        return calendar.getTime();
    }

    public static Date getDateWithDefaultTime(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getDateWithDefaultTime(int year, int month, int day){
        //set year, month and day of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

}
