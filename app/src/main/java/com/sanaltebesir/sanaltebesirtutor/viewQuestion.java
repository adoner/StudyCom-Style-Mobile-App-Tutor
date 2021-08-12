package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class viewQuestion extends AppCompatActivity {

    public String questionid;
    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/viewQuestion.php";
    private static String phpUrl2 = "http://www.sanaltebesir.com/android/tutor/acceptDecline.php";
    private JSONObject json;
    private ProgressDialog pDialog;
    public ImageView uploadImage;
    public ImageView expandBtn;
    public TextView commentText;
    public TextView lectureText;
    public Button answer;
    public Button accept;
    public Button decline;
    public String declineresult;
    ArrayList<HashMap<String, String>> questionData;
    JSONParser jsonParser = new JSONParser();
    AlertDialog alertDialog1;
    CharSequence[] values = {"Kötü Fotoğraf Kalitesi","Yanlış Ders","Eksik Soru","Hatalı Soru"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent myIntent = getIntent();
        questionid = myIntent.getStringExtra("id");
        uploadImage = findViewById(R.id.UploadImageview);
        commentText = findViewById(R.id.comment_textview);
        lectureText = findViewById(R.id.lecture_textview);
        answer = findViewById(R.id.answer_button);
        accept = findViewById(R.id.acceptBtn);
        decline = findViewById(R.id.declineBtn);
        expandBtn = findViewById(R.id.expandBtn);
        questionData = new ArrayList<>();
        new GetQuestionData().execute();
        answer.setVisibility(View.INVISIBLE);
        //Toast.makeText(getApplicationContext(), questionid, Toast.LENGTH_LONG).show();

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do it
                Intent myIntent = new Intent(getApplicationContext(), uploadAnswer.class);
                myIntent.putExtra("id", questionid);
                startActivity(myIntent);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SendAcceptData().execute();

            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAlertDialogWithRadioButtonGroup();

            }
        });

        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //expand Button
                AlertDialog.Builder builder = new AlertDialog.Builder(viewQuestion.this);
                builder.setMessage(questionData.get(0).get("notes"));
                builder.setNegativeButton("Tamam", null);
                builder.show();
                //Toast.makeText(getApplicationContext(), questionData.get(0).get("notes"),Toast.LENGTH_LONG).show();
            }
        });
    }

    private class GetQuestionData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(viewQuestion.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("questionid", questionid));
                json = jsonParser.makeHttpRequest(phpUrl,
                        "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("questiondata");

                for(int i = 0; i<arr.length();i++){

                    String name = arr.getJSONObject(i).getString("name");
                    String lecture = arr.getJSONObject(i).getString("lecture");
                    String notes = arr.getJSONObject(i).getString("notes");
                    String studentclass = arr.getJSONObject(i).getString("studentclass");
                    String accepted = arr.getJSONObject(i).getString("accepted");

                    HashMap<String, String> qList = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList.put("name", name);
                    qList.put("lecture", lecture);
                    qList.put("notes", notes);
                    qList.put("studentclass", studentclass);
                    qList.put("accepted", accepted);

                    // adding contact to contact list
                    questionData.add(qList);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {

                //Toast.makeText(getApplicationContext(), questionData.get(0).get("name"),Toast.LENGTH_LONG).show();
                Picasso.get().load("http://www.sanaltebesir.com/uploads/questions/"+questionData.get(0).get("name")).into(uploadImage);

                if(questionData.get(0).get("notes")!="") {
                    commentText.setText(questionData.get(0).get("notes"));
                    lectureText.setText(questionData.get(0).get("lecture")+"/"+questionData.get(0).get("studentclass"));
                }

                if(questionData.get(0).get("notes")==""){
                    commentText.setText(R.string.nocomment);
                }
                if(Integer.parseInt(questionData.get(0).get("accepted"))==0){
                    answer.setVisibility(View.INVISIBLE);
                }
                if(Integer.parseInt(questionData.get(0).get("accepted"))==1){
                    answer.setVisibility(View.VISIBLE);
                }
            }catch(NullPointerException e){

                e.printStackTrace();

            }
        }
    }

    private class SendAcceptData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("questionid", questionid));
                params.add(new BasicNameValuePair("response", "accept"));
                json = jsonParser.makeHttpRequest(phpUrl2, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try{

                answer.setVisibility(View.VISIBLE);
                accept.setVisibility(View.INVISIBLE);
                decline.setVisibility(View.INVISIBLE);

            }catch(Exception e){

                e.printStackTrace();

            }
        }
    }

    private class SendDeclineData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("questionid", questionid));
                params.add(new BasicNameValuePair("response", "decline"));
                params.add(new BasicNameValuePair("declineresult", declineresult));
                json = jsonParser.makeHttpRequest(phpUrl2, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try{

                Intent myIntent = new Intent(getApplicationContext(), getQuestion.class);
                startActivity(myIntent);
                finish();

            }catch(Exception e){

                e.printStackTrace();

            }
        }
    }

    public void CreateAlertDialogWithRadioButtonGroup(){

        AlertDialog.Builder builder = new AlertDialog.Builder(viewQuestion.this);
        builder.setTitle("Lütfen bir neden seçiniz.");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                declineresult = Integer.toString(item);
                //Toast.makeText(viewQuestion.this, declineresult, Toast.LENGTH_LONG).show();
                new SendDeclineData().execute();
                alertDialog1.dismiss();

            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id==android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
