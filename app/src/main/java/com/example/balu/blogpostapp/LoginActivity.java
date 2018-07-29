package com.example.balu.blogpostapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText nameText,passText;
    private Button loginBtn,signupBtn;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth; //create a auth instance variable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nameText = (EditText) findViewById(R.id.nameText);
        passText = (EditText) findViewById(R.id.passText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        loginProgress = (ProgressBar)findViewById(R.id.loginProgress);
        mAuth = FirebaseAuth.getInstance(); //connect to firebase auth and creat an instance of it

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String pass = passText.getText().toString();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)){
                    loginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(name,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startMain();
                            }else Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                            loginProgress.setVisibility(View.INVISIBLE);

                        }
                    });
                }

            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser(); // get current logged in user details
        if (currentUser != null) {
            // User is signed in
            startMain();
        } else {
            // No user is signed in
        }
    }

    private void startMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

