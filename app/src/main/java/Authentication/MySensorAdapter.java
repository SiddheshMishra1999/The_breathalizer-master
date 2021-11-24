package Authentication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thebreathalizer.R;

import java.util.ArrayList;

import DatabaseHelper.Sensor;

public class MySensorAdapter  extends RecyclerView.Adapter<MySensorAdapter.myViewHolder> {

    ArrayList<Sensor> sensors;
    Context context;

    public MySensorAdapter(Context context, ArrayList<Sensor>sensors){
        this.sensors = sensors;
        this.context = context;

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sensor_data, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Sensor sensor = sensors.get(position);
        holder.tvDataa.setText(sensor.getvalue());
        holder.tvDatee.setText(sensor.getTime());

    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }


    public static  class myViewHolder extends  RecyclerView.ViewHolder{

        TextView tvDataa, tvDatee;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDataa = itemView.findViewById(R.id.tvDataa);
            tvDatee = itemView.findViewById(R.id.tvDatee);


        }
    }
}
