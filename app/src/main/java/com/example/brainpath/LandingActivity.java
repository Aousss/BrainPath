package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainpath.ui.profile.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page); // Set your activity layout file

        // Hide the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, redirect to MainActivity
            Intent intent = new Intent(LandingActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close LandingActivity
        } else {
            // Find the button by its ID
            View landingStart = findViewById(R.id.landingLayout);

            // Set a click listener on the button
            landingStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start LoginActivity when the button is clicked
                    Intent intent = new Intent(LandingActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
