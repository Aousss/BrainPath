package com.example.brainpath.ui.interaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    public static String formatTimestamp(Date timestamp) {
        // Get the current time in Malaysia time zone
        Date currentDate = new Date();

        // Set up Malaysia time zone
        TimeZone malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur");

        // Check if the timestamp is from today
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        dateFormat.setTimeZone(malaysiaTimeZone); // Set time zone
        String currentDay = dateFormat.format(currentDate);
        String messageDay = dateFormat.format(timestamp);

        if (currentDay.equals(messageDay)) {
            // If the message is from today, show the time only with "Today"
            SimpleDateFormat timeFormat = new SimpleDateFormat("'Today, 'HH:mm", Locale.getDefault());
            timeFormat.setTimeZone(malaysiaTimeZone); // Set time zone
            return timeFormat.format(timestamp);
        } else {
            // If the message is older than today, show the date, month, and time
            SimpleDateFormat dateAndMonthFormat = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
            dateAndMonthFormat.setTimeZone(malaysiaTimeZone); // Set time zone
            return dateAndMonthFormat.format(timestamp);
        }
    }
}
