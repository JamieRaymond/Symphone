package com.example.jamier.symphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button recordSongButton;
    private Button browseSongsButton;
    private Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            logout();
        }

        recordSongButton = findViewById(R.id.create_song_button);
        recordSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecordSongActivity();
            }
        });

        browseSongsButton = findViewById(R.id.browse_songs_button);
        browseSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowseSongsActivity();
            }
        });

        logOutButton = findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void openRecordSongActivity() {
        Intent intent = new Intent(this, RecordSongActivity.class);
        startActivity(intent);

    }

    public void openBrowseSongsActivity() {
        Intent intent = new Intent(this, BrowseSongs.class);
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
