package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    //for server testing
    //private String settingsURL = "http://35.183.197.126/PHP-Backend/api/post/settings.php";
    //for local testing
    private String settingsURL = "http://10.0.2.2:80/PHP-Backend/api/post/settings.php";

    private TextView show_first_name, show_last_name, show_user_id, show_phone_number;
    private String firstName, lastName, sfuId, phoneNumber;
    private SessionManagement sessionManagement;

    private Button backButton;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sessionManagement = new SessionManagement(SettingsActivity.this);

        backButton = findViewById(R.id.backButton);

        show_first_name = findViewById(R.id.show_first_name);
        show_last_name = findViewById(R.id.show_last_name);
        show_user_id = findViewById(R.id.show_user_id);
        show_phone_number = findViewById(R.id.show_phone_number);

        backButton.setOnClickListener(v -> back());
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

    private void back() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
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
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage();
                Log.w("Failure Response", mMessage);
                //call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200) {
                    Log.d("Response", "200");
                    Log.d("Inside Response", Objects.requireNonNull(response.body()).string());
                    parseJSON(Objects.requireNonNull(response.body()).string());
                    setText();
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

    private void parseJSON(String json) {
        try {
            JSONObject result = new JSONObject(json);
            firstName = result.getString("first_name");
            lastName = result.getString("last_name");
            sfuId = result.getString("sfu_id");
            phoneNumber = result.getString("phone_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setText() {
        show_first_name.setText("First Name: " + firstName);
        show_last_name.setText("Last Name: " + lastName);
        show_user_id.setText("SFU ID: " + sfuId);
        show_phone_number.setText("Phone Number: " + phoneNumber);
    }
}



