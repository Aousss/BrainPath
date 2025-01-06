package com.example.brainpath.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainpath.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_1); // Initial layout
        firebaseAuth = FirebaseAuth.getInstance();

        forgotPassword_1();
    }

    private void forgotPassword_1() {
        Button gotoForgotPassword_2 = findViewById(R.id.btnContinue_1);
        EditText emailField = findViewById(R.id.usernameField);

        gotoForgotPassword_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = emailField.getText().toString().trim();
                if (userEmail.isEmpty()) {
                    emailField.setError("Please enter your email");
                    return;
                }

                // Send reset password email using Firebase
                firebaseAuth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this,
                                        "Password reset link sent to " + userEmail,
                                        Toast.LENGTH_LONG).show();
                                setContentView(R.layout.forgot_password_2);
                                forgotPassword_2(userEmail); // Pass email to the next screen
                            } else {
                                Toast.makeText(ForgotPassword.this,
                                        "Failed to send email: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void forgotPassword_2(String email) {
        TextView emailView = findViewById(R.id.emailForgotPassword);
        emailView.setText("A password reset link has been sent to: " + email);

        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}