package com.example.jamier.symphone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class RecordSongActivity extends AppCompatActivity{

    private EditText songName;

    private TextView countDownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMiliseconds = 30000; //30secs
    private String timeLeftText;
    private boolean timerRunning;

    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_song);

        //Sends user to Login Activity if not logged in//
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            logout();
        }

        //COUNTDOWN TEXT//
        countDownText = findViewById(R.id.timerText);

        //SONG NAME//
        songName = findViewById(R.id.song_name);

        //BUTTONS//
        final Button recordSongButton = findViewById(R.id.record_button);
        final Button playSongButton = findViewById(R.id.play_button);
        final Button stopSongButton = findViewById(R.id.stop_button);

        //FILE OUTPUT//
        OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/recording.aac";

        //STORAGE REFERENCE
        storageReference = FirebaseStorage.getInstance().getReference();

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
                uploadFile();
                openMainActivity();
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
        countDownTimer = new CountDownTimer(timeLeftInMiliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMiliseconds = millisUntilFinished;
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
        timeLeftInMiliseconds = 30000;
        timeLeftText = "30";
    }

    public void updateTimer(){
        int seconds = (int) timeLeftInMiliseconds / 1000;

        timeLeftText = "" + seconds;
        if(seconds < 10){
            timeLeftText = "0" + timeLeftText;
        }
        countDownText.setText(timeLeftText);
    }

    private void uploadFile(){


        Uri file = Uri.fromFile(new File(OUTPUT_FILE));

        if(file != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

            storageReference = storageReference.child(currentFirebaseUser.getUid()+"/"+songName.getText()+"/"+"recording1.aac"); // <-- Have to get song name here.
            storageReference.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "File Uploaded.", Toast.LENGTH_LONG).show();

                            //Database Stuff
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference();
                            String downloadUrl = storageReference.getDownloadUrl().toString();
                            myRef.child("Song_Name").child(String.valueOf(songName.getText())).setValue(downloadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int) progress + "% Uploaded ");
                        }
                    })
            ;
        }
        else{
            Toast.makeText(getApplicationContext(), "Cannot Upload. No File Found.", Toast.LENGTH_LONG).show();
        }
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void logout(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

}
