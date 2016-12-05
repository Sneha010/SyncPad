package com.nearby.syncpad.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final String TIME_FORMAT = "hh:mm aa";


    public static String getCurrentDate() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

        Calendar calendar = Calendar.getInstance();

        return simpleDateFormat.format(calendar.getTime());

    }

    public static String getCurrentTime() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);

        Calendar calendar = Calendar.getInstance();

        return simpleDateFormat.format(calendar.getTime());


    }

    public static String getDateUsingData(int day, int month, int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.YEAR, year);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

        return simpleDateFormat.format(calendar.getTime());

    }


    public static String getTimeUsingData(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);

        return simpleDateFormat.format(calendar.getTime());

    }


    public static long getTimeInMillis(String date, String time) {

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT + " " + TIME_FORMAT);
        try {
            Date dateNew = format.parse(date + " " + time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateNew);
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static String getFormattedDate(String date) {

        Date actualDateFormat;

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            actualDateFormat = format.parse(date);
            return new SimpleDateFormat("dd MMMM, yyyy").format(actualDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }


}
