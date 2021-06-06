package in.blogspot.tecnopandit.jabhunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
String result = "";
JSONObject resJson;
int count = 0;
final Calendar calendar = Calendar.getInstance();
EditText dateET, pinEt;
Button searchBtn, stopBtn, showSlots;

DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,date);
        updateLabel();
    }
};
    private void updateLabel() {
        String myFormat = "dd-MM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateET.setText(sdf.format(calendar.getTime()));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        dateET=findViewById(R.id.editTextDate2);
        searchBtn=findViewById(R.id.searchBtn);
        stopBtn=findViewById(R.id.stopBtn);
        pinEt=findViewById(R.id.editTextNumber);
        showSlots=findViewById(R.id.slotsBtn);
        Intent ser = new Intent(this,GetSlots.class);
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this,date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(dateET.getText().toString().isEmpty() &&pinEt.getText().toString().isEmpty())){
                    ser.putExtra("PIN",pinEt.getText().toString());
                    ser.putExtra("DATE",dateET.getText().toString());
                    ser.putExtra("cancelTimer","false");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(ser);
                    } else {
                        startService(ser);
                        stopBtn.setClickable(true);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"PIN and Date cannot be empty :)",Toast.LENGTH_LONG).show();
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ser.putExtra("cancelTimer","true");
                stopService(ser);
                Toast.makeText(getApplicationContext(),"Cancelling!",Toast.LENGTH_LONG).show();
            }

        });

        showSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SlotViewer.class);
                i.putExtra("data",result);
                startActivity(i);
            }
        });

    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getExtras().get("vaccineData").toString();
            ArrayList<JSONObject> activeVac = new ArrayList<>();
            try {
                resJson = new JSONObject(result);
                activeVac=parseJson(resJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("FROM ACTIVITY::::::", result);
            if(activeVac.size()!=0) {
                for(int i=0;i<activeVac.size();i++){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel("123", "vacChannel", NotificationManager.IMPORTANCE_HIGH);
                        notificationChannel.setDescription("Vaccine Channel");
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    NotificationCompat.Builder builder = null;
                    try {
                        builder = new NotificationCompat.Builder(getApplicationContext(), "123")
                                .setContentTitle(activeVac.get(i).getString("vaccine")+" Vaccine Slots Found!!")
                                .setSmallIcon(R.drawable.vaccinenoti)
                                .setContentText("Slots available at location "+activeVac.get(i).getString("name"))
                                .setContentInfo("Address: "+activeVac.get(i).getString("address")+", "+activeVac.get(i).getString("district_name")+", "+activeVac.get(i).getString("block_name"))
                                .setPriority(NotificationCompat.PRIORITY_HIGH);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(123, builder.build());
                }
                }

            count++;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter ifilter = new IntentFilter("DATA");
        this.registerReceiver(br,ifilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    public ArrayList<JSONObject> parseJson(JSONObject j) throws JSONException {
        ArrayList<JSONObject> resultAry = new ArrayList<>();
        JSONArray ary = j.getJSONArray("sessions");
        for(int i = 0; i<ary.length();i++){
            JSONObject temp=ary.getJSONObject(i);
            if(temp.getInt("available_capacity")!=0){
                count=1;
                resultAry.add(temp);
            }
        }
        return resultAry;
    }
}