package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.brainpath.R;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressAndTrackingHome extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView motivationQuote,progressPercentageDisplay, progressBarMessage;
    private FirebaseFirestore db;
    private DocumentReference progressRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_and_tracking);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        progressRef = db.collection("course").document("subject");

        // Bind views
        motivationQuote = findViewById(R.id.motivationQuote);
        progressBar = findViewById(R.id.progressBar);
        progressPercentageDisplay = findViewById(R.id.prgressPercentageDisplay);
        progressBarMessage = findViewById(R.id.progressBarMessage);

        // Fetch progress data
        fetchProgressData();
    }

    public void navigateToLearningGoals(View view) {
        Intent intent = new Intent(ProgressAndTrackingHome.this, LearningGoals.class);
        startActivity(intent);
    }
    public void navigateToProgressCoursesLists(View view) {
        Intent intent = new Intent(ProgressAndTrackingHome.this, ProgressCoursesLists.class);
        startActivity(intent);
    }
    public void navigateToProgressReminder(View view) {
        Intent intent = new Intent(ProgressAndTrackingHome.this, ProgressReminder.class);
        startActivity(intent);
    }

    private void fetchProgressData() {
        progressRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("ProgressAndTrackingHome", "Error fetching progress", e);
                Toast.makeText(this, "Error fetching progress", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Get progress percentage
                Long progress = snapshot.getLong("completion");
                if (progress != null) {
                    updateProgressUI(progress.intValue());
                } else {
                    Log.e("ProgressAndTrackingHome", "Progress percentage is missing.");
                }

                // Get user name
                String userName = snapshot.getString("name");
                if (userName != null) {
                    updateMotivationQuote(userName);
                } else {
                    Log.e("ProgressAndTrackingHome", "User name is missing.");
                }
            } else {
                Log.e("ProgressAndTrackingHome", "No progress data found.");
            }
        });
    }

    private void updateProgressUI(int progress) {
        progressBar.setProgress(progress);
        progressPercentageDisplay.setText(progress + "%");

        if (progress < 50) {
            progressBarMessage.setText("Keep going, you're doing great!");
        } else if (progress < 80) {
            progressBarMessage.setText("You're more than halfway there!");
        } else if (progress < 100) {
            progressBarMessage.setText("Almost there, keep pushing!");
        } else {
            progressBarMessage.setText("Congratulations! You've achieved your goal!");
        }
    }
    private void updateMotivationQuote(String userName) {
        motivationQuote.setText("Keep it up, " + userName + "!");
    }
}
