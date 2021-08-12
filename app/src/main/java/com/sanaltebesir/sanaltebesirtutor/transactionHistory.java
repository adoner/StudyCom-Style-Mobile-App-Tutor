package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class transactionHistory extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/transactionHistory.php";
    private JSONObject json;
    private ProgressDialog pDialog;
    private SessionHandler session;
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> transactionHistory;
    ArrayList<HashMap<String, String>> transactionProcessing;
    public String userid;
    public TextView tvProcessing;
    public TextView tvAmount;
    public TextView tvNotransaction;
    public ListView lvTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(getApplicationContext());
        userid = session.getUserDetails().userid;
        transactionHistory = new ArrayList<>();
        transactionProcessing = new ArrayList<>();
        tvProcessing = findViewById(R.id.tvProcessing);
        tvAmount = findViewById(R.id.tvAmount);
        tvNotransaction = findViewById(R.id.tvNotransaction);
        lvTransaction = findViewById(R.id.lvTransaction);

        new GetTransactionHistory().execute();
    }

    private class GetTransactionHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(transactionHistory.this);
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

                JSONArray arr = json.getJSONArray("transactions");

                for(int i = 0; i<arr.length();i++){

                    String title = arr.getJSONObject(i).getString("title");
                    String amount = arr.getJSONObject(i).getString("amount");
                    String transactiondate = arr.getJSONObject(i).getString("transactiondate");
                    String counter = arr.getJSONObject(i).getString("counter");

                    HashMap<String, String> qList = new HashMap<>();
                    // adding each child node to HashMap key => value
                    qList.put("title", title);
                    qList.put("amount", amount);
                    qList.put("transactiondate", transactiondate);
                    qList.put("counter", counter);

                    // adding contact to contact list
                    transactionHistory.add(qList);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }

            try {

                JSONArray arr2 = json.getJSONArray("processing");

                for(int j = 0; j<arr2.length();j++){

                    String title = arr2.getJSONObject(j).getString("title");
                    String amount = arr2.getJSONObject(j).getString("amount");

                    HashMap<String, String> qList2 = new HashMap<>();
                    // adding each child node to HashMap key => value
                    qList2.put("title", title);
                    qList2.put("amount", amount);

                    // adding contact to contact list
                    transactionProcessing.add(qList2);

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

                if(Integer.parseInt(transactionHistory.get(0).get("counter"))==0){

                    tvNotransaction.setText("Hesap Hareketi Bulunamadı");
                    tvProcessing.setVisibility(View.INVISIBLE);
                    tvAmount.setVisibility(View.INVISIBLE);
                    lvTransaction.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getApplicationContext(), "aa", Toast.LENGTH_SHORT).show();
                }else{

                    if(Integer.parseInt(transactionHistory.get(0).get("counter"))==1){

                        tvNotransaction.setVisibility(View.INVISIBLE);
                        tvProcessing.setText("İşlem devam ediyor.....");
                        tvAmount.setText(transactionProcessing.get(0).get("amount")+" TL");
                        lvTransaction.setVisibility(View.INVISIBLE);

                    }
                    if(Integer.parseInt(transactionHistory.get(0).get("counter"))==2){

                        tvNotransaction.setVisibility(View.INVISIBLE);
                        tvProcessing.setVisibility(View.INVISIBLE);
                        tvAmount.setVisibility(View.INVISIBLE);
                        SimpleAdapter adapter = new SimpleAdapter(
                                transactionHistory.this,transactionHistory,
                                R.layout.transaction_list_item, new String[]{"title","amount","transactiondate"},
                                new int[]{R.id.tvTitle,R.id.tvAmount,R.id.tvDate});

                        lvTransaction.setAdapter(adapter);

                    }
                    if(Integer.parseInt(transactionHistory.get(0).get("counter"))==3){

                        tvNotransaction.setVisibility(View.INVISIBLE);
                        tvProcessing.setText("İşlem devam ediyor.....");
                        tvAmount.setText(transactionProcessing.get(0).get("amount")+" TL");

                        SimpleAdapter adapter = new SimpleAdapter(
                                transactionHistory.this,transactionHistory,
                                R.layout.transaction_list_item, new String[]{"title","amount","transactiondate"},
                                new int[]{R.id.tvTitle,R.id.tvAmount,R.id.tvDate});

                        lvTransaction.setAdapter(adapter);

                    }
                }


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
