package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class supportPage extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/adminMessage.php";
    private static final String KEY_EMPTY = "";
    public JSONObject json;
    public ProgressDialog pDialog;
    private SessionHandler session;
    JSONParser jsonParser = new JSONParser();
    public String userid;
    public EditText etSubject;
    public EditText etMesaj;
    public Button sendButton;
    public String subject;
    public String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_page);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(supportPage.this);
        userid = session.getUserDetails().userid;
        etSubject = findViewById(R.id.etSubject);
        etMesaj = findViewById(R.id.etMesaj);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message button click
                subject = etSubject.getText().toString().trim();
                message = etMesaj.getText().toString().trim();

                if(validateInputs()) {
                    new sendMessage().execute();
                }
            }
        });
    }

    private class sendMessage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(supportPage.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid", userid));
                params.add(new BasicNameValuePair("subject", subject));
                params.add(new BasicNameValuePair("message", message));
                json = jsonParser.makeHttpRequest(phpUrl, "POST", params);

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
            finish();
        }
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */
    private boolean validateInputs() {

        if (KEY_EMPTY.equals(subject)) {
            etSubject.setError("Konu boş olamaz");
            etSubject.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(message)) {
            etMesaj.setError("Mesaj boş olamaz");
            etMesaj.requestFocus();
            return false;

        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
