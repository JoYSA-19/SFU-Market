package com.example.myapplication;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * The home page including post button, which will open up a dialog for posting instantly
 */
public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private final String BASE_URL = "http://10.0.2.2:3000";
    private String currentPhotoPath;
    private Uri pickedImgUri = null;
    private static final int UPLOAD_CODE = 1 ;
    private static final int CAMERA_CODE = 2 ;
    private static final int REQUEST_CODE = 3 ;

    Button postBtn;
    ImageButton cameraBtn,uploadBtn;
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

    /**
     * Shows the dialog and allows the user to enter information:
     *  item name, item description, photo and contact information
     */
    private void handlePostDialog() {
        View view = getLayoutInflater().inflate(R.layout.data_entry_dialog, null);
        AlertDialog builder = new AlertDialog.Builder(this).create();

        builder.setView(view);
        builder.show();

        //assigning text fields and buttons to a variable
        postBtn = view.findViewById(R.id.btnPost);
        cameraBtn = view.findViewById(R.id.cameraButton);
        uploadBtn = view.findViewById(R.id.uploadImageButton);
        imageView = view.findViewById(R.id.imageView);

        String itemName = view.findViewById(R.id.textName).toString();
        String itemDescription = view.findViewById(R.id.textDescription).toString();
        String textContact = view.findViewById(R.id.textContact).toString();
        int intPrice = view.fiewViewById(R.id.);
        PostInformation userInfo = new PostInformation();
        userInfo.name = itemName;
        userInfo.description = itemDescription;
        userInfo.contact = textContact;
        userInfo.price = intPrice;

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = retrofitInterface.executePost(userInfo);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(MainActivity.this,
                                    "Post Created Successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
                builder.dismiss();

            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String fileName = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                try {
                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();

                    pickedImgUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.example.myapplication.fileprovider",imageFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,pickedImgUri);

                    startActivityForResult(intent, CAMERA_CODE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });
    }

    /**
     * Ask permission from the user, once approved, open the gallery
     */
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
        else {
            // everything goes well : we have permission to access user gallery, open the gallery
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, UPLOAD_CODE);
        }
    }

    /**
     * After user picked an image, get the image and show it in the preview
     * @param requestCode request code
     * @param resultCode User's choice, whether Camera or Upload
     * @param data image data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UPLOAD_CODE && data != null ) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            imageView.setImageURI(pickedImgUri);
        }
        else if(resultCode == RESULT_OK && requestCode == CAMERA_CODE && data != null ){
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(bitmap);
        }
    }

}