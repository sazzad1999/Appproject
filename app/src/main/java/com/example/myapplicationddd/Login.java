package com.example.myapplicationddd;

import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;


import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private Button loginButton;
    private TextView forgotPassword, gotoSignup;


    private EditText edtEmail, edtPassword;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginButton = findViewById(R.id.signinButton);
        gotoSignup = findViewById(R.id.gotoSignupId);
        forgotPassword = findViewById(R.id.forgotPassword);

        loginButton.setOnClickListener(this);
        gotoSignup.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);


        edtEmail = findViewById(R.id.emailId);
        edtPassword = findViewById(R.id.passwordId);


        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


    }


    private void login() {

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (email.isEmpty()) {
            edtEmail.setError("email required");
            edtEmail.requestFocus();
            return;
        } else if (password.isEmpty()) {
            edtPassword.setError("email required");
            edtPassword.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Valid email required");
            edtEmail.requestFocus();
            return;
        } else if (password.length() < 6) {
            edtPassword.setError("password should be at least 6 charcter");
            edtPassword.requestFocus();
            return;
        } else {
            progressDialog.setMessage("Wait while login");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        nextActivity();

                       /* if (user.isEmailVerified()) {
                        } else {
                            user.sendEmailVerification();
                            mAuth.signOut();

                            Toast.makeText(Login.this, "Email is not verified", Toast.LENGTH_SHORT).show();
                        }*/
                    } else {
                        progressDialog.dismiss();
                        String s = "" + task.getException();
                        if (s.length() == 137) {
                            Toast.makeText(Login.this, "Network connection needed", Toast.LENGTH_SHORT).show();
                        } else if (s.length() == 127) {
                            Toast.makeText(Login.this, "Password invalid", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            Intent i = new Intent(Login.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void nextActivity() {
        Intent i = new Intent(Login.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    @Override
    public void onClick(View v) {

        if(v.getId() ==R.id.signinButton ) {
            login();
        }
        if(v.getId() == R.id.gotoSignupId){
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);

        }

    }
}