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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Login Page
 */
public class LoginActivity extends AppCompatActivity {

    private String loginURL = "http://10.0.2.2:81/PHP-Backend/api/post/login.php";
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

        loginBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);
            final String email = userEmail.getText().toString();
            final String password = userPassword.getText().toString();
            message = showMessage("Incorrect SFU ID or password");
            if (email.isEmpty() || password.isEmpty()){
                showMessage("Please enter your SFU ID or password").show();
                loginBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else
                signIn(email,password);

            if(!result) {
                progressBar.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
            }
        });

        //Switch to RegisterActivity
        signUpBtn.setOnClickListener(v -> {
            Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(registerActivity);
            finish();
        });

    }

    private void signIn(String email, String password) {
        //Prepare JSON file for login request
        JSONObject json = createJson(email, password);

        //Create login request
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Post JSON to server
                doLoginRequest(loginURL, String.valueOf(json));
            }
        }).start();
    }

    JSONObject createJson(String sfu_id, String user_password) {
        JSONObject json = new JSONObject();
        try {
            json.put("sfu_id", sfu_id);
            json.put("password", user_password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    void doLoginRequest(String url, String json) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage();
                Log.w("Failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    result = true;
                    Log.d("Response", "200");
                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else {
                    Log.d("Response", String.valueOf(response.code()));
                    message.show();
                }
            }
        });
    }

    private Toast showMessage(String text) {
        return Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG);
    }
}