package com.example.brainpath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainpath.ui.assessment.AssessmentFragment;
import com.example.brainpath.ui.profile.SignInActivity;

public class LandingActivity extends AppCompatActivity {

    private Handler handler; // Handler for delayed navigation
    private Runnable navigateRunnable; // Runnable to navigate after delay
    private boolean isNavigated = false; // Flag to check if user has already navigated

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page); // Set your activity layout file

        // REMOVE THE TOP ACTION BAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // ADDED - IF USER ALREADY LOGIN BEFORE, IT WILL DIRECTLY INTO THE HOME PAGE

        // Checking the login status
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // This will directly to the home page
            Intent assessmentIntent = new Intent(this, AssessmentFragment.class);
            startActivity(assessmentIntent);
            finish(); // close the LandingActivity
            return;
        }

        // Initialize the Handler and Runnable for delayed navigation
        handler = new Handler();
        navigateRunnable = () -> {
            if (!isNavigated) { // Ensure the user hasn't navigated manually
                navigateToNextPage();
            }
        };

        // Schedule navigation after 5 seconds
        handler.postDelayed(navigateRunnable, 5000);

        // Set a click listener on the layout to cancel the timer and navigate
        View landingStart = findViewById(R.id.landingLayout);
        landingStart.setOnClickListener(v -> {
            isNavigated = true; // Mark as navigated to prevent the timeout
            handler.removeCallbacks(navigateRunnable); // Cancel the delayed navigation
            navigateToSignIn();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to avoid memory leaks
        if (handler != null) {
            handler.removeCallbacks(navigateRunnable);
        }
    }

    private void navigateToNextPage() {
        Intent intent = new Intent(LandingActivity.this, SignInActivity.class);
        startActivity(intent);
        finish(); // Close the LandingActivity
    }

    private void navigateToHome() {
        Intent assessmentIntent = new Intent(this, AssessmentFragment.class);
        startActivity(assessmentIntent);
        finish(); // Close the LandingActivity
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(LandingActivity.this, SignInActivity.class);
        startActivity(intent);
        finish(); // Close the LandingActivity
    }
}