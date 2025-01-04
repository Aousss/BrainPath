package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressAndTrackingHome extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prgress_and_tracking);
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
}
