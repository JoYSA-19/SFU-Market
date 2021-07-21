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

    private String registerURL = "http://35.183.197.126/PHP-Backend/api/post/register.php";
    private EditText userId, userPassword, confirmPassword, firstName, lastName, phoneNumber;
    private Button signUpBtn;
    private TextView loginTxt;
    private ProgressBar progressBar;
    private Boolean result = false;
    private Toast message;
    boolean upperCase, lowerCase, number, specialChar = false;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userId = findViewById(R.id.inputEmail);
        userPassword = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        signUpBtn = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.loginProgressBar);
        loginTxt = findViewById(R.id.loginText);

        progressBar.setVisibility(View.INVISIBLE);
        signUpBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.INVISIBLE);
            final String sfu_id = userId.getText().toString();
            final String password = userPassword.getText().toString();
            final String confPassword = confirmPassword.getText().toString();
            final String first_name = firstName.getText().toString();
            final String last_name = lastName.getText().toString();
            final String phone_number = phoneNumber.getText().toString();
            message = showMessage("Account Already Exists");

            if (sfu_id.isEmpty() || password.isEmpty() || confPassword.isEmpty() || first_name.isEmpty() || last_name.isEmpty() || phone_number.isEmpty()){
                showMessage("All fields required").show();
                signUpBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (!passwordChecker(password)) {
                showMessage("Minimum Complexity Password not met!\n" +
                        "Password must contain:\n" +
                        "1 upper case letter\n" +
                        "1 lower case letter\n" +
                        "1 special character\n" +
                        "1 number\n" +
                        "at least 8 characters long").show();
                signUpBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (!password.equals(confPassword)) {
                showMessage("Passwords do not match").show();
                signUpBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {
                signUp(sfu_id, password, first_name, last_name, phone_number);

                if(!result) {
                    progressBar.setVisibility(View.INVISIBLE);
                    signUpBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        //Switch to LoginActivity
        loginTxt.setOnClickListener(view -> {
            Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginIntent);
        });
    }
    private void signUp(String sfu_id, String password, String first_name, String last_name, String phone_number) {
        //Prepare JSON file for login request
        JSONObject json = createJson(sfu_id, password, first_name, last_name, phone_number);

        //Create login request
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Post JSON to server
                doRegisterRequest(registerURL, String.valueOf(json));
            }
        }).start();
    }

    JSONObject createJson(String sfu_id, String user_password, String first_name, String last_name, String phone_number) {
        JSONObject json = new JSONObject();
        try {
            json.put("sfu_id", sfu_id);
            json.put("password", user_password);
            json.put("first_name", first_name);
            json.put("last_name", last_name);
            json.put("phone_number", phone_number);
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
                if(response.isSuccessful()) {
                    result = true;
                    Log.d("Response", "200");
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                } else {
                    Log.d("Response", String.valueOf(response.code()));
                    message.show();
                }
            }
        });
    }

    private boolean passwordChecker(String password) {
        if (password.length() <= 8) {
            return false;
        }
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (!specialChar && !Character.isLetterOrDigit(ch)) {
                specialChar = true;
            } else {
                if (!number && Character.isDigit(ch)) {
                    number = true;
                } else {
                    if (!upperCase || !lowerCase) {
                        if (Character.isUpperCase(ch)) {
                            upperCase = true;
                        }
                        if (Character.isLowerCase(ch)) {
                            lowerCase = true;
                        }
                    }
                }
            }
        }
        if ((upperCase && lowerCase && specialChar && number)) {
            return true;
        } else {
            return false;
        }
    }

    private Toast showMessage (String text) {
        return Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
    }
}