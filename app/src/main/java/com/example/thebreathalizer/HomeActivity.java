package com.example.thebreathalizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Authentication.LoginActivity;
import Authentication.SensorDataDisplayActivity;
import Authentication.SignUpActivity;
import Bluetooth_Connection.ConnectionActivity;
import DatabaseHelper.ProfileActivity;
import DatabaseHelper.UpdateInfoActivity;
import DatabaseHelper.User;

public class HomeActivity extends AppCompatActivity {

    private Button ViewProfileButton;
    private Button UpdateProfileButton;
    private Button ViewDataButton;

    private ImageButton BlowUserButton;


    private FirebaseUser mUser;
    private DatabaseReference databaseReference;

    private String userID;

    private TextView WelcomeTextView;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewProfileButton = findViewById(R.id.ViewProfileButton);
        WelcomeTextView = findViewById(R.id.WelcomeTextView);
        UpdateProfileButton = findViewById(R.id.UpdateProfileButton);
        BlowUserButton = findViewById(R.id.BlowUserButton);
        ViewDataButton = findViewById(R.id.ViewDataButton);


        // redirect to profile Activity
        goToProfile();

        // Welcome the user with their name
        WelcomeUser();

        // redirect user to blower activity
        goToBlowerUser();

        // redirect to Update page
        goToUpdateInfo();

        // redirect to data Activity
        goToDataViewer();
    }

    private void goToDataViewer() {
        ViewDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, SensorDataDisplayActivity.class);
            startActivity(intent);
            }
        });
    }

    private void goToUpdateInfo() {
        UpdateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goToBlowerUser() {
        BlowUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ConnectionActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goToProfile(){
        ViewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    // Fetch name from databse
    public void WelcomeUser(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userID = mUser.getUid();

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        String First_name = userProfile.first_name;
                        String Last_Name = userProfile.last_name;
                        String Name = First_name + " " + Last_Name;

                        WelcomeTextView.setText("Welcome " + Name);
                    }

                }catch (Exception e){
                    Display("Failed to get value" + e);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Display("Something didn't go right!");

            }

        });
    }

    // Display any string
    public void Display(String s){
        Toast.makeText(HomeActivity.this, s, Toast.LENGTH_LONG).show();
    }

    // if a user is not signed in or verified, go to login
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                if(firebaseUser.isEmailVerified()) {

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


}

