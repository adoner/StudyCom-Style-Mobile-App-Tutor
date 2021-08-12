package com.sanaltebesir.sanaltebesirtutor;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StickyService extends Service {

    public String userid;
    private SessionHandler session;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        session = new SessionHandler(getApplicationContext());
        userid = session.getUserDetails().userid;
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
      //Whatever you want
        Log.d("Application Status", "Killed");
    }

}