package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class privacyTerms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_terms);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView webview = (WebView) findViewById(R.id.privacyterms_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.sanaltebesir.com/android/gizlilik.php");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
