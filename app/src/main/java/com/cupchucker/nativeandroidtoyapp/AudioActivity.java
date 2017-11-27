package com.cupchucker.nativeandroidtoyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class AudioActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static String mFileName = null;

    private MediaRecorder mRecorder = null;

    private MediaPlayer mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;

    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private boolean mStartPlaying = false;

    private boolean mStartRecording = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;

            switch (item.getItemId()) {
                case R.id.navigation_text:
                    setContentView(R.layout.activity_main);
                    intent = new Intent(AudioActivity.this, MainActivity.class);
                    break;
                case R.id.navigation_audio:
                    break;
                case R.id.navigation_graphics:
                    setContentView(R.layout.activity_graphics);
                    intent = new Intent(AudioActivity.this, GraphicsActivity.class);
                    break;
            }

            if (intent != null) {
                startActivity(intent);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

//        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_audio);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // record btn event listener
        final Button recordBtn = (Button) findViewById(R.id.btnRecord);
        recordBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                toggleRecording(mStartRecording);
                if (mStartRecording) {
                    recordBtn.setText("Stop recording");
                } else {
                    recordBtn.setText("Start recording");
                }
                mStartRecording = !mStartRecording;
                System.out.println("Record clicked");
            }
        });

        // playback btn event listener
        Button playbackBtn = (Button) findViewById(R.id.btnPlayback);
        playbackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO
                System.out.println("Playback recording clicked");
            }
        });

        // play .wav btn event listener
        final Button playWavBtn = (Button) findViewById(R.id.btnPlayMp3);
        playWavBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mStartPlaying = !mStartPlaying;
                togglePlay(mStartPlaying);

                if (!mStartPlaying) {
                    playWavBtn.setText("Start playing");
                } else {
                    playWavBtn.setText("Stop playing");
                }

                System.out.println("Play .wav clicked");
            }
        });
    }

    private void toggleRecording(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            System.out.println("prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void togglePlay(boolean start) {
        if (start) {
            startPlayingWav();
        } else {
            stopPlayingWav();
        }
    }

    private void startRecordingPlayback() {
        stopPlayingWav();
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            System.out.println("problem");
        }
    }

    private void startPlayingWav() {
        stopPlayingWav();
        mPlayer = MediaPlayer.create(this, R.raw.pug_pinball);
//        try {
//            int resID = getResources().getIdentifier("pug_pinball.wav", "raw", getPackageName());
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
            mPlayer.start();
//        } catch (IOException e) {
//            System.out.println("prepare() failed");
//        }
    }

    private void stopPlayingWav() {
        if (mPlayer != null) {
            mPlayer.release();
        }

        mPlayer = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
            finish();
        }

    }
}
