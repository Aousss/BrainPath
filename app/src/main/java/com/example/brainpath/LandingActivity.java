package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page); // Set your activity layout file

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
