package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class userTerms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_terms);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView webview = (WebView) findViewById(R.id.userterms_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.sanaltebesir.com/android/kullanim.php");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
