package com.example.thebreathalizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import Authentication.LoginActivity;
import Bluetooth_Connection.ConnectionActivity;

public class BlowerActivity extends AppCompatActivity {

    private Button HomeButton;
    private TextView DataReceivedTextView;
    private ScrollView scrollView;
    private Button AnalyzeButton;

    private FirebaseAuth mFirebaseAuth;

    private static final String TAG = "";
    private int myMaxChars = 50000;//Default
    private UUID myUUID;
    private BluetoothSocket myBluetoothSocket;
    private ReadInput myReadThread = null;

    private boolean isBluetoothConnected = false;
    private BluetoothDevice myDevice;
    private boolean hasUserInitiatedDisconnect = false;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blower_acftivity);

        HomeButton = findViewById(R.id.HomeButton);
        scrollView = findViewById(R.id.scrollView);
        DataReceivedTextView = findViewById(R.id.DataReceivedTextView);

        mFirebaseAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        myDevice = b.getParcelable("Bluetooth_Connection.SOCKET");
        myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        myMaxChars = b.getInt(ConnectionActivity.BUFFER_SIZE);
        Log.d(TAG, "Ready");

        DataReceivedTextView.setMovementMethod(new ScrollingMovementMethod());


        // check parameters and bring the user back to the activity they came from
        goHome();

//        goToAnalyzeActivity();


    }

    private class ReadInput implements Runnable {

        private boolean bluetoothStop = false;
        private Thread thread;

        public ReadInput() {
            thread = new Thread(this, "Input Thread");
            thread.start();
        }

        public boolean isRunning() {
            return thread.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = myBluetoothSocket.getInputStream();
                while (!bluetoothStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);
                        GetValue(strInput);



                        DataReceivedTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                DataReceivedTextView.append(strInput);

                                int txtLength = DataReceivedTextView.getEditableText().length();
                                if (txtLength > myMaxChars) {
                                    DataReceivedTextView.getEditableText().delete(0, txtLength - myMaxChars);
                                }

                            }
                        });


                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bluetoothStop = true;
        }

    }

    private void GetValue(String strInput) {
        String prefix = "Average Value: ";
        String noPrefix = strInput.substring(strInput.indexOf(prefix)+prefix.length());

        String[] part = noPrefix.split( "\\r?\\n");
        String Value = part[0];

        ConnectivityManager connectivityManager = (ConnectivityManager) (BlowerActivity.this).getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wifiConnection!= null && wifiConnection.isConnected()) || (mobileConnection != null && mobileConnection.isConnected())) {

            FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
            Intent intent;
            if(!(mUser == null)){
                // a user is signed in
                intent = new Intent(BlowerActivity.this, AnalyzeActivity.class);
                intent.putExtra("Value", Value);



            }else{
                // Guest user
                intent = new Intent(BlowerActivity.this, GuestAnalyzeActivity.class);
                intent.putExtra("Value", Value);

            }
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(BlowerActivity.this, GuestAnalyzeActivity.class);
            intent.putExtra("Value", Value);
            startActivity(intent);
            finish();

        }











    }


    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (myReadThread != null) {
                myReadThread.stop();
                while (myReadThread.isRunning())
                    ; // Wait until it stops
                myReadThread = null;

            }

            try {
                myBluetoothSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            isBluetoothConnected = false;
            if (hasUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    @Override
    protected void onPause() {
        if (myBluetoothSocket != null && isBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (myBluetoothSocket == null || !isBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(BlowerActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (myBluetoothSocket == null || !isBluetoothConnected) {
                    myBluetoothSocket = myDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    myBluetoothSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Display("Connected to device");
            isBluetoothConnected = true;
            myReadThread = new ReadInput(); // Kick off input reader



            progressDialog.dismiss();
        }

    }

    private void goHome() {
        // When the button is pressed, check if the user is logged in or not
        // if he isn't logged in, bring him back to the guest Activity
        // else bring him to Home activity
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
                Intent intent;
                if(!(mUser == null)){
                    // a user is signed in
                    intent = new Intent(BlowerActivity.this, HomeActivity.class);


                }else{
                    // Guest user
                    intent = new Intent(BlowerActivity.this, GuestUserActivity.class);


                }
                startActivity(intent);
            }
        });
    }

    // Display any string
    public void Display(String s){
        Toast.makeText(BlowerActivity.this, s, Toast.LENGTH_LONG).show();
    }

}