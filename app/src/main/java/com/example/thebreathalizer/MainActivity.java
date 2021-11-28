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
//       ViewProfileButton = findViewById(R.id.ViewProfileButton);
        onStart();
//        goToProfile();


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

//    public void goToProfile(){
//        ViewProfileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }





//    // Skip main activity and go to Login Activity if the user is not logged in
//
//    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
//        @Override
//        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//            if (firebaseUser == null) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        }
//    };
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseAuth.addAuthStateListener(authStateListener);
//
//
//    }
//
//    // removing the listener
//    @Override
//    protected void onStop() {
//        super.onStop();
//        firebaseAuth.removeAuthStateListener(authStateListener);
//    }
}