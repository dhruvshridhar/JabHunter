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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
String result = "";
int count = 0;
final Calendar calendar = Calendar.getInstance();
EditText dateET, pinEt;
Button searchBtn, stopBtn;

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
                stopService(ser);
            }
        });

    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getExtras().get("vaccineData").toString();
            Log.e("FROM ACTIVITY::::::", result);
            if(count<1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel("123", "vacChannel", NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.setDescription("Vaccine Channel");
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "123")
                        .setContentTitle("Vaccine Slot Found")
                        .setSmallIcon(R.drawable.vaccinenoti)
                        .setContentText("Slots available at location XYZ")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(123, builder.build());
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
}