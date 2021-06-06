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
    Timer t;
    String cancelTimer = "";

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
//                    result=sampleData;
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
        cancelTimer = intent.getStringExtra("cancelTimer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("From Service","!!!!!!!!Called!!!!!!!!!");
                fetchData fd = new fetchData();
                fd.execute(pin,date);

            }
        };
        t = new Timer();

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

    @Override
    public void onDestroy() {
        Log.e("CALLED FROM ONDESTROY","::: OUTSIDE IF::::");
        if(cancelTimer=="true"){
            Log.e("CALLED FROM ONDESTROY","::: INSIDE IF:::: TRUE");
            this.stopSelf();
        }
        super.onDestroy();
    }

    String sampleData = "{\n" +
            "  \"sessions\": [\n" +
            "    {\n" +
            "      \"center_id\": 706648,\n" +
            "      \"name\": \"18-44 SONI HOSPITAL WP\",\n" +
            "      \"address\": \"SONI HOSPITAL\",\n" +
            "      \"state_name\": \"Rajasthan\",\n" +
            "      \"district_name\": \"Jaipur I\",\n" +
            "      \"block_name\": \"Jaipur I Urban\",\n" +
            "      \"pincode\": 302004,\n" +
            "      \"from\": \"09:00:00\",\n" +
            "      \"to\": \"17:00:00\",\n" +
            "      \"lat\": 26,\n" +
            "      \"long\": 75,\n" +
            "      \"fee_type\": \"Paid\",\n" +
            "      \"session_id\": \"baac7763-fff7-4a03-af70-3e8c0fb99224\",\n" +
            "      \"date\": \"06-06-2021\",\n" +
            "      \"available_capacity_dose1\": 0,\n" +
            "      \"available_capacity_dose2\": 0,\n" +
            "      \"available_capacity\": 0,\n" +
            "      \"fee\": \"0\",\n" +
            "      \"min_age_limit\": 18,\n" +
            "      \"vaccine\": \"COVAXIN\",\n" +
            "      \"slots\": [\n" +
            "        \"09:00AM-11:00AM\",\n" +
            "        \"11:00AM-01:00PM\",\n" +
            "        \"01:00PM-03:00PM\",\n" +
            "        \"03:00PM-05:00PM\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"center_id\": 603488,\n" +
            "      \"name\": \"ISOLATION SMS HOSPITAL (IDH)\",\n" +
            "      \"address\": \"SMS HOSPITAL JAIPUR\",\n" +
            "      \"state_name\": \"Rajasthan\",\n" +
            "      \"district_name\": \"Jaipur I\",\n" +
            "      \"block_name\": \"Jaipur I Urban\",\n" +
            "      \"pincode\": 302004,\n" +
            "      \"from\": \"09:00:00\",\n" +
            "      \"to\": \"17:00:00\",\n" +
            "      \"lat\": 26,\n" +
            "      \"long\": 75,\n" +
            "      \"fee_type\": \"Free\",\n" +
            "      \"session_id\": \"6e0947f9-424c-4325-9326-aba9829b6315\",\n" +
            "      \"date\": \"06-06-2021\",\n" +
            "      \"available_capacity_dose1\": 0,\n" +
            "      \"available_capacity_dose2\": 0,\n" +
            "      \"available_capacity\": 26,\n" +
            "      \"fee\": \"0\",\n" +
            "      \"min_age_limit\": 45,\n" +
            "      \"vaccine\": \"COVISHIELD\",\n" +
            "      \"slots\": [\n" +
            "        \"09:00AM-11:00AM\",\n" +
            "        \"11:00AM-01:00PM\",\n" +
            "        \"01:00PM-03:00PM\",\n" +
            "        \"03:00PM-05:00PM\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"center_id\": 423259,\n" +
            "      \"name\": \"UPHC Motikatla MHA\",\n" +
            "      \"address\": \"DHP Motikatla\",\n" +
            "      \"state_name\": \"Rajasthan\",\n" +
            "      \"district_name\": \"Jaipur I\",\n" +
            "      \"block_name\": \"Jaipur I Urban\",\n" +
            "      \"pincode\": 302004,\n" +
            "      \"from\": \"09:00:00\",\n" +
            "      \"to\": \"17:00:00\",\n" +
            "      \"lat\": 26,\n" +
            "      \"long\": 75,\n" +
            "      \"fee_type\": \"Free\",\n" +
            "      \"session_id\": \"aeeddc0d-bdb1-479b-996a-e88de725c388\",\n" +
            "      \"date\": \"06-06-2021\",\n" +
            "      \"available_capacity_dose1\": 0,\n" +
            "      \"available_capacity_dose2\": 0,\n" +
            "      \"available_capacity\": 0,\n" +
            "      \"fee\": \"0\",\n" +
            "      \"min_age_limit\": 45,\n" +
            "      \"vaccine\": \"COVISHIELD\",\n" +
            "      \"slots\": [\n" +
            "        \"09:00AM-11:00AM\",\n" +
            "        \"11:00AM-01:00PM\",\n" +
            "        \"01:00PM-03:00PM\",\n" +
            "        \"03:00PM-05:00PM\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"center_id\": 707325,\n" +
            "      \"name\": \"18-44 DENTAL HOSPITAL WP\",\n" +
            "      \"address\": \"DANTEL HOSPITAL\",\n" +
            "      \"state_name\": \"Rajasthan\",\n" +
            "      \"district_name\": \"Jaipur I\",\n" +
            "      \"block_name\": \"Jaipur I Urban\",\n" +
            "      \"pincode\": 302004,\n" +
            "      \"from\": \"09:00:00\",\n" +
            "      \"to\": \"17:00:00\",\n" +
            "      \"lat\": 26,\n" +
            "      \"long\": 75,\n" +
            "      \"fee_type\": \"Free\",\n" +
            "      \"session_id\": \"0c962750-36d3-4494-94e8-153228061d12\",\n" +
            "      \"date\": \"06-06-2021\",\n" +
            "      \"available_capacity_dose1\": 0,\n" +
            "      \"available_capacity_dose2\": 0,\n" +
            "      \"available_capacity\": 33,\n" +
            "      \"fee\": \"0\",\n" +
            "      \"min_age_limit\": 45,\n" +
            "      \"vaccine\": \"COVISHIELD\",\n" +
            "      \"slots\": [\n" +
            "        \"09:00AM-11:00AM\",\n" +
            "        \"11:00AM-01:00PM\",\n" +
            "        \"01:00PM-03:00PM\",\n" +
            "        \"03:00PM-05:00PM\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"center_id\": 584300,\n" +
            "      \"name\": \"ADARASH NAGAR\",\n" +
            "      \"address\": \"ADARASH NAGAR 1\",\n" +
            "      \"state_name\": \"Rajasthan\",\n" +
            "      \"district_name\": \"Jaipur I\",\n" +
            "      \"block_name\": \"Jaipur I Urban\",\n" +
            "      \"pincode\": 302004,\n" +
            "      \"from\": \"09:00:00\",\n" +
            "      \"to\": \"17:00:00\",\n" +
            "      \"lat\": 26,\n" +
            "      \"long\": 75,\n" +
            "      \"fee_type\": \"Free\",\n" +
            "      \"session_id\": \"8241da0f-a268-4aca-a4a1-98f59ffdebd9\",\n" +
            "      \"date\": \"06-06-2021\",\n" +
            "      \"available_capacity_dose1\": 0,\n" +
            "      \"available_capacity_dose2\": 0,\n" +
            "      \"available_capacity\": 0,\n" +
            "      \"fee\": \"0\",\n" +
            "      \"min_age_limit\": 45,\n" +
            "      \"vaccine\": \"COVISHIELD\",\n" +
            "      \"slots\": [\n" +
            "        \"09:00AM-11:00AM\",\n" +
            "        \"11:00AM-01:00PM\",\n" +
            "        \"01:00PM-03:00PM\",\n" +
            "        \"03:00PM-05:00PM\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"center_id\": 706648,\n" +
            "      \"name\": \"18-44 SONI HOSPITAL WP\",\n" +
            "      \"address\": \"SONI HOSPITAL\",\n" +
            "      \"state_name\": \"Rajasthan\",\n" +
            "      \"district_name\": \"Jaipur I\",\n" +
            "      \"block_name\": \"Jaipur I Urban\",\n" +
            "      \"pincode\": 302004,\n" +
            "      \"from\": \"09:00:00\",\n" +
            "      \"to\": \"17:00:00\",\n" +
            "      \"lat\": 26,\n" +
            "      \"long\": 75,\n" +
            "      \"fee_type\": \"Paid\",\n" +
            "      \"session_id\": \"140469b7-102b-4718-b8a7-48d5afbe239b\",\n" +
            "      \"date\": \"06-06-2021\",\n" +
            "      \"available_capacity_dose1\": 0,\n" +
            "      \"available_capacity_dose2\": 0,\n" +
            "      \"available_capacity\": 2,\n" +
            "      \"fee\": \"0\",\n" +
            "      \"min_age_limit\": 45,\n" +
            "      \"vaccine\": \"COVAXIN\",\n" +
            "      \"slots\": [\n" +
            "        \"09:00AM-11:00AM\",\n" +
            "        \"11:00AM-01:00PM\",\n" +
            "        \"01:00PM-03:00PM\",\n" +
            "        \"03:00PM-05:00PM\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}