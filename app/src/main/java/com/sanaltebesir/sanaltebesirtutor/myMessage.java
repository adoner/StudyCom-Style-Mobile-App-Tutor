package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class myMessage extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/message.php";
    private JSONObject json;
    private ProgressDialog pDialog;
    public ListView lv;
    String userid;
    ArrayList<HashMap<String, String>> messageList;
    JSONParser jsonParser = new JSONParser();
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(getApplicationContext());
        messageList = new ArrayList<>();
        lv = findViewById(R.id.lvMessages);
        userid = session.getUserDetails().userid;
        new GetMessages().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), viewMessage.class);
                myIntent.putExtra("messageid", messageList.get(position).get("id"));
                startActivity(myIntent);
            }
        });
    }

    private class GetMessages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(myMessage.this);
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
                // check log cat for response
                // Log.d("Create Response", json.toString());

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("messages");

                for(int i = 0; i<arr.length();i++){

                    String id = arr.getJSONObject(i).getString("id");
                    String title = arr.getJSONObject(i).getString("title");
                    String body = arr.getJSONObject(i).getString("body");
                    String messagedate = arr.getJSONObject(i).getString("messagedate");
                    String viewed = arr.getJSONObject(i).getString("viewed");
                    String viewdate = arr.getJSONObject(i).getString("viewdate");

                    HashMap<String, String> mList = new HashMap<>();
                    // adding each child node to HashMap key => value
                    mList.put("id", id);
                    mList.put("title", title);
                    mList.put("body", body);
                    mList.put("messagedate", messagedate);
                    mList.put("viewed", viewed);
                    mList.put("viewdate", viewdate);

                    // adding contact to contact list
                    messageList.add(mList);

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
                ListAdapter adapter = new SimpleAdapter(
                        myMessage.this, messageList,
                        R.layout.message_list_item, new String[]{"title", "messagedate","viewed"},
                        new int[]{R.id.title, R.id.message, R.id.viewed});

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
