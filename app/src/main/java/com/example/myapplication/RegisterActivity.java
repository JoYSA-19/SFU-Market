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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Registration Page
 */
public class RegisterActivity extends AppCompatActivity {

    //for server testing
    // String registerURL = "http://35.183.197.126/PHP-Backend/api/post/register.php";
    //for local testing
    private String registerURL = "http://10.0.2.2:80/PHP-Backend/api/post/register.php";

    private EditText userId, userPassword, confirmPassword, firstName, lastName, phoneNumber;
    private Button signUpBtn;
    private TextView loginTxt;
    private ProgressBar progressBar;
    private Boolean result = false;
    private Toast accountExists, missingFields, passwordComplexity, passwordMatch;
    private boolean upperCase, lowerCase, number, specialChar = false;

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
        loginTxt = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //Predefine toast messages
        accountExists = showMessage("Account Already Exists");
        missingFields = showMessage("All fields required");
        passwordComplexity = showMessage("Minimum Complexity Password not met!\n" +
                "Password must contain:\n" +
                "1 upper case letter\n" +
                "1 lower case letter\n" +
                "1 special character\n" +
                "1 number\n" +
                "at least 8 characters long");
        passwordMatch = showMessage("Passwords do not match");

        signUpBtn.setOnClickListener(v -> signupOnPress());
        loginTxt.setOnClickListener(view -> loginOnPress());
    }

    //Verify Sign up
    private void signupOnPress() {
        //Start loading bar on sign up button
        signUpBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        //Finalize currently inputted values into string
        final String sfu_id = userId.getText().toString();
        final String password = userPassword.getText().toString();
        final String confPassword = confirmPassword.getText().toString();
        final String first_name = firstName.getText().toString();
        final String last_name = lastName.getText().toString();
        final String phone_number = phoneNumber.getText().toString();

        //Check if form is valid
        if(!signUpValidator(sfu_id, password, confPassword, first_name, last_name, phone_number)) {
            signUpBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            signUp(sfu_id, password, first_name, last_name, phone_number);
            //Show signup button again if sign up failed
            if(!result) {
                signUpBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    //Switches to loginActivity
    private void loginOnPress() {
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(loginIntent);
    }

    //Checks if the form is valid
    private boolean signUpValidator(String sfu_id, String password, String confPassword, String first_name, String last_name, String phone_number) {
        //Check for empty fields
        if (sfu_id.isEmpty() || password.isEmpty() || confPassword.isEmpty() || first_name.isEmpty() || last_name.isEmpty() || phone_number.isEmpty()) {
            missingFields.show();
            return false;
        }
        //Verify that the passwords match
        else if (!password.equals(confPassword)) {
            passwordMatch.show();
            return false;
        }
        //Verify if the password complexity meets requirements
        else if (!passwordChecker(password)) {
            passwordComplexity.show();
            return false;
        }
        else {
            return true;
        }
    }

    //Function posts data to backend
    private void signUp(String sfu_id, String password, String first_name, String last_name, String phone_number) {
        //Prepare JSON file for sign up request
        JSONObject json = createJson(sfu_id, password, first_name, last_name, phone_number);
        //Creates sign up request
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Post JSON to server
                doRegisterRequest(registerURL, String.valueOf(json));
            }
        }).start();
    }
    //Function prepares JSON file for post
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
    /**
     * Function interacts with backend to post
     * @param url Database address
     * @param json Registration JSON Data
     */
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
                    Log.d("Response", String.valueOf(response.code()));
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                } else {
                    Log.d("Response", String.valueOf(response.code()));
                    accountExists.show();
                }
            }
        });
    }
    //Minimum password complexity; checks if requirements for password is met
    //Source: https://ssaurel.medium.com/develop-a-password-strength-calculator-application-for-android-de3711ba7959
    private boolean passwordChecker(String password) {
        if (password.length() <= 8) {
            return false; //password length is too short
        }
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            //determines if a special character is present
            if (!specialChar && !Character.isLetterOrDigit(ch)) {
                specialChar = true;
            } else {
                //determines if a number is present
                if (!number && Character.isDigit(ch)) {
                    number = true;
                } else {
                    //determines if a letter is present and which type of letter (upper or lower)
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
        //once all the conditions are met, return true
        if ((upperCase && lowerCase && specialChar && number)) {
            return true;
        } else {
            return false;
        }
    }
    //Helper function for displaying toast message
    private Toast showMessage (String text) {
        return Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
    }
}
