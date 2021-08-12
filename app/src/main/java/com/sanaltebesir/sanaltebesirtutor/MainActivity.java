package com.sanaltebesir.sanaltebesirtutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/tutorstatistics.php";
    private static String phpUrl2 = "http://www.sanaltebesir.com/android/tutor/firebaseToken.php";
    private static final String TAG = "MyNotificationToken";
    private JSONObject json;
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> tutorstatisticsList;
    ArrayList<HashMap<String, String>> unviewedList;
    public String userid;
    public TextView twSolved;
    public TextView twRefused;
    public TextView twBalance;
    public TextView tvMessage;
    public Button btnMessage;
    public String token;
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Button soruiste = findViewById(R.id.btnSoruiste);
        Button sorular = findViewById(R.id.btnSorular);
        Button hesap = findViewById(R.id.btnHesap);
        Button ozelders = findViewById(R.id.btnOzelders);
        Button btnProfile =findViewById(R.id.btnProfile);
        Button btnAbout =findViewById(R.id.btnAbout);
        Button btnWallet =findViewById(R.id.btnWallet);
        btnMessage = findViewById(R.id.btnMessage);
        ImageView logoview = findViewById(R.id.logoImgview);
        twSolved = findViewById(R.id.solvedQuestion);
        twRefused = findViewById(R.id.refusedQuestion);
        twBalance = findViewById(R.id.accountBalance);
        tvMessage = findViewById(R.id.tvMessage);
        tutorstatisticsList = new ArrayList<>();
        unviewedList = new ArrayList<>();
        userid = session.getUserDetails().userid;
        Intent stickyService = new Intent(this, StickyService.class);
        startService(stickyService);

        soruiste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soruIste();
            }
        });
        hesap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAccount();
            }
        });
        ozelders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOzelDers();
            }
        });
        sorular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSorular();
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myProfile();
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUs();
            }
        });
        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWallet();
            }
        });
        new GetStatistics().execute();
        new sendFirebaseToken().execute();
        new GetMessage().execute();
        //session.logoutUser();
        logoview.bringToFront();
        tvMessage.setTranslationZ(10);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessage();
            }
        });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
       FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token
                token = task.getResult().getToken();
                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
            }
        });
    }

    private class GetStatistics extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters
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
                    String balance = arr.getJSONObject(i).getString("balance");
                    String status = arr.getJSONObject(i).getString("status");

                    HashMap<String, String> qList = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList.put("total", total);
                    qList.put("solved", solved);
                    qList.put("refused", refused);
                    qList.put("balance", balance);
                    qList.put("status", status);

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

            try{

                twSolved.setText(tutorstatisticsList.get(0).get("solved")+" adet");
                twRefused.setText(tutorstatisticsList.get(0).get("refused")+" adet");
                twBalance.setText(tutorstatisticsList.get(0).get("balance")+" TL");

            }catch(NullPointerException e){

                e.printStackTrace();

            }
        }
    }

    private class GetMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl, "POST", params);
                // check log cat for response
                //Log.d("Create Response", json.toString());

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr4 = json.getJSONArray("messages");

                for(int i = 0; i<arr4.length();i++){

                    String unviewed = arr4.getJSONObject(i).getString("unviewed");

                    HashMap<String, String> qList4 = new HashMap<>();
                    // adding each child node to HashMap key => value
                    qList4.put("unviewed", unviewed);

                    // adding contact to contact list
                    unviewedList.add(qList4);

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
                //Message
                tvMessage.setText(unviewedList.get(0).get("unviewed"));

            }catch(NullPointerException e){

                e.printStackTrace();

            }
        }
    }
    private class sendFirebaseToken extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid", userid));
                params.add(new BasicNameValuePair("token", token));
                json = jsonParser.makeHttpRequest(phpUrl2, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    public void soruIste(){

        if(Integer.parseInt(tutorstatisticsList.get(0).get("status"))==1){

            Intent intent = new Intent(this, getQuestion.class);
            startActivity(intent);
        }
        if(Integer.parseInt(tutorstatisticsList.get(0).get("status"))==0){

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Sanal Tebeşir");
            builder.setMessage("Kayıt sırasında vermiş olduğunuz bilgi ve dökümanlar incelenerek olumlu olduğu takdirde bu özellik açılacaktır.");
            builder.setNegativeButton("Tamam", null);
            builder.show();
        }

    }

    public void myAccount(){

        Intent intent = new Intent(this, myAccount.class);
        startActivity(intent);
    }

    public void openSorular(){

        Intent intent = new Intent(this, myArchive.class);
        startActivity(intent);
    }

    public void myProfile(){

        Intent intent = new Intent(this, myProfile.class);
        startActivity(intent);
    }

    public void aboutUs(){

        Intent intent = new Intent(this, aboutPage.class);
        startActivity(intent);
    }

    public void myWallet(){

        Intent intent = new Intent(this, myWallet.class);
        startActivity(intent);
    }

    public void openMessage(){

        Intent intent = new Intent(this, myMessage.class);
        startActivity(intent);
    }

    public void openOzelDers(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sanal Tebeşir");
        builder.setMessage("Özel Ders özelliği çok yakın zamanda hizmetinizde olacak!.");
        builder.setNegativeButton("Tamam", null);
        /*builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        });*/
        builder.show();

    }
}
