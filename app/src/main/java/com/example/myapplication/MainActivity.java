package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";
    private String currentPhotoPath;
    private Uri pickedImgUri = null;
    private static final int PReqCode = 2 ;
    private static final int REQUESTCODE = 1 ;

    Button postBtn;
    ImageButton uploadBtn;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        findViewById(R.id.btnMakePost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePostDialog();
            }
        });
    }

    private void handlePostDialog() {
        View view = getLayoutInflater().inflate(R.layout.data_entry_dialog, null);

        AlertDialog builder = new AlertDialog.Builder(this).create();

        builder.setView(view);
        builder.show();

        //assigning text fields and buttons to a variable
        postBtn = view.findViewById(R.id.btnPost);
        imageView = view.findViewById(R.id.imageView);
        uploadBtn = view.findViewById(R.id.uploadImageButton);

        String itemName = view.findViewById(R.id.textName).toString();
        String itemDescription = view.findViewById(R.id.textDescription).toString();
        String textContact = view.findViewById(R.id.textContact).toString();
        PostInformation userInfo = new PostInformation();
        userInfo.name = itemName;
        userInfo.description = itemDescription;
        userInfo.contact = textContact;

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<Void> call = retrofitInterface.executePost(userInfo);

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

                builder.dismiss();

            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }

        });
    }
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();
    }





    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }



    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            imageView.setImageURI(pickedImgUri);

        }


    }
//            @Override
//            public void onClick(View view) {
//                String fileName = "photo";
//                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
//                try {
//                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
//                    currentPhotoPath = imageFile.getAbsolutePath();
//
//                    Uri imageUri = FileProvider.getUriForFile(MainActivity.this,
//                            "com.example.myapplication.fileprovider",imageFile);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//
//                    startActivityForResult(intent, REQUESTCODE);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        ImageView imageView = findViewById(R.id.imageView);
//        Bitmap photo = null;
//        if (data != null) {
//            photo = (Bitmap) data.getExtras().get(currentPhotoPath);
//        }
//        imageView.setImageBitmap(photo);
////        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
////            ImageView imageView = findViewById(R.id.imageView);
////            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
////            if(bitmap == null){
////                BitmapFactory.Options options = new BitmapFactory.Options();
////                options.inSampleSize = 2;
////                bitmap = BitmapFactory.decodeFile(currentPhotoPath,options);
////            }
////            imageView.setImageBitmap(bitmap);
////
////        }
//    }
}