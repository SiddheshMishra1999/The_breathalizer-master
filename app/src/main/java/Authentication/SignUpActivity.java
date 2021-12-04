package Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thebreathalizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    // initializing stuff
    private TextInputLayout SignUpEmailEditText;
    private TextInputLayout SignUpUsernameEditText;
    private TextInputLayout SignUpPasswordEditText;
    private TextInputLayout SignUpConfirmPasswordEditText;
    private ProgressBar SignUpProgressBar;

    private Button ContinueButton;

//    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        mAuth = FirebaseAuth.getInstance();

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SignUpEmailEditText = findViewById(R.id.SignUpEmailEditText);
        SignUpUsernameEditText = findViewById(R.id.SignUpUsernameEditText);
        SignUpPasswordEditText = findViewById(R.id.SignUpPasswordEditText);
        SignUpConfirmPasswordEditText = findViewById(R.id.SignUpConfirmPasswordEditText);
        ContinueButton = findViewById(R.id.ContinueButton);

        SignUpProgressBar = findViewById(R.id.SignUpProgressBar);
        continue_Button();





    }

    // Validating Email
    private boolean validEmail(){
        String emailInput = SignUpEmailEditText.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            SignUpEmailEditText.setError("Field Required");

            return false;
        }else if(TextUtils.isEmpty(emailInput) || !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            SignUpEmailEditText.setError("Please enter a valid email address");

            return false;
        }
        else{
            SignUpEmailEditText.setError(null);
            return true;
        }
    }
    // Validating Username
    private boolean validUsername(){
        String usernameInput = SignUpUsernameEditText.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()){
            SignUpUsernameEditText.setError("Field Required");

            return false;
        }else if(usernameInput.length()>20){
            SignUpUsernameEditText.setError("Username too long");
            return false;

        }
        else{
            SignUpUsernameEditText.setError(null);
            return true;
        }
    }
    // Valid password check
    private boolean validPassword(){
        String passwordInput = SignUpPasswordEditText.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()){
            SignUpPasswordEditText.setError("Field Required");
            return false;
        }else if(passwordInput.length() < 6){
            SignUpPasswordEditText.setError("Password is too short!");
            return false;

        }
        else{
            SignUpPasswordEditText.setError(null);
            return true;
        }
    }

    // Verifying if the password is the same as entered previously
    private boolean validConfirmPassword(){
        String passwordInput = SignUpPasswordEditText.getEditText().getText().toString().trim();
        String confirmPasswordInput = SignUpConfirmPasswordEditText.getEditText().getText().toString().trim();

        if (confirmPasswordInput.isEmpty()){
            SignUpConfirmPasswordEditText.setError("Field Required");
            return false;
        }else if(!passwordInput.equals(confirmPasswordInput)){
            SignUpConfirmPasswordEditText.setError("Password does not match");
            return false;

        }else if(passwordInput.equals(confirmPasswordInput)){
            SignUpConfirmPasswordEditText.setError(null);
            return true;
        }else{
            SignUpConfirmPasswordEditText.setError(null);
            return true;
        }
    }

    // Redirect to the next step of the signup
    public void continue_Button(){

        ContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validEmail() | !validUsername() | !validPassword() | !validConfirmPassword()){
                    return;
                }
                String emailInput = SignUpEmailEditText.getEditText().getText().toString().trim();
                String usernameInput = SignUpUsernameEditText.getEditText().getText().toString().trim();
                String passwordInput = SignUpPasswordEditText.getEditText().getText().toString().trim();


                // Send the info to a different activity
                Intent intent = new Intent(SignUpActivity.this, UserInfoActivity.class);
                intent.putExtra("email",emailInput);
                intent.putExtra("username",usernameInput);
                intent.putExtra("password",passwordInput);


                startActivity(intent);




            }
        });


    }

    // Display any string
    public void Display(String s){
        Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_LONG).show();
    }


}