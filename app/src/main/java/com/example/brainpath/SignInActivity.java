package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SignInActivity extends AppCompatActivity {

    private  boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button loginBtn = findViewById(R.id.signInButton);
        Button signupBtn = findViewById(R.id.googleSignInButton);

        EditText passwordField = findViewById(R.id.passwordField);
        TextView showPassword = findViewById(R.id.showPassword);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //        SHOW or HIDE PASSWORD
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Password is hidden
                    passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword.setText("Show");
                } else {
                    // Password is show
                    passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordField.setTransformationMethod(null);
                    showPassword.setText("Hide");
                }

                // Move cursor to the end after toggling
                passwordField.setSelection(passwordField.length());

                // Toggle the state
                isPasswordVisible = !isPasswordVisible;
            }
        });

        //        FORGOT PASSWORD
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        //        REGISTER NEW ACCOUNT
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //        SET THE GRADIENT BUTTON
        loginBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_gradient));
    }
}
