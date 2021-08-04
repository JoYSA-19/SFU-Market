package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    //for server testing
    private final String settingsURL = "http://35.183.69.80/PHP-Backend/api/post/settings.php";
    private final String logOutURL = "http://35.183.69.80/PHP-Backend/api/post/logout.php";
    //for local testing
    //private final String settingsURL = "http://10.0.2.2:80/PHP-Backend/api/post/settings.php";
    //private final String logOutURL = "http://10.0.2.2:80/PHP-Backend/api/post/logout.php";

    private TextView show_first_name, show_last_name, show_user_id, show_phone_number;
    private String firstName, lastName, sfuId, phoneNumber;
    private SessionManagement sessionManagement;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sessionManagement = new SessionManagement(SettingsActivity.this);

        Button signOutButton = findViewById(R.id.signOutButton);

        show_first_name = findViewById(R.id.show_first_name);
        show_last_name = findViewById(R.id.show_last_name);
        show_user_id = findViewById(R.id.show_user_id);
        show_phone_number = findViewById(R.id.show_phone_number);

        signOutButton.setOnClickListener(v -> signOut());

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set feed selected
        bottomNavigationView.setSelectedItemId(R.id.settings);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.feed:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences;

        //Check if the user is currently logged into an account
        String loggedInID = sessionManagement.getSession();
        String deviceID = sessionManagement.getUniqueID();

        if(loggedInID != null && deviceID != null) {
            JSONObject json = getInfoJSON(loggedInID, deviceID);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Post JSON to server
                    doSettingsRequest(settingsURL, String.valueOf(json));
                }
            }).start();
        }
    }

    private void signOut() {
        //Remove sfu_id from SharedPreferences
        SessionManagement sessionManagement = new SessionManagement(SettingsActivity.this);
        sessionManagement.endSession();

        //Sign out in backend
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Call server
                doLogOutRequest(logOutURL, sessionManagement.getUniqueID());
            }
        }).start();

        //Take back to login page
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginActivity);
    }

    void doLogOutRequest(String url, String uuid) {
        RequestBody body = new FormBody.Builder()
                .add("uuid", uuid)
                .build();
        //Create the http client
        OkHttpClient client = new OkHttpClient();
        //Call database to sign out
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        //Create client call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String mMessage = e.getMessage();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String mMessage = Objects.requireNonNull(response.body()).string();
                    Log.e("Signed Out", String.valueOf(response.code()));
                }
                else {
                    Log.e("Log Out Failed", String.valueOf(response.code()));
                }
            }
        });
    }

    private void doSettingsRequest(String url, String json) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String mMessage = e.getMessage();
                Log.w("Failure Response", mMessage);
                //call.cancel();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() == 200) {
                    Log.d("Response", "200");
                    JSONObject result = null;
                    try {
                        result = new JSONObject(Objects.requireNonNull(response.body()).string());
                        firstName = result.getString("first_name");
                        lastName = result.getString("last_name");
                        sfuId = result.getString("sfu_id");
                        phoneNumber = result.getString("phone_number");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setText();
                        }
                    });
                } else {
                    Log.d("Response", String.valueOf(response.code()));

                }
            }
        });
    }

    private JSONObject getInfoJSON(String sfu_id, String uuid) {
        JSONObject json = new JSONObject();
        try {
            json.put("sfu_id", sfu_id);
            json.put("uuid", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void setText() {
        show_first_name.setText("First Name: " + firstName);
        show_last_name.setText("Last Name: " + lastName);
        show_user_id.setText("SFU ID: " + sfuId);
        show_phone_number.setText("Phone Number: " + phoneNumber);
    }
}



