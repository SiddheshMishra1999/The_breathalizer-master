package com.example.thebreathalizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Authentication.LoginActivity;
import Authentication.SignUpActivity;
import Authentication.UserInfoActivity;
import DatabaseHelper.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    private Button ViewProfileButton;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // changed this from activity_main to activity_splash
        onStart();


    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            }
        }, 3000);
    }


}