package Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thebreathalizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import DatabaseHelper.User;

public class UserInfoActivity extends AppCompatActivity  {

    private DatePickerDialog datePickerDialog;

    private FirebaseAuth mAuth;


    private Spinner ProvinceSpinner;
    private Spinner LicenseSpinner;

    private TextInputLayout FirstNameEditText;
    private TextInputLayout LastNameEditText;

    private Button saveButton;
    private Button DobButton;

    private TextView ageTextView;
    private TextView MinAgeTextView;

    private TextView emailTextView;
    private TextView usernameTextView;
    private TextView passwordTextView;

    private ArrayList<String> values;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        // return to main activity
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        DobButton = findViewById(R.id.DobButton);
        ProvinceSpinner = findViewById(R.id.ProvinceSpinner);
        LicenseSpinner = findViewById(R.id.Licensepinner);
        FirstNameEditText = findViewById(R.id.FirstNameEditText);
        LastNameEditText = findViewById(R.id.LastNameEditText);

        saveButton = findViewById(R.id.saveButton);

        ageTextView = findViewById(R.id.ageTextView);
        MinAgeTextView = findViewById(R.id.MinAgeTextView);

        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTextView);


        // List of the provinces
        ArrayAdapter<String> provinceList = new ArrayAdapter<String>(UserInfoActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.provinces));
        provinceList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ProvinceSpinner.setAdapter(provinceList);

        // List of Licenses types
        ArrayAdapter<String> licenseList = new ArrayAdapter<String>(UserInfoActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.license));
        licenseList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LicenseSpinner.setAdapter(licenseList);

        // Date of Birth section:
        onDateSet();

        //setting today's date on button
        DobButton.setText(todays_date());

        // checking if each input is fulled
        validRegistration();

        // get email, password and username from SignUpActivity
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        String username = bundle.getString("username");
        String password = bundle.getString("password");
        // setting the values to Text Views
        emailTextView.setText(email);
        usernameTextView.setText(username);
        passwordTextView.setText(password);



    }



    // Set today's date on the button
    @NonNull
    private String  todays_date() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return setDate(year, month, day);
    }

    // Setting the chosen date on the button DOB
    public void onDateSet() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String CurrentDate = setDate(year, month, day);
                month += 1;
                DobButton.setText(CurrentDate);

                // Getting Today's date
                Calendar calendar = Calendar.getInstance();
                int yearT = calendar.get(Calendar.YEAR);
                int monthT = calendar.get(Calendar.MONTH);
                int dayT = calendar.get(Calendar.DAY_OF_MONTH);

                // Calculating Age
                int age = yearT - year;
                Integer ageInt = new Integer(age);
                String age_string = ageInt.toString();

                // setting Text view to the age
                ageTextView.setText(age_string);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.BUTTON_POSITIVE;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener , year, month, day);

        // Making so user cannot choose a date in the future
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    // checking if First name is empty
    private boolean valid_firstName(){

        String FirstName = FirstNameEditText.getEditText().getText().toString().trim();

        if(FirstName.isEmpty()){
            FirstNameEditText.setError("Required Field");
            return false;
        }
        else{
            FirstNameEditText.setError(null);
            return true;
        }
    }

    // checking if Last name is empty
    private boolean valid_lastName(){

        String LastName = LastNameEditText.getEditText().getText().toString().trim();

        if(LastName.isEmpty()){
            LastNameEditText.setError("Required Field");
            return false;
        }
        else{
            LastNameEditText.setError(null);
            return true;
        }
    }



    // checking if a province has been chosen
    private boolean ProvinceCheck() {
        String province = ProvinceSpinner.getSelectedItem().toString();
        if (province.equals("Choose the province you reside in")) {
            Display(province);
            return false;

        } else {
            return true;
        }
    }

    // checking if a license has been chosen
    private boolean LicenseCheck() {
        String license = LicenseSpinner.getSelectedItem().toString();
        if (license.equals("Choose the License you have")) {
            Display(license);
            return false;

        } else {
            return true;
        }
    }



            // Checking the age of the user
    private boolean AgeCheck(){
        String currentAge = ageTextView.getText().toString();
        String ProvinceSelected = ProvinceSpinner.getSelectedItem().toString();
        int currentAge_int = Integer.parseInt(currentAge);
        int min_age = 18;
        int min_age2 = 19;

        if(ProvinceSelected.equals("Quebec") | ProvinceSelected.equals("Manitoba") | ProvinceSelected.equals("Alberta" )){


                    MinAgeTextView.setText("Minimum age for " + ProvinceSelected + " is " + min_age);



            if(currentAge_int < min_age){
                Display("You are too young to register.");
                return false;
            }
            else
                return true;
        }else{

                    MinAgeTextView.setText("Minimum age for " + ProvinceSelected + " is " + min_age2);

            if(currentAge_int < min_age2){
                Display("You are too young to register.");
                return false;
            }
            else
                return true;
        }


    }

    // Checking if all the requirements are met
    private void validRegistration(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!valid_firstName() | !valid_lastName() | !AgeCheck() | !ProvinceCheck() | !LicenseCheck()){
                    return;
                }

                // putting all the strings in 1 place to use for authentication
                String FirstName = FirstNameEditText.getEditText().getText().toString().trim();
                String LastName = LastNameEditText.getEditText().getText().toString().trim();
                String currentAge = ageTextView.getText().toString();
                String province = ProvinceSpinner.getSelectedItem().toString();
                String license = LicenseSpinner.getSelectedItem().toString();
                String email = emailTextView.getText().toString();
                String username = usernameTextView.getText().toString();
                String password = passwordTextView.getText().toString();





                // Registering a User
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    User user = new User(email, username, FirstName, LastName, currentAge, province, license);

                                    FirebaseDatabase.getInstance().getReference("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Display("User has been registered!");
                                                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                Display("Failed to register User");
                                            }
                                        }
                                    });
                                    // Email verification
                                    FirebaseUser userB = FirebaseAuth.getInstance().getCurrentUser();

                                        userB.sendEmailVerification();
                                        Display("Please check your email to verify your account!");

                                }else{
                                    Display("Failed to register User");
                                }
                            }
                        });

            }
        });


    }




    // Date Format
    private String setDate(int year, int month, int day) {

        month +=1;
        return month_format(month) + " " + day + " " + year;
    }
    // Month in 3 letters
    private String month_format(int month) {

        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }


    // Display any string
    public void Display(String s){
        Toast.makeText(UserInfoActivity.this, s, Toast.LENGTH_LONG).show();
    }


    // On click Listener for date
    public void openCalandarDateSet(View view) {
        datePickerDialog.show();
    }
}