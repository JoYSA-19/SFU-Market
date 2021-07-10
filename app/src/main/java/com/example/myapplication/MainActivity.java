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
import android.widget.EditText;
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

    private EditText nameInput, descriptionInput, contactInput, priceInput;
    private ImageView imageView;

    private PostInformation userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        findViewById(R.id.btnMakePost).setOnClickListener(view -> handlePostDialog());
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
        Button postBtn = view.findViewById(R.id.btnPost);
        ImageButton cameraBtn = view.findViewById(R.id.cameraButton);
        ImageButton uploadBtn = view.findViewById(R.id.uploadImageButton);
        imageView = view.findViewById(R.id.imageView);
        nameInput = view.findViewById(R.id.inputName);
        descriptionInput = view.findViewById(R.id.inputDescription);
        contactInput = view.findViewById(R.id.textContact);
        priceInput = view.findViewById(R.id.inputPrice);

        userInfo = new PostInformation();

        postBtn.setOnClickListener(v -> makePost(builder));
        cameraBtn.setOnClickListener(view1 -> takePhoto());
        uploadBtn.setOnClickListener(v -> checkAndRequestForPermission());
    }


    /**
     * The method makePost will make a post by getting user input and send to the back-end
     * @param builder AlertDialog
     */
    private void makePost(AlertDialog builder) {
        String itemPrice; // empty value
        String itemName;
        String itemDescription;
        String contactInfo;
        itemName = nameInput.getText().toString();
        itemDescription = descriptionInput.getText().toString();
        contactInfo = contactInput.getText().toString();
        itemPrice = priceInput.getText().toString();
        if(!checkPostValidity(itemName,itemDescription,contactInfo,itemPrice))
            return;
        // Only for Testing
        System.out.println("Name: " + itemName);
        System.out.println("Des: " + itemDescription);
        System.out.println("contact: " + contactInfo);
        System.out.println("price: " + itemPrice);

        if(pickedImgUri == null){
            showMessage("All fields are required: Please include a photo");
            return;
        }
        System.out.println(pickedImgUri.toString());

        userInfo.price = Float.parseFloat(itemPrice);
        // Don't have to catch NumberFormatException because the EditText is already specified as "input float"
        if(userInfo.price < 0){
            showMessage("Negative value: Please enter a valid value");
            return;
        }
        userInfo.name = itemName;
        userInfo.description = itemDescription;
        userInfo.contact = contactInfo;
        userInfo.imageUri = pickedImgUri;

        // Pass values to back-end
        Call<Void> call = retrofitInterface.executePost(userInfo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                if (response.code() == 200) {
                    Toast.makeText(MainActivity.this,
                            "Post Created Successfully", Toast.LENGTH_LONG).show();
                    builder.dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        builder.dismiss(); //Please comment this line if testing on back-end

    }

    /**
     * This function will take the user to the camera and take a photo of their item.
     * With permission given.
     */
    private void takePhoto() {
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

    /**
     * Ask permission from the user, once approved, open the gallery
     */
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showMessage("Please accept for required permission");
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

    /**
     * Check if the input values are empty.
     * @param itemName item name
     * @param itemDescription item description
     * @param contactInfo contact information
     * @param itemPrice item price
     * @return true if all filled; false if at least one is empty
     */
    private boolean checkPostValidity (String itemName, String itemDescription, String contactInfo, String itemPrice){
        boolean result = false;
        if(itemName.isEmpty())
            showMessage("All fields are required: Please enter the item name");
        else if(itemDescription.isEmpty())
            showMessage("All fields are required: Please enter the item description");
        else if(itemPrice.isEmpty())
            showMessage("All fields are required: Please check the price field");
        else if(contactInfo.isEmpty())
            showMessage("All fields are required: Please leave your contact information");
        else
            result = true;
        return result;
    }

    /**
     * Create a Toast message at the bottom of the screen
     * @param text message
     */
    private void showMessage (String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

}