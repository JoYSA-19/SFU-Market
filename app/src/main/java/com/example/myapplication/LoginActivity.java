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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
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

    //for server testing
    private final String loginURL = "http://35.183.69.80/PHP-Backend/api/post/login.php";
    private final String sessionURL = "http://35.183.69.80/PHP-Backend/api/post/session.php";
    //for local testing
    //private final String loginURL = "http://10.0.2.2:80/PHP-Backend/api/post/login.php";
    //private final String sessionURL = "http://10.0.2.2:80/PHP-Backend/api/post/session.php";

    private EditText sfuId, userPassword;
    //TODO "remember me" feature
    private Button loginBtn, signUpBtn;
    private ProgressBar progressBar;
    private Boolean result = false;
    private Toast loginFail, missingFields, verificationText;
    private String sfu_id, password;
    private SessionManagement sessionManagement;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Declare each View element
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManagement = new SessionManagement(LoginActivity.this);

        sfuId = findViewById(R.id.inputEmail);
        userPassword = findViewById(R.id.inputPassword);
        loginBtn = findViewById(R.id.loginButton);
        signUpBtn = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //Predefine toast messages
        loginFail = showMessage("Incorrect SFU ID or password");
        verificationText = showMessage("An Email has been sent to your SFU ID for Account Validation");
        missingFields = showMessage("Please enter your SFU ID or password");

        loginBtn.setOnClickListener(v -> loginOnPress());
        signUpBtn.setOnClickListener(v -> registerOnPress());
    }

    /**
     * Login to the app. If previously logged in, the app will take the
     * user to the homepage directly.
     */
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences;

        //Check if the user is currently logged into an account
        String loggedInID = sessionManagement.getSession();
        String deviceID = sessionManagement.getUniqueID();

        if(loggedInID != null && deviceID != null) {
            JSONObject json = onStartJSON(loggedInID, deviceID);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Post JSON to server
                    doServerRequest(sessionURL, String.valueOf(json), "session");
                }
            }).start();
        } else {
            //Do nothing
        }
    }


    /**
     * Verify login
     */
    private void loginOnPress() {
        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.INVISIBLE);
        sfu_id = sfuId.getText().toString();
        password = userPassword.getText().toString();
        if (sfu_id.isEmpty() || password.isEmpty()) {
            missingFields.show();
            loginBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else signIn(sfu_id, password);
        if (!result) {
            progressBar.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Switch to registerActivity
     */
    private void registerOnPress() {
        Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(registerActivity);
    }

    /**
     * Prepares validation to backend
     * @param email user email
     * @param password password, hashed in the server
     */
    private void signIn(String email, String password) {
        //get UUID of current device
        String uuid = sessionManagement.getUniqueID();
        //Prepare JSON file for login request
        JSONObject json = logInJson(email, password, uuid);

        //Create login request
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Post JSON to server
                doServerRequest(loginURL, String.valueOf(json), "login");
            }
        }).start();
    }


    /**
     * Function prepares JSON file for validation
     * @param sfu_id sfu_id : (Ex. lsh14)
     * @param user_password password
     * @param uuid Cell phone id, for Remember Me function
     * @return LoginJSon
     */
    JSONObject logInJson(String sfu_id, String user_password, String uuid) {
        JSONObject json = new JSONObject();
        try {
            json.put("sfu_id", sfu_id);
            json.put("password", user_password);
            json.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Generate the JSON
     * @param sfu_id sfu_id : (Ex. lsh14)
     * @param uuid Cell phone id, for Remember Me function
     * @return OnStartJSon
     */
    JSONObject onStartJSON(String sfu_id, String uuid) {
        JSONObject json = new JSONObject();
        try {
            json.put("sfu_id", sfu_id);
            json.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Function interacts with backend to post
     * @param url Database address
     * @param json login JSON Data
     */
    void doServerRequest(String url, String json, String mode) {
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
                if(response.code() == 200) {
                    Log.d("Response", "200");
                    if(mode.equals("login")) {
                        result = true;
                        login();
                    }
                    if(mode.equals("session")) {
                        resumeSession();
                    }
                } else if(response.code() == 401) {
                    Log.d("Response", "401");
                    if(mode.equals("login")) {
                        verificationText.show();
                    }
                } else {
                    Log.d("Response", String.valueOf(response.code()));
                    if(mode.equals("login")) {
                        loginFail.show();
                    }
                }
            }
        });
    }

    /**
     * Save session, clears tasks and loads mainActivity
     */
    private void login() {
        sessionManagement.saveSession(sfu_id);

        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
    }

    /**
     * Resumes session, clears tasks and loads mainActivity
     */
    private void resumeSession() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
    }

    /**
     * Helper function for displaying toast message
     * @param text message
     * @return Toast message, use with .show()
     */
    private Toast showMessage(String text) {
        return Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG);
    }

}