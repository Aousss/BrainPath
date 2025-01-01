package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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

    //    HANDLE THE PAGE FOR FORGOT PASSWORD 3
    private void forgotPassword_2() {
        Button gotoForgotPassword_3 = findViewById(R.id.btnContinue_2);
        gotoForgotPassword_3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setContentView(R.layout.forgot_password_3);
                forgotPassword_3();
            }
        });
    }

    //    HANDLE THE PAGE FOR FORGOT PASSWORD 3
    private void forgotPassword_3() {
        Button gotoLogin = findViewById(R.id.btnSubmit);
        gotoLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
