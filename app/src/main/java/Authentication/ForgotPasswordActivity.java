package Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thebreathalizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Button GetPasswordButton;
    private TextInputLayout ForgotPasswordEmailEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        // return to main activity
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        // getting current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        ForgotPasswordEmailEditText = findViewById(R.id.ForgotPasswordEmailEditText);
        GetPasswordButton = findViewById(R.id.GetPasswordButton);
        // sends an email to the user's email and brings back to the login
        Get_Password();



    }

    // Validating Input
    private boolean validInput(){
        String Input = ForgotPasswordEmailEditText.getEditText().getText().toString().trim();
        if (Input.isEmpty()){
            ForgotPasswordEmailEditText.setError("Field Required");
            return false;
        }
        else{
            ForgotPasswordEmailEditText.setError(null);
            return true;
        }
    }


    // Send an email with reset password to the user when button is pressed
    public void Get_Password(){
        GetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validInput()) {
                    return;
                }
                String Input = ForgotPasswordEmailEditText.getEditText().getText().toString().trim();

                mAuth.sendPasswordResetEmail(Input)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Display("Please check your email to update password");
                                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Display("Please make sure the email entered is correct!");
                                }
                            }
                        });



            }
        });
    }













        // Display any string
        public void Display(String s){
            Toast.makeText(ForgotPasswordActivity.this, s, Toast.LENGTH_LONG).show();
        }



}