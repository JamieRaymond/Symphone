package com.example.jamier.symphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button RecordSongbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecordSongbutton = (Button) findViewById(R.id.create_song_button);
        RecordSongbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecordSongActivity();
            }
        });
    }

    public void openRecordSongActivity() {
        Intent intent = new Intent(this, RecordSongActivity.class);
        startActivity(intent);

    }

}
