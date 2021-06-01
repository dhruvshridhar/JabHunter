package in.blogspot.tecnopandit.jabhunter;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
public class GetSlots extends Service {
    public GetSlots() {
    }

    class fetchData extends AsyncTask<String,String,String>{
        public JSONObject jsonObj ;
        public  String result;
        public JSONArray sessionAry;

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String pin = "302004";
                String date = "01-06-2021";
                pin = strings[0];
                date = strings[1];
                String urlStr = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode="+pin+"&date="+date;
                URL url = new URL(urlStr);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.connect();
                if (conn.getResponseCode()==HttpsURLConnection.HTTP_OK){
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String temp="";
                    while((temp=bufferedReader.readLine())!=null){
//                    temp=bufferedReader.readLine();
                        stringBuilder.append(temp);
                    }
                    result = stringBuilder.toString();
                    jsonObj = new JSONObject(result);
                    sessionAry = jsonObj.getJSONArray("sessions");
//                Log.e("DATA::::::",sessionAry.toString());
                    Intent sendToApp = new Intent();
                    sendToApp.putExtra("vaccineData",result);
                    sendToApp.setAction("DATA");
//                    Log.e("RESULT:::::", result);
                    sendBroadcast(sendToApp);
                }
                else{
                    result="Something went wrong!";
                }
            }
            catch (Exception e){
                Log.e("ERROR::::",e.getMessage());
            }
            return result;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String pin = intent.getStringExtra("PIN");
        String date = intent.getStringExtra("DATE");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("From Service","!!!!!!!!Called!!!!!!!!!");
                fetchData fd = new fetchData();
                fd.execute(pin,date);
            }
        };
        Timer t = new Timer();
        t.scheduleAtFixedRate(timerTask,0,2000);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}