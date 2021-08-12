package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class viewMessage extends AppCompatActivity {

    public String messageid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent myIntent = getIntent();
        messageid = myIntent.getStringExtra("messageid");
        WebView webview = findViewById(R.id.viewMessage);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.sanaltebesir.com/android/tutor/viewMessage.php?messageid="+messageid);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
