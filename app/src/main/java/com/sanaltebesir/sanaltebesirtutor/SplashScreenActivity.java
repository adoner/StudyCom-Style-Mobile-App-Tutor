package com.sanaltebesir.sanaltebesirtutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    public TextView twNoconnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        twNoconnection = findViewById(R.id.noconnection_tw);
        twNoconnection.setVisibility(View.INVISIBLE);

        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
            //Show the connected screen
            Thread timerThread = new Thread(){
                public void run(){
                    try{
                        sleep(2000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }finally{

                        Intent intent = new Intent(SplashScreenActivity.this, signinActivity.class);
                        startActivity(intent);

                    }
                }
            };
            timerThread.start();

        } else {
            //Show disconnected screen
            twNoconnection.setVisibility(View.VISIBLE);
            twNoconnection.setText("İnternet bağlantınız yok.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
