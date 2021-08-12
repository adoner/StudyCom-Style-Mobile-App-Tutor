package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class aboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView webview = (WebView) findViewById(R.id.webViewAboutus);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.sanaltebesir.com/android/hakkimizda.php");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
