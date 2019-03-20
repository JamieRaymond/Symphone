package com.example.jamier.symphone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements OnClickListener{

    private EditText editTextEmail, editTextConfirmEmail, editTextPassword, editTextConfirmPassword, editTextFirstName, editTextLastName, editTextDob;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.email);
        editTextConfirmEmail = findViewById(R.id.confirmemail);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmpassword);
        editTextFirstName = findViewById(R.id.firstname);
        editTextLastName = findViewById(R.id.lastname);
        editTextDob = findViewById(R.id.dob);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.register_button).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser() != null) {
            //handle the already logged in user **HAVE TO DO**
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                registerUser();
                openLoginActivity();
                break;
        }
    }


    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String confirmEmail = editTextConfirmEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String dob = editTextDob.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email required");
            editTextEmail.requestFocus();
            return;
        }

        //Makes sure its a valid email;
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if(confirmEmail.isEmpty()){
            editTextConfirmEmail.setError("Confirmation email required");
            editTextConfirmEmail.requestFocus();
            return;
        }

        //Makes sure its a valid confirm email:
        if(!Patterns.EMAIL_ADDRESS.matcher(confirmEmail).matches()){
            editTextConfirmEmail.setError("Enter a valid email");
            editTextConfirmEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Password not long enough");
            editTextPassword.requestFocus();
            return;
        }
        if(confirmPassword.isEmpty()){
            editTextConfirmPassword.setError("Confirmation password required");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if(confirmPassword.length() < 6){
            editTextConfirmPassword.setError("Confirmation password is not long enough");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if(firstName.isEmpty()){
            editTextFirstName.setError("First name required");
            editTextFirstName.requestFocus();
            return;
        }
        if(lastName.isEmpty()){
            editTextLastName.setError("Last name required");
            editTextLastName.requestFocus();
            return;
        }
        if(dob.isEmpty()){
            editTextDob.setError("Date of birth required");
            editTextDob.requestFocus();
            return;
        }
        //Validation is okay
        //Showing progress...

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user = new User(
                                    email,
                                    firstName,
                                    lastName,
                                    dob
                            );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
