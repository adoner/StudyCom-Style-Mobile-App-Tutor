package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class viewSolvedQuestions extends AppCompatActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    public String questionid;
    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/getImageLinks.php";
    private JSONObject json;
    ArrayList<HashMap<String, String>> imageLink;
    JSONParser jsonParser = new JSONParser();
    public String[] urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solved_questions);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageLink = new ArrayList<>();
        Intent myIntent = getIntent();
        questionid = myIntent.getStringExtra("questionid");
        new GetImageLink().execute();

    }

    private void init() {

        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(viewSolvedQuestions.this,urls));

        /*CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);*/

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        //indicator.setRadius(5 * density);

        NUM_PAGES = urls.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        /*swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);*/

        // Pager listener over indicator
       /* indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });*/

    }

    class GetImageLink extends AsyncTask<Void,Void,Void> {

        protected Void doInBackground(Void... voids){

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

                JSONArray arr = json.getJSONArray("imagelinks");

                for(int i = 0; i<arr.length();i++){

                    String questionlink = arr.getJSONObject(i).getString("questionlink");
                    String answerlink = arr.getJSONObject(i).getString("answerlink");

                    HashMap<String, String> qList = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList.put("questionlink", questionlink);
                    qList.put("answerlink", answerlink);

                    // adding contact to contact list
                    imageLink.add(qList);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

            urls = new String[] {
                    "http://www.sanaltebesir.com/uploads/questions/"+imageLink.get(0).get("questionlink"),
                    "http://www.sanaltebesir.com/uploads/answers/"+imageLink.get(0).get("answerlink")
            };

            init();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
