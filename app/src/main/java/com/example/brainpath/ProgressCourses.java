package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ProgressCourses extends AppCompatActivity {
    private TextView courseTitle;
    private TextView courseProgressText;
    private ProgressBar courseProgressBar;
    private ProgressBar quizProgressBar;
    private TextView percentageCorrectText;
    private TextView percentageWrongText;
    private TextView improvement1Text;
    private ProgressBar improvementProgress1;
    private TextView improvement1Percentage;
    // Add other improvements as needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_courses); // Ensure this matches your XML layout file

        // Initialize views
        courseTitle = findViewById(R.id.courseTitle);
        courseProgressText = findViewById(R.id.prgressPercentageDisplay);
        courseProgressBar = findViewById(R.id.progressBar);

        // Quiz performance progress
        quizProgressBar = findViewById(R.id.percentageCorrectWrongBar);
        percentageCorrectText = findViewById(R.id.percentageCorrectText);
        percentageWrongText = findViewById(R.id.percentageWrongText);

        // Improvement progress bars
        improvement1Text = findViewById(R.id.progressPercentageImprovement1);
        improvementProgress1 = findViewById(R.id.progressBarImprovement1);
        improvement1Percentage = findViewById(R.id.progressPercentageImprovement1);

        // Retrieve the passed data from the Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("courseName");
        int progress = intent.getIntExtra("courseProgress", 0); // Default to 0 if not passed

        // Set course name and progress
        courseTitle.setText(name);
        courseProgressText.setText(progress + "%");
        courseProgressBar.setProgress(progress);

        // Dummy data for quiz performance
        int correctProgress = 65; // Dummy value
        int wrongProgress = 35; // Dummy value
        quizProgressBar.setProgress(correctProgress);
        percentageCorrectText.setText(correctProgress + "%");
        percentageWrongText.setText(wrongProgress + "%");

        // Dummy data for improvement topics
        // Example of the first improvement topic
        improvement1Text.setText("1. Differentiation");
        improvementProgress1.setProgress(10); // Dummy value
        improvement1Percentage.setText("10%");

        // You can repeat this for other improvement topics with dummy data
    }
}
