package com.example.thebreathalizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Bluetooth_Connection.ConnectionActivity;
import Contact.ContactListActivity;
import DatabaseHelper.Sensor;
import DatabaseHelper.User;

public class AnalyzeActivity extends AppCompatActivity {
    private TextView ValueTextView;
    private Button TesAgainButton;
    private Button ContactButton;
    private TextView UserNameTextView;
    private TextView LevelPermittedTextView;
    private TextView SuggestionTextView;
    private Button backToHomeButton;
    private Button ExitButton;

    private FirebaseUser mUser;
    private DatabaseReference databaseReference, databaseReference2;
    private String userID;
    private FirebaseAuth mAuth;

    // Vibrator function
    Vibrator vibrator;

    public int counter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        TesAgainButton = findViewById(R.id.TesAgainButton);
        ContactButton = findViewById(R.id.ContactButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        ExitButton = findViewById(R.id.ExitButton);

        ValueTextView = findViewById(R.id.ValueTextView);
        UserNameTextView = findViewById(R.id.UserNameTextView);
        LevelPermittedTextView = findViewById(R.id.LevelPermittedTextView);
        SuggestionTextView = findViewById(R.id.SuggestionTextView);

        mAuth = FirebaseAuth.getInstance();

        counter ++;






        // Redirects to search device activity
        goToTestAgain();

        // Redirects to home activity

        goToHome();


        // Fetches data from database

        GetInfo();

        // Redirects to contact list activity

        goToContact();

        // Closes app
        exitApp();




    }
    // Closes app

    private void exitApp() {
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnalyzeActivity.this, ConnectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

    // Redirects to home activity

    private void goToHome() {
        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyzeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Redirects to contact list activity

    private void goToContact() {
        ContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyzeActivity.this, ContactListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Fetches data from database
    public void GetInfo(){
        Bundle bundle = getIntent().getExtras();
        String Value = bundle.getString("Value").trim();
        ValueTextView.setText("Your Alcohol level is " + Value);

        DateFormat format = new SimpleDateFormat("YYYY:MM:dd_hh:mm:ss a");
        String date = format.format(new Date());

        long d = System.currentTimeMillis();
        String date2 = format.format(new Date(d));



        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child(userID);




        double value = Double.parseDouble(Value);


            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {


            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {

                    HashMap<String , String  > valuemap = new HashMap<>();
                    valuemap.put("time", date2);
                    valuemap.put("value", Value);

                    databaseReference2.push().setValue(valuemap);


                    // Add data in the firebase database
                    Map<String, Object> map = new HashMap<>();
                    map.put(date2, Value);
                    databaseReference.child(userID).child("Data").updateChildren(map);

                        String First_name = userProfile.first_name;
                        String Last_Name = userProfile.last_name;
                        String Name = First_name + " " + Last_Name;
                        UserNameTextView.setText(Name + "'s Alcohol Level Analysis");
                        String ProvinceSelected = userProfile.province;

                        // Checks the license type and gives output accordingly
                        String License_type = userProfile.license;
                        if (License_type.equals("Probationary")) {
                            LevelPermittedTextView.setText("0, You cannot drink and dr ive");
                            SuggestionTextView.setText("YOU MUST NOT DRINK AND DRIVE!");
                            backToHomeButton.setVisibility(View.VISIBLE);
                        } else {
                            LevelPermittedTextView.setText("You can have a Blood Alcohol level of 400 maximum");

                            if (value <= 300) {
                                SuggestionTextView.setText("You are not over the limit, Drive Safely!");
                                backToHomeButton.setVisibility(View.VISIBLE);
                                ExitButton.setVisibility(View.VISIBLE);


                            } else if (value > 300 && value <= 400) {
                                SuggestionTextView.setText("You are close to the limit, Driving is not recommended!");
                                ContactButton.setVisibility(View.VISIBLE);

                            } else if (value > 400 && value <= 500) {
                                SuggestionTextView.setText("You are over the limit,Driving is not recommended!");
                                ContactButton.setVisibility(View.VISIBLE);
                                shakeItBaby();


                            } else if (value > 500 && value <= 600) {
                                SuggestionTextView.setText("You are way over the limit, Do not Drive, Choose a different method!");
                                ContactButton.setVisibility(View.VISIBLE);
                                shakeItBaby();

                            } else if (value > 600 && value <= 700) {
                                SuggestionTextView.setText("You are drunk, Do not Drive, Choose a different method!");
                                ContactButton.setVisibility(View.VISIBLE);
                                shakeItBaby();

                            } else if (value > 700) {
                                SuggestionTextView.setText("You are VERY DRUNK. DO NOT DRIVE! Choose one of the following methods!");
                                ContactButton.setVisibility(View.VISIBLE);
                                shakeItBaby();

                            }
                        }
                    }



            }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

    // Vibration of the app for 10 secs
    private void shakeItBaby() {
        vibrator =  (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(10000);

    }

    // redirects to Search device activity
    private void goToTestAgain() {

        TesAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyzeActivity.this, ConnectionActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    // Display any string
    public void Display(String s){
        Toast.makeText(AnalyzeActivity.this, s, Toast.LENGTH_LONG).show();
    }


}

