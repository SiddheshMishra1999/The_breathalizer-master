package Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thebreathalizer.AnalyzeActivity;
import com.example.thebreathalizer.HomeActivity;
import com.example.thebreathalizer.R;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DatabaseHelper.Sensor;
import DatabaseHelper.User;

public class SensorDataDisplayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = mUser.getUid();


    private DatabaseReference root = db.getReference().child(userID);

    private MySensorAdapter adapter;

    private ArrayList<Sensor> list;

    private Button GoHomeButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data_display);

        GoHomeButton = findViewById(R.id.GoHomeButton);




        recyclerView = findViewById(R.id.SensorRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new MySensorAdapter(this,list);

        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                try {
                    if (userProfile != null) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Sensor sensor = dataSnapshot.getValue(Sensor.class);
                            list.add(sensor);

                        }
                        adapter.notifyDataSetChanged();
                        Display("Data Retrieved "  );
                    } else {

                        Display("No Data Found");
                    }

                }catch (Exception e){
                    Display("Didn't work "+ e);

                }

            }

            public void goToHome2(){

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        GoHome();

    }

    // go to home
    public void GoHome(){
        GoHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SensorDataDisplayActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    // Display any string
    public void Display(String s){
        Toast.makeText(SensorDataDisplayActivity.this, s, Toast.LENGTH_LONG).show();
    }
}