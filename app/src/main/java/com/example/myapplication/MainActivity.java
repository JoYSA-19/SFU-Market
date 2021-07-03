package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton btnMakePost = (FloatingActionButton) findViewById(R.id.btnMakePost);
        if (btnMakePost == null) throw new AssertionError();
        btnMakePost.setOnClickListener(view -> showDialog());
    }

    private void showDialog(){
        PostEntryDialog dialog = new PostEntryDialog();
        dialog.show(getSupportFragmentManager(),"DIALOG_FRAGMENT");
    }
}