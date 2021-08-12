package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.VideoView;

public class howToWork extends AppCompatActivity {

    public VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_work);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        videoView = findViewById(R.id.videoView);
        videoView.setVideoPath("http://www.sanaltebesir.com/android/videos/howtoworktutor.mp4");
        //videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }
}
