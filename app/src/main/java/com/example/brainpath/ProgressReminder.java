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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProgressReminder extends AppCompatActivity {
    private Switch notificationSwitch;
    private NotificationManager notificationManager;
    private LinearLayout reminderListContainer;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_reminder);

        notificationSwitch = findViewById(R.id.notificationOn);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        reminderListContainer = findViewById(R.id.reminderListContainer);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
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

    // This method loads the upcoming events from Firestore
    private void loadUpcomingEvents() {
        // Fetch reminders from Firestore (you can modify the path to suit your structure)
        db.collection("reminder")  // Your course collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // List to hold all events
                            List<Event> events = new ArrayList<>();

                            // Loop through the Firestore results and create event objects
                            querySnapshot.forEach(document -> {
                                String title = document.getString("title");
                                String date = document.getString("date");

                                // Add event to the list
                                events.add(new Event(title, date));
                            });

                            // Inflate the events into the layout
                            inflateEvents(events);
                        }
                    } else {
                        // Handle failure
                        Toast.makeText(this, "Error loading reminders", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Method to dynamically create views for the events
    private void inflateEvents(List<Event> events) {
        // Inflate event cards for each event
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




