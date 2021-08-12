package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class myStatistics extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/tutorstatistics.php";
    ArrayList<HashMap<String, String>> tutorstatisticsList;
    JSONParser jsonParser = new JSONParser();
    private JSONObject json;
    private ProgressDialog pDialog;
    private SessionHandler session;
    String userid;
    public TextView total;
    public TextView solved;
    public TextView refused;
    public TextView overtime;
    public TextView avarageanstime;
    public TextView avaragepoint;
    public TextView totalearnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_my_statistics);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userid = session.getUserDetails().userid;
        tutorstatisticsList = new ArrayList<>();
        total = findViewById(R.id.total);
        solved = findViewById(R.id.solved);
        refused = findViewById(R.id.refused);
        overtime = findViewById(R.id.overtime);
        avarageanstime = findViewById(R.id.avarageanstime);
        avaragepoint = findViewById(R.id.avaragepoint);
        totalearnings = findViewById(R.id.totalearnings);
        new GetStatistics().execute();
    }

    private class GetStatistics extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(myStatistics.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters and Post it
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("statistics");

                for(int i = 0; i<arr.length();i++){

                    String total = arr.getJSONObject(i).getString("total");
                    String solved = arr.getJSONObject(i).getString("solved");
                    String refused = arr.getJSONObject(i).getString("refused");
                    String timeover = arr.getJSONObject(i).getString("timeover");
                    String avarageanstime = arr.getJSONObject(i).getString("avarageanstime");
                    String rank = arr.getJSONObject(i).getString("rank");
                    String totalearning = arr.getJSONObject(i).getString("totalearning");

                    HashMap<String, String> qList = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList.put("total", total);
                    qList.put("solved", solved);
                    qList.put("refused", refused);
                    qList.put("timeover", timeover);
                    qList.put("avarageanstime", avarageanstime);
                    qList.put("rank", rank);
                    qList.put("totalearning", totalearning);

                    // adding contact to contact list
                    tutorstatisticsList.add(qList);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog.isShowing())
                pDialog.dismiss();

            try{

                total.setText(tutorstatisticsList.get(0).get("total"));
                solved.setText(tutorstatisticsList.get(0).get("solved"));
                refused.setText(tutorstatisticsList.get(0).get("refused"));
                overtime.setText(tutorstatisticsList.get(0).get("timeover"));
                avaragepoint.setText(tutorstatisticsList.get(0).get("rank"));
                avarageanstime.setText(tutorstatisticsList.get(0).get("avarageanstime")+" dk");
                totalearnings.setText(tutorstatisticsList.get(0).get("totalearning")+" TL");

            }catch(NullPointerException e){

                e.printStackTrace();

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
