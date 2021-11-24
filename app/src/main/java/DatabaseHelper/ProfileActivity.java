package DatabaseHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thebreathalizer.HomeActivity;
import com.example.thebreathalizer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Authentication.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button LogOutButton;
    private Button homeButton;

    private FirebaseUser mUser;
    private DatabaseReference databaseReference;

    private String userID;


    private TextView UsernameTextView;
    private TextView EmailTextView;
    private TextView NameTextView;
    private TextView ageTextView;
    private TextView ProvinceTextView;
    private TextView LicenseTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UsernameTextView = findViewById(R.id.UsernameTextView);
        EmailTextView = findViewById(R.id.EmailTextView);
        NameTextView = findViewById(R.id.NameTextView);
        ageTextView = findViewById(R.id.ageTextView);
        ProvinceTextView = findViewById(R.id.ProvinceTextView);
        LicenseTextView = findViewById(R.id.LicenseTextView);


        DisplayInfo();

        homeButton = findViewById(R.id.homeButton);
        goHome();

        LogOutButton = findViewById(R.id.LogOutButton);
        LogOut();
    }

    // redirect to Main activity
    private void goHome() {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }


    public void LogOut(){
        LogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                // Redirecting to the login activity

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }

    // Get user data

    public void DisplayInfo(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userID = mUser.getUid();

        //testing
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);


                if(userProfile!= null){
                    String email = userProfile.email;
                    String username = userProfile.username;
                    String First_name = userProfile.first_name;
                    String Last_Name = userProfile.last_name;
                    String Name = First_name + " " + Last_Name;
                    String Age = userProfile.age;
                    String Province = userProfile.province;
                    String License = userProfile.license;

                    UsernameTextView.setText("Username: " + username);
                    EmailTextView.setText("Email: " + email);
                    NameTextView.setText("Name: "+ Name);
                    ageTextView.setText("Age: "+ Age);
                    ProvinceTextView.setText("Province: " + Province);
                    LicenseTextView.setText("License: " + License );
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
        Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_LONG).show();
    }

}