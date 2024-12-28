package com.example.brainpath.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brainpath.R;

public class ForgotPassword extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_1); //CALLED THE DEFAULT LAYOUT

        forgotPassword_1();
    }

    //    HANDLE THE PAGE FOR FORGOT PASSWORD 1
    private void forgotPassword_1() {
        Button gotoForgotPassword_2 = findViewById(R.id.btnContinue_1);
        gotoForgotPassword_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setContentView(R.layout.forgot_password_2);
                forgotPassword_2();
            }
        });
    }

    //    HANDLE THE PAGE FOR FORGOT PASSWORD 1
    private void forgotPassword_2() {
        Button gotoForgotPassword_3 = findViewById(R.id.btnContinue_2);
        gotoForgotPassword_3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setContentView(R.id.);
            }
        });
    }
}
