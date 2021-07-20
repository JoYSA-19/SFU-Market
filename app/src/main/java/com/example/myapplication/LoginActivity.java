package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Login Page
 */
public class LoginActivity extends AppCompatActivity {

    private String loginURL = "http://10.0.2.2:80/PHP-Backend/api/post/login.php";
    private EditText userEmail, userPassword;
    //TODO "remember me" feature
    private Button loginBtn, signUpBtn;
    private ProgressBar progressBar;
    private Boolean result = false;
    private Toast message;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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

        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        loginBtn.setOnClickListener(v -> {
            String sfu_id, password;
            sfu_id = String.valueOf(userEmail.getText());
            password = String.valueOf(userPassword.getText());

            if (sfu_id.equals("") || password.equals("")) {
                showMessage("All fields required");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[2];
                    field[0] = "sfu_id";
                    field[1] = "password";
                    //Creating array for data
                    String[] data = new String[2];
                    data[0] = sfu_id;
                    data[1] = password;
                    PutData putData = new PutData("http://10.0.2.2:80/PHP-Backend/api/post/login.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            progressBar.setVisibility(View.GONE);
                            String result = putData.getResult();
                            result = result.trim();
                            if (result.equals("Login Successful")) {
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showMessage(result);
                            }
                        }
                    }
                });
            }
        });
    }

    private void showMessage (String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
}