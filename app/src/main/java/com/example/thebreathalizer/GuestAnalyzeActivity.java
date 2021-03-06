package com.example.thebreathalizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import Authentication.LoginActivity;
import Bluetooth_Connection.ConnectionActivity;
import Contact.ContactListActivity;

@SuppressWarnings("deprecation")
public class GuestAnalyzeActivity extends AppCompatActivity {

    private Button TesAgainGuestButton;
    private Button goToLoginGuestButton;
    private Button     ContactButton;
    private TextView ValueGuestTextView;
    private TextView SuggestionTextView;
    private  TextView LevelPermittedTextView;
    private Button ExitButton;

    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_analyze);

        TesAgainGuestButton = findViewById(R.id.TesAgainButton);
        ValueGuestTextView = findViewById(R.id.ValueGuestTextView);
        goToLoginGuestButton = findViewById(R.id.goToLoginGuestButton);
        SuggestionTextView = findViewById(R.id.SuggestionTextView);
        LevelPermittedTextView = findViewById(R.id.LevelPermittedTextView);
        ContactButton = findViewById(R.id.ContactButton);
        ExitButton = findViewById(R.id.ExitButton);

        // Redirect to Login activity
        goToLogin();

        // Redirect to contact list activity

        goToContact();

        // Redirect to search device activity

        testAgain();

        // Compare default vs new value and give output

        checkInfo();

        // Close app

        exitApp();


    }

    // Close app method
    private void exitApp() {
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestAnalyzeActivity.this, ConnectionActivity.class);
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

    // Redirect to contact list activity

    private void goToContact() {
        ContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestAnalyzeActivity.this, ContactListActivity.class);
                startActivity(intent);
            }
        });

    }

    // Redirect to search device activity

    private void testAgain() {
        TesAgainGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestAnalyzeActivity.this, ConnectionActivity.class);
                startActivity(intent);
            }
        });
    }

    // Compare default vs new value and give output

    private void checkInfo() {
        Bundle bundle = getIntent().getExtras();
        String Value = bundle.getString("Value");

        ValueGuestTextView.setText("Your Alcohol level is " + Value);
        double value = Double.parseDouble(Value);

        LevelPermittedTextView.setText("You can have a Blood Alcohol level of 400 maximum");



        if(value <= 300){
            SuggestionTextView.setText("You are not over the limit, Drive Safely!");
            goToLoginGuestButton.setVisibility(View.VISIBLE);
            ExitButton.setVisibility(View.VISIBLE);

        }else if(value> 300 && value <= 400 ){
            SuggestionTextView.setText("You are close to the limit, Driving is not recommended!");
            ContactButton.setVisibility(View.VISIBLE);
        }else if(value> 400 && value <= 500 ) {
            SuggestionTextView.setText("You are over the limit,Driving is not recommended!");
            ContactButton.setVisibility(View.VISIBLE);
            shakeItBaby();


        }else if(value> 500 && value <= 600 ) {
            SuggestionTextView.setText("You are way over the limit, Do not Drive, Choose a different method!");
            ContactButton.setVisibility(View.VISIBLE);

            shakeItBaby();
        }else if(value> 600 && value <= 700 ) {
            SuggestionTextView.setText("You are drunk, Do not Drive, Choose a different method!");
            ContactButton.setVisibility(View.VISIBLE);

            shakeItBaby();

        }else if(value >  700 ) {
            SuggestionTextView.setText("You are VERY DRUNK. DO NOT DRIVE! Choose one of the following methods!");
            ContactButton.setVisibility(View.VISIBLE);
            shakeItBaby();

        }
    }

    // Vibrator function
    private void shakeItBaby() {
        vibrator =  (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(10000);

    }


    // Redirect to Login activity

    private void goToLogin() {
        goToLoginGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(GuestAnalyzeActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });


    }


}