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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private String registerURL = "http://10.0.2.2:80/PHP-Backend/api/post/register.php";
    private EditText userEmail, userPassword, confirmPassword;
    private Button signUpBtn;
    private TextView loginTxt;
    private ProgressBar progressBar;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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
        //Prepare JSON file for login request
        JSONObject json = createJson(email, password);

        //Create login request
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Post JSON to server
                doRegisterRequest(registerURL, String.valueOf(json));
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

    void doRegisterRequest(String url, String json) {
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
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body().string();
                Log.e("success", mMessage);
                //Send to MainActivity
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
    }

    private void showMessage (String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
}