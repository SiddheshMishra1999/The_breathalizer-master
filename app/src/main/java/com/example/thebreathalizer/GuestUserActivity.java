package com.example.thebreathalizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import Authentication.LoginActivity;
import Authentication.SignUpActivity;
import Bluetooth_Connection.ConnectionActivity;

public class GuestUserActivity extends AppCompatActivity {

    private Button SignUpGuestButton;
    private Button LoginGuestButton;
    private ImageButton BlowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_user);

        SignUpGuestButton = findViewById(R.id.SignUpGuestButton);
        LoginGuestButton = findViewById(R.id.LoginGuestButton);

        BlowButton = findViewById(R.id.BlowButton);


        // redirect to Login
        goToLogin();


        // redirect to sign up
        goToSignup();


        // redirect to blower activity
        goToBlower();
    }

    private void goToBlower() {
        BlowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestUserActivity.this, ConnectionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goToSignup() {
        SignUpGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestUserActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goToLogin() {

        LoginGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestUserActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }
}