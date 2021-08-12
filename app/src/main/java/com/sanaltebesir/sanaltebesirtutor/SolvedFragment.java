package com.sanaltebesir.sanaltebesirtutor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SolvedFragment extends Fragment {


    public SolvedFragment() {
        // Required empty public constructor
    }

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/solvedQuestion.php";
    private JSONObject json;
    private SessionHandler session;
    public ListView lv;
    public String userid;
    public String questionid;
    public Integer arrayLength;
    ArrayList<HashMap<String, String>> questionList;
    JSONParser jsonParser = new JSONParser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_solved, container, false);
        lv = v.findViewById(R.id.assignedListview);
        questionList = new ArrayList<>();
        new AsyncTaskTest().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(getContext(), item, Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(view.getContext(), viewSolvedQuestions.class);
                myIntent.putExtra("questionid", questionList.get(position).get("questionid"));
                startActivity(myIntent);

            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getContext());
        userid = session.getUserDetails().userid;
    }


    class AsyncTaskTest extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Void doInBackground(Void... voids){

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid", userid));
                json = jsonParser.makeHttpRequest(phpUrl,
                        "POST", params);
                // check log cat for response
                Log.d("Create Response", json.toString());

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("qinfo");

                for(int i = 0; i<arr.length();i++){

                    String lecturename = arr.getJSONObject(i).getString("lecturename");
                    String notes = arr.getJSONObject(i).getString("notes");
                    String askingDate = arr.getJSONObject(i).getString("askingDate");
                    String questionid = arr.getJSONObject(i).getString("questionid");

                    HashMap<String, String> qList = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList.put("lecturename", lecturename);
                    qList.put("notes", notes);
                    qList.put("askingDate", askingDate);
                    qList.put("questionid", questionid);

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

            try {
                SimpleAdapter adapter = new SimpleAdapter(getContext(),questionList,R.layout.assigned_list_item,
                        new String[]{"lecturename","notes","askingDate"},
                        new int[]{R.id.name_tw, R.id.notes_tw, R.id.askingDate_tw});

                lv.setAdapter(adapter);

            }catch(NullPointerException e){

                e.printStackTrace();
            }
        }
    }

}
