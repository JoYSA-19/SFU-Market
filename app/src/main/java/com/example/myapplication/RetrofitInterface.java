package com.example.myapplication;

import android.widget.EditText;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/btnPost")
    Call<Void> executePost(@Body PostInformation userInfo);



}
