package DatabaseHelper;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateInfoActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    String UID = mUser.getUid();


    private Spinner ProvinceUpdateSpinner;
    private Spinner LicenseUpdateSpinner;

    private TextInputLayout FirstNameUpdateEditText;
    private TextInputLayout LastNameUpdateEditText;
    private TextInputLayout UsernameUpdateEditText;

    private Button UpdateButton;
    private Button DobUpdateButton;

    private TextView ageUpdateTextView;
    private TextView MinAgeTextView;

    DatabaseReference databaseReference;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        DobUpdateButton = findViewById(R.id.DobUpdateButton);
        ProvinceUpdateSpinner = findViewById(R.id.ProvinceUpdateSpinner);
        LicenseUpdateSpinner = findViewById(R.id.LicenseUpdatepinner);
        FirstNameUpdateEditText = findViewById(R.id.FirstNameUpdateEditText);
        LastNameUpdateEditText = findViewById(R.id.LastNameUpdateEditText);
        UsernameUpdateEditText = findViewById(R.id.UsernameUpdateEditText);

        UpdateButton = findViewById(R.id.UpdateButton);

        ageUpdateTextView = findViewById(R.id.ageUpdateTextView);
        MinAgeTextView = findViewById(R.id.MinAgeTextView);

        // List of the provinces
        ArrayAdapter<String> provinceList = new ArrayAdapter<String>(UpdateInfoActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.provinces));
        provinceList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ProvinceUpdateSpinner.setAdapter(provinceList);

        // List of Licenses types
        ArrayAdapter<String> licenseList = new ArrayAdapter<String>(UpdateInfoActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.license));
        licenseList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LicenseUpdateSpinner.setAdapter(licenseList);

        // Date of Birth section:
        onDateSet();

        //setting today's date on button
        DobUpdateButton.setText(todays_date());

        // Set the values in the text fields to show user their current values
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);



                if (userProfile != null) {

                    String username = userProfile.username;
                    String First_name = userProfile.first_name;
                    String Last_Name = userProfile.last_name;
                    String Age = userProfile.age;
                    String Province = userProfile.province;
                    String License = userProfile.license;

                    //Sets the value of province to the previous value
                    ProvinceUpdateSpinner.setSelection(getIndex(ProvinceUpdateSpinner, Province));


                    LicenseUpdateSpinner.setSelection(getIndex(LicenseUpdateSpinner, License));


//                  UsernameTextView.setText(username);
                    FirstNameUpdateEditText.getEditText().setText(First_name);
                    LastNameUpdateEditText.getEditText().setText(Last_Name);
                    UsernameUpdateEditText.getEditText().setText(username);
                    ageUpdateTextView.setText(Age);

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        UpdateInfoButton();


    }

    private int getIndex(Spinner provinceUpdateSpinner, String province) {

        int index = 0;

        for (int i=0;i<provinceUpdateSpinner.getCount();i++){
            if (provinceUpdateSpinner.getItemAtPosition(i).equals(province)){
                index = i;
            }
        }
        return index;
    }



    // Update the info in the database and redirect to the profile activity
    public void UpdateInfoButton(){
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!valid_firstName() | !valid_lastName() | !AgeCheck() | !ProvinceCheck() | !LicenseCheck()) {
                    return;
                }

                databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);



                        if (userProfile != null) {

                            String username = userProfile.username;
                            String First_name = userProfile.first_name;
                            String Last_Name = userProfile.last_name;
                            String Age = userProfile.age;
                            String Province = userProfile.province;
                            String License = userProfile.license;

                            //Sets the value of province to the previous value

                            // check if the username has been changed
                            if (!username.equals(UsernameUpdateEditText.getEditText().getText().toString())) {
                                databaseReference.child(UID).child("username").setValue(UsernameUpdateEditText.getEditText().getText().toString());

                            }

                            // check if the Province has been changed
                            if (!Province.equals(ProvinceUpdateSpinner.getSelectedItem().toString())) {
                                databaseReference.child(UID).child("province").setValue(ProvinceUpdateSpinner.getSelectedItem().toString());
                            }

                            // check if the License type has been changed
                            if (!License.equals(LicenseUpdateSpinner.getSelectedItem().toString())) {
                                databaseReference.child(UID).child("license").setValue(LicenseUpdateSpinner.getSelectedItem().toString());
                            }

                            // check if the age has been changed
                            if (!Age.equals(ageUpdateTextView.getText().toString())) {
                                databaseReference.child(UID).child("age").setValue(ageUpdateTextView.getText().toString());
                            }


                            // check if the last name has been changed
                            if (!Last_Name.equals(LastNameUpdateEditText.getEditText().getText().toString())) {
                                databaseReference.child(UID).child("last_name").setValue(LastNameUpdateEditText.getEditText().getText().toString());

                            }

                            // check if the first name has been changed
                            if (!First_name.equals(FirstNameUpdateEditText.getEditText().getText().toString())) {
                                databaseReference.child(UID).child("first_name").setValue(FirstNameUpdateEditText.getEditText().getText().toString());
                            }

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                        Display("Data has been Updated");

                        Intent intent = new Intent(UpdateInfoActivity.this, ProfileActivity.class);
                        startActivity(intent);



            }
        });

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
                DobUpdateButton.setText(CurrentDate);

                // Getting Today's date
                Calendar calendar = Calendar.getInstance();
                int yearT = calendar.get(Calendar.YEAR);
                int monthT = calendar.get(Calendar.MONTH);
                int dayT = calendar.get(Calendar.DAY_OF_MONTH);

                // Calculating Age
                int age = yearT - year;

//                if (dayT < day) {
//                    age--;
//                }
                Integer ageInt = new Integer(age);
                String age_string = ageInt.toString();

                // setting Text view to the age
                ageUpdateTextView.setText(age_string);
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

        String FirstName = FirstNameUpdateEditText.getEditText().getText().toString().trim();

        if(FirstName.isEmpty()){
            FirstNameUpdateEditText.setError("Required Field");
            return false;
        }
        else{
            FirstNameUpdateEditText.setError(null);
            return true;
        }
    }

    // checking if Last name is empty
    private boolean valid_lastName(){

        String LastName = LastNameUpdateEditText.getEditText().getText().toString().trim();

        if(LastName.isEmpty()){
            LastNameUpdateEditText.setError("Required Field");
            return false;
        }
        else{
            LastNameUpdateEditText.setError(null);
            return true;
        }
    }



    // checking if a province has been chosen
    private boolean ProvinceCheck() {
        String province = ProvinceUpdateSpinner.getSelectedItem().toString();
        if (province.equals("Choose the province you reside in")) {
            Display(province);
            return false;

        } else {
            return true;
        }
    }

    // checking if a license has been chosen
    private boolean LicenseCheck() {
        String license = LicenseUpdateSpinner.getSelectedItem().toString();
        if (license.equals("Choose the License you have")) {
            Display(license);
            return false;

        } else {
            return true;
        }
    }



    // Checking the age of the user
    private boolean AgeCheck(){
        String currentAge = ageUpdateTextView.getText().toString();
        String ProvinceSelected = ProvinceUpdateSpinner.getSelectedItem().toString();
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
        Toast.makeText(UpdateInfoActivity.this, s, Toast.LENGTH_LONG).show();
    }


    public void openCalandarDateSetUpdate(View view) {


        datePickerDialog.show();
    }
}