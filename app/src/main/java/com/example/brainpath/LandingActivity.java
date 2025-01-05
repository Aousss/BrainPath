package com.example.brainpath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainpath.ui.home.HomeFragment;
import com.example.brainpath.ui.profile.SignInActivity;

public class LandingActivity extends AppCompatActivity {

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
            Intent assessmentIntent = new Intent(this, HomeFragment.class);
            startActivity(assessmentIntent);
            finish(); // close the LandingActivity
            return;
        }



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