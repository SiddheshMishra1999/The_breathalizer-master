package Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thebreathalizer.BlowerActivity;
import com.example.thebreathalizer.GuestUserActivity;
import com.example.thebreathalizer.HomeActivity;
import com.example.thebreathalizer.MainActivity;
import com.example.thebreathalizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import DatabaseHelper.ProfileActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout LoginEmailEditText;
    private TextInputLayout LogInPasswordEditText;

    private Button LogInButton;
    private Button ForgotPasswordButton;
    private Button SignUpRedirectButton;
    private Button GuestUserButton;

    private FirebaseAuth mAuth;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();



        LoginEmailEditText = findViewById(R.id.LoginEmailEditText);
        LogInPasswordEditText = findViewById(R.id.LogInPasswordEditText);
        LogInButton = findViewById(R.id.LogInButton);
        ForgotPasswordButton = findViewById(R.id.ForgotPasswordButton);
        SignUpRedirectButton = findViewById(R.id.SignUpRedirectButton);
        GuestUserButton = findViewById(R.id.GuestUserButton);

        // Redirect to Sign up page
        goToSignUp();

        // Redirect to Sign up page
        goToForgotPassword();

        // Redirect to profile page
        LoginUser();

        // Redirect to Guest User
        goToGuest();




    }

        // If the user is already logged in, skip this activity and go to the Main Activity
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                ConnectivityManager connectivityManager = (ConnectivityManager) (LoginActivity.this).getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


                if ((wifiConnection != null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {

                    if (user != null) {
                        if (user.isEmailVerified()) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }

            }

        };


    // listening to changes in the on Start
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void LoginUser(){
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = LoginEmailEditText.getEditText().getText().toString().trim();
                String passwordInput = LogInPasswordEditText.getEditText().getText().toString().trim();

                if(!validEmail() | !validPassword() ){
                    return;
                }
                // Logging in the user
                mAuth.signInWithEmailAndPassword(email, passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

//                            // Email verification
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
                            if(user.isEmailVerified()) {
//                                // Redirecting to User Profile
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                            else{
//                                user.sendEmailVerification();
                                Display("Please check your email to verify your account!");
                            }
                        }
                        else{
                            // Login failed
                            Display("Login failed, please check your credentials or check your Wifi Connection!");
                        }
                    }
                });



            }
        });
    }
    // Validating Email
    private boolean validEmail(){
        String emailInput = LoginEmailEditText.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            LoginEmailEditText.setError("Field Required");

            return false;
        }else if(TextUtils.isEmpty(emailInput) || !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            LoginEmailEditText.setError("Please enter a valid email address");

            return false;
        }
        else{
            LoginEmailEditText.setError(null);
            return true;
        }
    }

    // Valid password check
    private boolean validPassword(){
        String passwordInput = LogInPasswordEditText.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()){
            LogInPasswordEditText.setError("Field Required");
            return false;
        }else if(passwordInput.length() < 6){
            LogInPasswordEditText.setError("Password is too short!");
            return false;

        }
        else{
            LogInPasswordEditText.setError(null);
            return true;
        }
    }


    public void goToSignUp(){
        SignUpRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public  void goToForgotPassword(){
        ForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });
    }

    public void goToGuest(){
        GuestUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, GuestUserActivity.class);
                startActivity(intent);
            }
        });
    }

    // Display any string
    public void Display(String s){
        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
    }
//
//    // removing the listener
    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);

    }





}