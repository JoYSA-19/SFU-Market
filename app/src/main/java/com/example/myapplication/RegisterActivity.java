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
import android.widget.TextView;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private String registerURL = "http://10.0.2.2:80/PHP-Backend/api/post/register.php";
    private EditText userEmail, userPassword, confirmPassword, firstName, lastName, phoneNumber;
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
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        signUpBtn = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        loginTxt = findViewById(R.id.loginText);

        loginTxt.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signUpBtn.setOnClickListener(v -> {
            String sfu_id, last_name, first_name, phone_number, password, confPassword;
            first_name = String.valueOf(firstName.getText());
            last_name = String.valueOf(lastName.getText());
            sfu_id = String.valueOf(userEmail.getText());
            phone_number = String.valueOf(phoneNumber.getText());
            password = String.valueOf(userPassword.getText());
            confPassword = String.valueOf(confirmPassword.getText());

            if (first_name.equals("") || last_name.equals("") || sfu_id.equals("") || phone_number.equals("") || password.equals("")) {
                showMessage("All fields required");
            } else if (!password.equals(confPassword)) {
                showMessage("Passwords do not match");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[5];
                    field[0] = "sfu_id";
                    field[1] = "password";
                    field[2] = "first_name";
                    field[3] = "last_name";
                    field[4] = "phone_number";
                    //Creating array for data
                    String[] data = new String[5];
                    data[0] = sfu_id;
                    data[1] = password;
                    data[2] = first_name;
                    data[3] = last_name;
                    data[4] = phone_number;
                    PutData putData = new PutData("http://10.0.2.2:80/PHP-Backend/api/post/register.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            progressBar.setVisibility(View.GONE);
                            String result = putData.getResult();
                            result = result.trim();
                            if (result.equals("Sign Up Success")) {
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
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