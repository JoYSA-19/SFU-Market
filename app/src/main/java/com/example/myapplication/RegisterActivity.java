package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword, confirmPassword;
    private Button signUpBtn;
    private TextView loginTxt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.inputEmail);
        userPassword = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        signUpBtn = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.loginProgressBar);
        loginTxt = findViewById(R.id.loginText);

        progressBar.setVisibility(View.INVISIBLE);
        signUpBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.INVISIBLE);
            final String email = userEmail.getText().toString();
            final String password = userPassword.getText().toString();
            final String confPassword = confirmPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()){
                showMessage("Please enter your user email address or password");
                signUpBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (!password.equals(confPassword)) {
                showMessage("Passwords do not match");
                signUpBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else
                signUp(email,password);

        });
        //Switch to LoginActivity
        loginTxt.setOnClickListener(view -> {
            Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginIntent);
        });
    }
    private void signUp(String email, String password) {
        Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(loginActivity);
        finish();
        //TODO Sign up process
    }
    private void showMessage (String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
}