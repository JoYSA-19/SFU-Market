package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        //FloatingActionButton btnMakePost = (FloatingActionButton) findViewById(R.id.btnMakePost);
        //if (btnMakePost == null) throw new AssertionError();
        //btnMakePost.setOnClickListener(view -> showDialog());

        findViewById(R.id.btnMakePost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePostDialog();
            }
        });
    }

    private void handlePostDialog() {
        View view = getLayoutInflater().inflate(R.layout.data_entry_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view).show();

        Button postBtn = view.findViewById(R.id.btnPost);
        EditText itemName = view.findViewById(R.id.textName);
        EditText itemDescription = view.findViewById(R.id.textDescription);
        EditText textContact = view.findViewById(R.id.textContact);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<Void> call = retrofitInterface.executePost(itemName, itemDescription, textContact);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(MainActivity.this,
                                    "Post Created Successfully", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
/**
    private void showDialog(){
        PostEntryDialog dialog = new PostEntryDialog();
        dialog.show(getSupportFragmentManager(),"DIALOG_FRAGMENT");
    }
 */
}