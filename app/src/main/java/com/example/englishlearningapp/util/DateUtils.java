package com.example.englishlearningapp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getRelativeDate(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long diff = timestamp - currentTime;
        long days = diff / (1000 * 60 * 60 * 24);

        if (days == 0) {
            return "Hôm nay";
        } else if (days == 1) {
            return "Ngày mai";
        } else if (days == -1) {
            return "Hôm qua";
        } else if (days > 0 && days <= 7) {
            return days + " ngày tới";
        } else if (days < 0 && days >= -7) {
            return Math.abs(days) + " ngày trước";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    public static String formatDate(long timestamp, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static long getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}