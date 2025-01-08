package com.example.brainpath.ui.progress;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReminderFragment extends Fragment {

    private Switch notificationSwitch;
    private NotificationManager notificationManager;
    private RecyclerView reminderRecyclerView;

    private FirebaseFirestore db;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> events;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.activity_progress_reminder, container, false);

        // Initialize UI components
        notificationSwitch = rootView.findViewById(R.id.notificationOn);
        notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        reminderRecyclerView = rootView.findViewById(R.id.reminder_recycler_view);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        events = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(events);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reminderRecyclerView.setAdapter(reminderAdapter);

        // Handle switch state change
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });

        loadUpcomingEvents();

        return rootView;
    }

    private void enableNotifications() {
        Notification notification = new NotificationCompat.Builder(requireContext(), "channel_id")
                .setContentTitle("Reminder")
                .setContentText("You have enabled notifications.")
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        notificationManager.notify(1, notification);

        Toast.makeText(requireContext(), "Notifications Enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableNotifications() {
        notificationManager.cancel(1);

        Toast.makeText(requireContext(), "Notifications Disabled", Toast.LENGTH_SHORT).show();
    }

    private void loadUpcomingEvents() {
        db.collection("reminder")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            events.clear();
                            querySnapshot.forEach(document -> {
                                String title = document.getString("title");
                                String date = document.getString("date");
                                events.add(new Reminder(title, date));
                            });
                            reminderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error loading reminders", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
