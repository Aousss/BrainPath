package com.example.brainpath;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProgressReminder extends AppCompatActivity {
    private Switch notificationSwitch;
    private NotificationManager notificationManager;
    private LinearLayout reminderListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_reminder);

        notificationSwitch = findViewById(R.id.notificationOn);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        reminderListContainer = findViewById(R.id.reminderListContainer);
        // Handle switch state change
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });

        loadUpcomingEvents();
    }

    private void enableNotifications() {
        // Example: Creating a simple notification
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Reminder")
                .setContentText("You have enabled notifications.")
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        notificationManager.notify(1, notification);

        Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableNotifications() {
        // Cancel any ongoing notifications
        notificationManager.cancel(1);

        Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
    }

    // This method loads dummy upcoming events (and inflates them into the layout)
    private void loadUpcomingEvents() {
        // Dummy data for upcoming events with dates
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("Meeting with Math Instructor", "20 Jan 2025"));
        events.add(new Event("Study Group for Physics", "25 Jan 2025"));
        events.add(new Event("Chemistry Lab Session", "30 Jan 2025"));

        // Loop through the list and inflate them into the layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (Event event : events) {
            // Inflate the entire CardView layout
            LinearLayout eventCard = (LinearLayout) inflater.inflate(R.layout.progress_reminder_lists, null);

            // Find the TextView inside the inflated CardView
            TextView reminderTitle = eventCard.findViewById(R.id.reminderTitle);
            TextView dueDate = eventCard.findViewById(R.id.dueDate);

            // Set the event text for the reminder title
            reminderTitle.setText(event.getTitle());

            // Set the date dynamically
            dueDate.setText(event.getDate());

            // Add the inflated CardView to the container
            reminderListContainer.addView(eventCard);
        }
    }
}

class Event {
    private String title;
    private String date;

    public Event(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}




