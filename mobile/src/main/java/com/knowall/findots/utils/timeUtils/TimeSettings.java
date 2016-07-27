package com.knowall.findots.utils.timeUtils;

import android.util.Log;

import com.knowall.findots.Constants;
import com.knowall.findots.restcalls.destinations.DestinationData;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by parijathar on 7/25/2016.
 */
public class TimeSettings {

    public static String DateTimeInUTC() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        //Time in GMT
        return dateFormatGmt.format(new Date());
    }


    public static String dateTimeInUTC(String strDate) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(strDate);
            SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
            String sDateInUTC = sdfUTC.format(date);
            return sDateInUTC;
        } catch (Exception e) {
        }
        return "";
    }

    public static String dateTimeInGMT() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        //Time in GMT
        return dateFormatGmt.format(new Date());
    }

    public static String dateTimeInUTCToLocal(String strDate) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = formatter.parse(strDate);
            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfLocal.setTimeZone(TimeZone.getDefault());
            String sDateInLocal = sdfLocal.format(date);
            return sDateInLocal;
        } catch (Exception e) {
        }
        return "";
    }

    public static String dateTimeUTC_toLocale(String utcTime) {
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsed = sourceFormat.parse(utcTime); // => Date is in UTC now

            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            destFormat.setTimeZone(tz);

            return destFormat.format(parsed);
        } catch (Exception e) {

        }
        return "";
    }


    public static String getTimeOnly(String date) {
        String checkedOutTime = "";

        try {
            if (date.length() != 0) {

                SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdfLocal.setTimeZone(TimeZone.getDefault());
                Date mDate = sdfLocal.parse(date);

                SimpleDateFormat sdf12HourFormat = new SimpleDateFormat("hh:mm aa");
                checkedOutTime = sdf12HourFormat.format(mDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkedOutTime;
    }


    public static long getTimeDifference(String date) {
        long difference = 0;
        try {
            Date currentDate = new Date();

            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfLocal.setTimeZone(TimeZone.getDefault());
            Date anotherTime = sdfLocal.parse(date);

            difference = anotherTime.getTime() - currentDate.getTime();

            Log.i("timings", "----------------------------------------------");
            Log.i("timings", "currentTime: "+currentDate.getTime()+" // "+currentDate);
            Log.i("timings", "anotherTime: "+anotherTime.getTime()+" // "+anotherTime);
            Log.i("timings", "getTimeDifference: "+difference);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return difference;
    }
}
