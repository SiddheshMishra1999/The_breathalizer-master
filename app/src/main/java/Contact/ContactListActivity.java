package Contact;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.example.thebreathalizer.AnalyzeActivity;
import com.example.thebreathalizer.BlowerActivity;
import com.example.thebreathalizer.GuestAnalyzeActivity;
import com.example.thebreathalizer.HomeActivity;
import com.example.thebreathalizer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import Authentication.LoginActivity;
import Bluetooth_Connection.ConnectionActivity;


public class ContactListActivity extends AppCompatActivity {

    private Button     ExitBlower;
    private FirebaseAuth mFirebaseAuth;

    private Button ExitButton;


    private RecyclerView ContactRecyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    MainAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ContactRecyclerView = findViewById(R.id.ContactRecyclerView);
        ExitBlower = findViewById(R.id.ExitBlower);
        mFirebaseAuth = FirebaseAuth.getInstance();
        ExitButton = findViewById(R.id.ExitButton);


        checkPermission();
        backToHome();
        exitApp();


    }
    private void exitApp() {
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, ConnectionActivity.class);
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

    private void backToHome() {
        ExitBlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) (ContactListActivity.this).getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if((wifiConnection!= null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {

                    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
                    Intent intent;
                    if(!(mUser == null)){
                        // a user is signed in
                        intent = new Intent(ContactListActivity.this, HomeActivity.class);




                    }else{
                        // Guest user
                        intent = new Intent(ContactListActivity.this, LoginActivity.class);


                    }
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(ContactListActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
            }
        });

    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(ContactListActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ContactListActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);

        }else {
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC";

        try (Cursor cursor = getContentResolver().query(uri, null, null, null, sort)) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.Contacts._ID
                    ));
                    String name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME
                    ));
                    Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + "=?";
                    Cursor phoneCursor = getContentResolver().query(
                            uriPhone, null, selection, new String[]{id}, null
                    );
                    if (phoneCursor.moveToNext()) {
                        String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        ContactModel model = new ContactModel();

                        model.setName(name);

                        model.setNumber(number);

                        arrayList.add(model);
                        phoneCursor.close();
                    }

                }
                cursor.close();
            }
        }
        ContactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this, arrayList);
        ContactRecyclerView.setAdapter(adapter);






    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContactList();
        }else {
            Toast.makeText(ContactListActivity.this,"Permission denied.", Toast.LENGTH_LONG).show();
            checkPermission();

        }
    }


}