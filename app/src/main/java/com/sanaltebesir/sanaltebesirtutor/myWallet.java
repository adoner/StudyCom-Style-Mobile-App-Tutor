package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class myWallet extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/tutorstatistics.php";
    private JSONObject json;
    private ProgressDialog pDialog;
    private SessionHandler session;
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> accountInfo;
    public String userid;
    public TextView tvAmount;
    public TextView tvBalance;
    public TextView tvTransactionHistory;
    public CardView cvBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(getApplicationContext());
        userid = session.getUserDetails().userid;
        accountInfo = new ArrayList<>();
        tvAmount = findViewById(R.id.tvAmount);
        tvBalance = findViewById(R.id.tvBalance);
        tvTransactionHistory = findViewById(R.id.tvTransactionHistory);
        cvBank = findViewById(R.id.cvBank);
        tvBalance.bringToFront();
        new GetAccountContetnt().execute();

        cvBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // withdraw to Bank
                if(Float.parseFloat(accountInfo.get(0).get("balance"))>=50.00){
                    Intent myIntent = new Intent(getApplicationContext(), withdrawtoBank.class);
                    startActivity(myIntent);
                }
                // withdraw to Bank
                if(Float.parseFloat(accountInfo.get(0).get("balance"))<50.00){

                    AlertDialog.Builder builder = new AlertDialog.Builder(myWallet.this);
                    builder.setTitle("Sanal Tebeşir");
                    builder.setMessage("Kullanılabilir bakiyeniz enaz 50 TL olmalıdır!");
                    builder.setNegativeButton("Tamam", null);
                    builder.show();

                }
            }
        });

        tvTransactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // transaction history
                Intent myIntent = new Intent(getApplicationContext(), transactionHistory.class);
                startActivity(myIntent);
            }
        });
    }

    private class GetAccountContetnt extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(myWallet.this);
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
                params.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("statistics");

                for(int i = 0; i<arr.length();i++){

                    String balance = arr.getJSONObject(i).getString("balance");

                    HashMap<String, String> qList = new HashMap<>();
                    // adding each child node to HashMap key => value
                    qList.put("balance", balance);

                    // adding contact to contact list
                    accountInfo.add(qList);

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

                tvBalance.setText(accountInfo.get(0).get("balance")+" TL");

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
