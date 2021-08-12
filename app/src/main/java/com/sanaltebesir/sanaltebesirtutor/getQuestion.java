package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class getQuestion extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/assignedQuestion.php";
    private static String phpUrl3 = "http://www.sanaltebesir.com/android/tutor/getAvailable.php";
    private static String phpUrl2 = "http://www.sanaltebesir.com/android/tutor/checkStatus.php";
    private static String phpUrl4 = "http://www.sanaltebesir.com/android/tutor/totalQuestion.php";
    private static String phpUrl5 = "http://www.sanaltebesir.com/android/tutor/questionFromPool.php";
    private SessionHandler session;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private JSONObject json;
    private ProgressDialog pDialog;
    public ListView lv;
    public Switch availableSwitch;
    public TextView tw_Dersadi;
    public TextView tw_soruaded;
    public Button soruiste;
    public TextView twSliding;
    public TextView tvButtonClosed;
    String userid;
    String switchStatus;
    ArrayList<HashMap<String, String>> questionList;
    ArrayList<HashMap<String, String>> statusList;
    ArrayList<HashMap<String, String>> questionTotal;
    ArrayList<HashMap<String, String>> questionFromPool;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_get_question);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mySwipeRefreshLayout = this.findViewById(R.id.swipeContainer);
        availableSwitch = findViewById(R.id.swAvailable);
        tw_Dersadi = findViewById(R.id.tw_dersadi);
        tw_soruaded = findViewById(R.id.tw_soruaded);
        soruiste = findViewById(R.id.btn_soruiste);
        twSliding = findViewById(R.id.twSwiping);
        tvButtonClosed = findViewById(R.id.tvButtonclosed);
        userid = session.getUserDetails().userid;
        questionList = new ArrayList<>();
        statusList = new ArrayList<>();
        questionTotal = new ArrayList<>();
        questionFromPool = new ArrayList<>();
        lv = findViewById(R.id.assignedList);
        soruiste.setEnabled(false);
        new CheckStatus().execute();
        new GetTotalSolvedQuestion().execute();
        new GetAssignedQuestions().execute();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(false);
                        Intent i = new Intent(getQuestion.this, getQuestion.class);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                    }
                }
        );

        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    switchStatus = "1";
                    soruiste.setEnabled(true);
                    twSliding.setVisibility(View.VISIBLE);
                    tvButtonClosed.setVisibility(View.INVISIBLE);

                }else{
                    switchStatus = "0";
                    soruiste.setEnabled(false);
                    twSliding.setVisibility(View.INVISIBLE);
                    tvButtonClosed.setVisibility(View.VISIBLE);

                }
                    new GetAvailable().execute();
               // Toast.makeText(getApplicationContext(),"mmm",Toast.LENGTH_LONG).show();

            }
        });

        soruiste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do anything you want
                new GetQuestionFromPool().execute();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {

                    //Toast.makeText(getApplicationContext(), questionList.get(0).get("id"), Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(view.getContext(), viewQuestion.class);
                    myIntent.putExtra("id", questionList.get(0).get("id"));
                    startActivity(myIntent);
                    finish();
                }
                if (position == 1) {

                    //Toast.makeText(getApplicationContext(), questionList.get(1).get("id"), Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(view.getContext(), viewQuestion.class);
                    myIntent.putExtra("id",questionList.get(1).get("id"));
                    startActivity(myIntent);
                    finish();
                }

            }
        });
    }

     class GetAssignedQuestions extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getQuestion.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... voids){

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("assigned");

                for(int i = 0; i<arr.length();i++){

                    String id = arr.getJSONObject(i).getString("id");
                    String lecturename = arr.getJSONObject(i).getString("lecturename");
                    String notes = arr.getJSONObject(i).getString("notes");
                    String askingDate = arr.getJSONObject(i).getString("askingDate");

                    HashMap<String, String> qList = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList.put("id", id);
                    qList.put("lecturename", lecturename);
                    qList.put("notes", notes);
                    qList.put("askingDate", askingDate);

                    // adding contact to contact list
                    questionList.add(qList);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void aVoid){

            super.onPostExecute(aVoid);
            pDialog.dismiss();

            try {

                if(questionList.size()>0) {
                   // iwSliding.setVisibility(View.INVISIBLE);
                    twSliding.setVisibility(View.INVISIBLE);
                    tvButtonClosed.setVisibility(View.INVISIBLE);
                }
                SimpleAdapter adapter = new SimpleAdapter(
                        getQuestion.this,questionList,
                        R.layout.assigned_list_item, new String[]{"lecturename","notes","askingDate"},
                        new int[]{R.id.name_tw, R.id.notes_tw, R.id.askingDate_tw});

                lv.setAdapter(adapter);

            }catch(NullPointerException e){

                e.printStackTrace();
            }
        }
    }

     class CheckStatus extends AsyncTask<Void,Void,Void> {

        protected Void doInBackground(Void... voids){

            try{

                // Building Parameters
                List<NameValuePair> params2 = new ArrayList<>();
                params2.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl2, "POST", params2);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr2 = json.getJSONArray("checkstatus");

                for(int i = 0; i<arr2.length();i++){

                    String lecture = arr2.getJSONObject(i).getString("lecture");
                    String available = arr2.getJSONObject(i).getString("available");

                    HashMap<String, String> qList2 = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList2.put("lecture", lecture);
                    qList2.put("available", available);

                    // adding contact to contact list
                    statusList.add(qList2);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void aVoid){

            super.onPostExecute(aVoid);

            try {

                //Toast.makeText(getApplicationContext(),statusList.get(0).get("available"),Toast.LENGTH_LONG).show();
                switch(statusList.get(0).get("available")){
                    case "1":
                        availableSwitch.setChecked(true);
                        tvButtonClosed.setVisibility(View.INVISIBLE);
                        twSliding.setVisibility(View.VISIBLE);
                    break;
                    case "0":
                        availableSwitch.setChecked(false);
                        tvButtonClosed.setVisibility(View.VISIBLE);
                        twSliding.setVisibility(View.INVISIBLE);
                    break;
                }
                tw_Dersadi.setText(statusList.get(0).get("lecture"));

            }catch(NullPointerException e){

                e.printStackTrace();
            }
        }
    }

     class GetAvailable extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Void doInBackground(Void... voids){

            try{

                // Building Parameters
                List<NameValuePair> params3 = new ArrayList<>();
                params3.add(new BasicNameValuePair("userid", userid));
                params3.add(new BasicNameValuePair("switchStatus", switchStatus));
                json = jsonParser.makeHttpRequest(phpUrl3, "POST", params3);

            }catch(Exception e){

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void aVoid){

            super.onPostExecute(aVoid);

        }
    }

     class GetTotalSolvedQuestion extends AsyncTask<Void,Void,Void> {

        protected Void doInBackground(Void... voids){

            try{

                // Building Parameters
                List<NameValuePair> params4 = new ArrayList<>();
                params4.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl4, "POST", params4);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr4 = json.getJSONArray("totalamount");

                for(int i = 0; i<arr4.length();i++){

                    String totalquestion = arr4.getJSONObject(i).getString("totalquestion");

                    HashMap<String, String> qList4 = new HashMap<>();
                    // adding each child node to HashMap key => value
                    qList4.put("totalquestion", totalquestion);
                    // adding contact to contact list
                    questionTotal.add(qList4);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void aVoid){

            super.onPostExecute(aVoid);

            try {

                tw_soruaded.setText(questionTotal.get(0).get("totalquestion"));

            }catch(NullPointerException e){

                e.printStackTrace();
            }
        }
    }

     class GetQuestionFromPool extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getQuestion.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(Void... voids){

            try{

                // Building Parameters
                List<NameValuePair> params5 = new ArrayList<>();
                params5.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl5, "POST", params5);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr5 = json.getJSONArray("pool");

                for(int i = 0; i<arr5.length();i++) {

                    String id = arr5.getJSONObject(i).getString("id");
                    String lecturename = arr5.getJSONObject(i).getString("lecturename");
                    String notes = arr5.getJSONObject(i).getString("notes");
                    String askingDate = arr5.getJSONObject(i).getString("askingDate");

                    HashMap<String, String> qList5 = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList5.put("id", id);
                    qList5.put("lecturename", lecturename);
                    qList5.put("notes", notes);
                    qList5.put("askingDate", askingDate);

                    // adding contact to contact list
                    questionFromPool.add(qList5);
                }

            }catch (JSONException e) {

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void aVoid){

            super.onPostExecute(aVoid);
            pDialog.dismiss();

            if(questionFromPool.size()>0) {
                // iwSliding.setVisibility(View.INVISIBLE);
                twSliding.setVisibility(View.INVISIBLE);
            }

            try {

                SimpleAdapter adapter = new SimpleAdapter(
                        getQuestion.this,questionFromPool,
                        R.layout.assigned_list_item, new String[]{"lecturename","notes","askingDate"},
                        new int[]{R.id.name_tw, R.id.notes_tw, R.id.askingDate_tw});

                lv.setAdapter(adapter);

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
