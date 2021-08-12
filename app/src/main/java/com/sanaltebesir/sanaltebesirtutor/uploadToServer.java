package com.sanaltebesir.sanaltebesirtutor;


import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class uploadToServer extends AppCompatActivity {

    public static final String UPLOAD_URL = "http://www.sanaltebesir.com/android/tutor/UpdateServerData.php";
    private static final String phpUrl = "http://www.sanaltebesir.com/android/tutor/soruBank.php";
    public ProgressBar progressBar;
    private ProgressDialog pDialog;
    private SessionHandler session;
    public JSONObject json;
    public List<String> subjectList;
    public int progressStatus = 0;
    public Handler hdlr = new Handler();
    public Boolean progress = true;
    public TextView prgTxt;
    public String notification;
    public String questionid;
    public String imagename;
    public Spinner spin;
    public EditText edtxt;
    public Button submitBtn;
    public String userid;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_to_server);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(getApplicationContext());
        userid = session.getUserDetails().userid;
        submitBtn = findViewById(R.id.submit_btn);
        edtxt = findViewById(R.id.upload_edtxt);
        spin = findViewById(R.id.lectures_spinner);
        progressBar = findViewById(R.id.prgBar);
        prgTxt = findViewById(R.id.prgrs_txtvw);

        Intent myIntent = getIntent();
        questionid = myIntent.getStringExtra("questionid");
        imagename = myIntent.getStringExtra("imagename");

        submitBtn.setEnabled(false);
        edtxt.setEnabled(false);
        spin.setEnabled(false);

        SharedPreferences preferences=getSharedPreferences("UploadProgress",MODE_PRIVATE);
        progress = preferences.getBoolean("progress", false);
        notification = preferences.getString("notification", "");
        new GetProgress().execute();
        new GetSubjects().execute();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateServerData().execute();
            }
        });
    }

    private class GetProgress extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void...voids) {

            try {

                new Thread(new Runnable() {
                    public void run() {
                        while (progressStatus < 100) {

                            progressStatus += 1;

                            // Update the progress bar and display the current value in text view
                            hdlr.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressStatus);
                                }
                            });
                            try {
                                // Sleep for 100 milliseconds to show the progress slowly.
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            }catch(RuntimeException e){

                e.printStackTrace();
            }

            Boolean percent = true;
            return percent;
        }

        protected void onPostExecute(Boolean percent){
            super.onPostExecute(percent);
            try {
                if (percent) {
                    prgTxt.setText(notification);
                    progressBar.setVisibility(View.GONE);

                    submitBtn.setEnabled(true);
                    edtxt.setEnabled(true);
                    spin.setEnabled(true);
                }
            }catch(RuntimeException e){e.printStackTrace();}
        }
    }

    private class UpdateServerData extends AsyncTask<Void,Void,String> {

        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(uploadToServer.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void...voids) {

            try{

                HashMap<String,String> data = new HashMap<>();
                data.put("userid", userid);
                data.put("questionid", questionid);
                data.put("imagename", imagename);
                data.put("subject", spin.getSelectedItem().toString());
                data.put("notes", edtxt.getText().toString());
                String result = rh.postRequest(UPLOAD_URL,data);
                return result;

            }catch(Exception e){

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }
    }

    private class GetSubjects extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("questionid", questionid));
                json = jsonParser.makeHttpRequest(phpUrl, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("subjectdata");
                subjectList = new ArrayList<>();

                for(int i = 0; i<arr.length();i++){

                    String subject = arr.getJSONObject(i).getString("subject");
                    subjectList.add(i, subject);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try{

                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,subjectList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setPrompt("İlgili Konuyu Seçiniz..");
                spin.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.subject_nothing_selected, getApplicationContext()));

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
