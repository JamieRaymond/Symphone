package com.example.jamier.symphone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class RecordSongActivity extends AppCompatActivity{

    private TextView countDownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMili = 30000; //30secs
    private String timeLeftText;
    private boolean timerRunning;

    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_song);

        //COUNTDOWN TEXT//
        countDownText = findViewById(R.id.timerText);

        //BUTTONS//
        final Button recordSongButton = findViewById(R.id.record_button);
        final Button playSongButton = findViewById(R.id.play_button);
        final Button stopSongButton = findViewById(R.id.stop_button);

        //FILE OUTPUT//
        OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/recording.aac";

        //DISABLE BUTTONS UNTIL RECORD IS PRESSED//
        playSongButton.setEnabled(false);
        stopSongButton.setEnabled(false);

        //RECORD//
        recordSongButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonTapped(recordSongButton);
                stopSongButton.setEnabled(true);
                recordSongButton.setEnabled(false);
            }
        });

        //PLAY//
        playSongButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonTapped(playSongButton);
                playSongButton.setEnabled(false);
                recordSongButton.setEnabled(false);
                stopSongButton.setEnabled(true);
            }
        });

        //STOP//
        stopSongButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonTapped(stopSongButton);
                stopTimer();
                stopSongButton.setEnabled(false);
                playSongButton.setEnabled(true);
                recordSongButton.setEnabled(true);
            }
        });

        //SUBMIT//
        Button submitSongButton = findViewById(R.id.submit_button);
        submitSongButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openConfirmationActivity();
            }
        });
    }

    public void buttonTapped(View v){
        switch(v.getId()){
            case R.id.record_button:
                try{
                    beginRecording();
                    startStop();
                    Toast.makeText(getApplicationContext(), "Recording", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.play_button:
                try{
                    playRecording();
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.stop_button:
                try{
                    stopPlayback();
                    countDownText.setText("30");
                    Toast.makeText(getApplicationContext(), "Stopping Playback", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void beginRecording() throws Exception{
        ditchMediaRecorder();
        File outFile = new File(OUTPUT_FILE);

        if(outFile.exists())
        {
            outFile.delete();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(OUTPUT_FILE);

        recorder.setMaxDuration(30000);
        recorder.prepare();
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                    Toast.makeText(getApplicationContext(), "Recording limit reached. Stopping recording", Toast.LENGTH_LONG).show();
                }
            }
        });
        recorder.start();
    }

    private void playRecording() throws Exception{
        ditchMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(OUTPUT_FILE);
        mediaPlayer.prepare();
        mediaPlayer.start();

    }

    private void stopPlayback() {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
        }
    }

    private void ditchMediaRecorder() {
        if(recorder != null)
        {
            recorder.release();
        }
    }
    private void ditchMediaPlayer() {
        if(mediaPlayer != null)
        {
            try{
                mediaPlayer.release();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void startStop(){
        if(timerRunning){
            stopTimer();
        }
        else {
            startTimer();
        }
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMili, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMili = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                countDownText.setText("DONE");
            }
        }.start();

        timerRunning = true;
    }

    public void stopTimer(){
        countDownTimer.cancel();
        timerRunning = false;
        timeLeftInMili = 30000;
        timeLeftText = "30";
    }

    public void updateTimer(){
        int seconds = (int) timeLeftInMili / 1000;

        timeLeftText = "" + seconds;
        if(seconds < 10){
            timeLeftText = "0" + timeLeftText;
        }
        countDownText.setText(timeLeftText);
    }

    public void openConfirmationActivity() {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        startActivity(intent);

    }

}
