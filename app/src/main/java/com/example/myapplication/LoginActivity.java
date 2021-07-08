package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Login Page
 */
public class LoginActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    //TODO "remember me" feature
    private Button loginBtn, signUpBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.inputEmail);
        userPassword = findViewById(R.id.inputPassword);
        loginBtn = findViewById(R.id.loginButton);
        signUpBtn = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.loginProgressBar);

        progressBar.setVisibility(View.INVISIBLE);

        loginBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);
            final String email = userEmail.getText().toString();
            final String password = userPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()){
                showMessage("Please enter your user email address or password");
                loginBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else
                signIn(email,password);

        });

        //Switch to RegisterActivity
        signUpBtn.setOnClickListener(v -> {
            Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(registerActivity);
            finish();
        });

    }

    private void signIn(String email, String password) {
        //TODO Sign in process
    }

    private void showMessage (String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
}