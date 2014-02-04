package com.dabgroup;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.VideoView;

public class Splash extends Activity implements OnCompletionListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        VideoView video = (VideoView) findViewById(R.id.videoView);
        video.setVideoPath("android.resource://com.progetto/raw/" + R.raw.intro);
        video.start();
        video.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Intent intent = new Intent(this, DriveDroid.class);
        startActivity(intent);
        finish();
    }
}