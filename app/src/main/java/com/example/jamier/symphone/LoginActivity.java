package com.example.jamier.symphone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public EditText loginEmailId, logInPass;
    Button buttonLogIn;
    TextView signUp;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        loginEmailId = findViewById(R.id.loginEmail);
        logInPass = findViewById(R.id.loginpaswd);
        buttonLogIn = findViewById(R.id.btnLogIn);
        signUp = findViewById(R.id.TVSignIn);


            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(LoginActivity.this, "User logged in ", Toast.LENGTH_SHORT).show();
                        Intent I = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(I);
                    } else {
                        Toast.makeText(LoginActivity.this, "Login to continue", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent I = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(I);
                }
            });
            buttonLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //final ProgressDialog progressDialog = new ProgressDialog(this);
                    //progressDialog.setTitle("Logging In...");
                    //progressDialog.show();

                    String userEmail = loginEmailId.getText().toString();
                    String userPaswd = logInPass.getText().toString();
                    if (userEmail.isEmpty()) {
                        loginEmailId.setError("Provide your Email first!");
                        loginEmailId.requestFocus();
                    } else if (userPaswd.isEmpty()) {
                        logInPass.setError("Enter Password!");
                        logInPass.requestFocus();
                    } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                    } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                        firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Error - Could not Login", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            }
                        });

                    } else {
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}