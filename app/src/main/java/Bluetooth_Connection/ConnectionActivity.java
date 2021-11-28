package Bluetooth_Connection;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thebreathalizer.BlowerActivity;
import com.example.thebreathalizer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import Authentication.LoginActivity;

public class ConnectionActivity extends AppCompatActivity {
    private Button SearchDeviceButton;
    private ListView DevicesListView;
    private Button ConnectDeviceButton;
    private BluetoothAdapter bluetoothAdapter;
    private TextView ListOfContentTextView;

    // This is the code we use for BT Enable
    private static final int BT_ENABLE_REQUEST = 10;
    private static final int SETTINGS = 20;

    private UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Default
    private int bufferSize = 50000;



    public static final String DEVICE_EXTRA = "Bluetooth_Connection.SOCKET";
    public static final String DEVICE_UUID = "Bluetooth_Connection.uuid";
    private static final String DEVICE_LIST = "Bluetooth_Connection.devicelist";
    private static final String DEVICE_LIST_SELECTED = "Bluetooth_Connection.devicelistselected";
    public static final String BUFFER_SIZE = "Bluetooth_Connection.buffersize";
    //    public static final String BUFFER_SIZE = "com.example.anysensormonitoring.buffersize";
    private static final String TAG = "BlueTest5-MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        SearchDeviceButton = findViewById(R.id.SearchDeviceButton);
        DevicesListView = findViewById(R.id.DevicesListView);
        ConnectDeviceButton = findViewById(R.id.ConnectDeviceButton);
        ListOfContentTextView = findViewById(R.id.ListOfContentTextView);
        if(getIntent().getBooleanExtra("EXIT", false)){
            finish();
        }

        if (savedInstanceState != null) {
            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
            if (list != null) {
                initList(list);
                MyAdapter adapter = (MyAdapter) DevicesListView.getAdapter();
                int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
                if (selectedIndex != -1) {
                    adapter.setSelectedIndex(selectedIndex);
                    ConnectDeviceButton.setEnabled(true);
                }
            } else {
                initList(new ArrayList<BluetoothDevice>());
            }

        } else {
            initList(new ArrayList<BluetoothDevice>());
        }
        // when search is pressed
       searchDevices();

        //when connect is pressed

        ConnectDevice();




    }

    private void ConnectDevice() {
        ConnectDeviceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                try {

                    BluetoothDevice device = ((MyAdapter) (DevicesListView.getAdapter())).getSelectedItem();


                    Intent intent = new Intent(getApplicationContext(), BlowerActivity.class);
                    intent.putExtra(DEVICE_EXTRA, device);
                    intent.putExtra(DEVICE_UUID, DEVICE_UUID.toString());
                    intent.putExtra(BUFFER_SIZE, bufferSize);
                    startActivity(intent);
                } catch (Exception e) {
                    Display("Please Select a device to connect!");
                }

            }


        });

    }

    private void searchDevices() {
        SearchDeviceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ConnectDeviceButton.setVisibility(View.VISIBLE);


                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                if (bluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
                } else if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, BT_ENABLE_REQUEST);
                } else {
                    new SearchDevices().execute();
                }


            }
        });
    }

    protected void onPause() {
// TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
// TODO Auto-generated method stub
        super.onStop();
    }

    // Requesting the user to enable bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_ENABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    Display("Bluetooth Enabled successfully");
                    new SearchDevices().execute();
                } else {
                    Display("Bluetooth couldn't be enabled");
                }

                break;
            case SETTINGS: //If the settings have been updated
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String uuid = prefs.getString("prefUuid", "Null");
                myUuid = UUID.fromString(uuid);
                Log.d(TAG, "UUID: " + uuid);
                String bufSize = prefs.getString("prefTextBuffer", "Null");
                bufferSize = Integer.parseInt(bufSize);

                String orientation = prefs.getString("prefOrientation", "Null");
                Log.d(TAG, "Orientation: " + orientation);
                if (orientation.equals("Landscape")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (orientation.equals("Portrait")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (orientation.equals("Auto")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initialize the List adapter
     *
     * @param objects
     */
    private void initList(List<BluetoothDevice> objects) {
        final MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.list_of_devices, R.id.ListOfContentTextView, objects);
        DevicesListView.setAdapter(adapter);
        DevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                ConnectDeviceButton.setEnabled(true);
            }
        });
    }

    /**
     * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
     * will show up with this. I didn't see any need to re-build the wheel over here
     *
     * @author ryder
     */
    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                listDevices.add(device);
            }
            return listDevices;

        }

        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            if (listDevices.size() > 0) {
                MyAdapter adapter = (MyAdapter) DevicesListView.getAdapter();
                adapter.replaceItems(listDevices);
            } else {
                Display("No paired devices found, please pair your serial BT device and try again");
            }
        }

    }

    /**
     * Custom adapter to show the current devices in the list. This is a bit of an overkill for this
     * project, but I figured it would be good learning
     * Most of the code is lifted from somewhere but I can't find the link anymore
     *
     * @author ryder
     */
    private class MyAdapter extends ArrayAdapter<BluetoothDevice> {
        private int selectedIndex;
        private Context context;
        private int selectedColor = Color.parseColor("#abcdef");
        private List<BluetoothDevice> myList;

        public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
            super(ctx, resource, textViewResourceId, objects);
            context = ctx;
            myList = objects;
            selectedIndex = -1;
        }

        public void setSelectedIndex(int position) {
            selectedIndex = position;
            notifyDataSetChanged();
        }

        public BluetoothDevice getSelectedItem() {
            return myList.get(selectedIndex);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView tv;
        }

        public void replaceItems(List<BluetoothDevice> list) {
            myList = list;
            notifyDataSetChanged();
        }

        public List<BluetoothDevice> getEntireList() {
            return myList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            ViewHolder holder;
            if (convertView == null) {
                vi = LayoutInflater.from(context).inflate(R.layout.list_of_devices, null);
                holder = new ViewHolder();

                holder.tv = (TextView) vi.findViewById(R.id.ListOfContentTextView);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if (selectedIndex != -1 && position == selectedIndex) {
                holder.tv.setBackgroundColor(selectedColor);
            } else {
                holder.tv.setBackgroundColor(Color.WHITE);
            }
            BluetoothDevice device = myList.get(position);
            holder.tv.setText(device.getName() + "\n " + device.getAddress());

            return vi;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.homescreen, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Intent intent = new Intent(ConnectionActivity.this, BlowerActivity.class);
//                startActivityForResult(intent, SETTINGS);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    // Display any string
    public void Display(String s){
        Toast.makeText(ConnectionActivity.this, s, Toast.LENGTH_LONG).show();
    }
}



