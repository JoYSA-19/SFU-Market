package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * The home page including post button, which will open up a dialog for posting instantly
 */
public class MainActivity extends AppCompatActivity {

    //for server testing
    //private final String postURL = "http://35.183.197.126/PHP-Backend/api/post/create.php";
    //private final String feedURL = "http://35.183.197.126/PHP-Backend/api/post/feed.php";
    //private final String imageURL = "http://35.183.197.126/PHP-Backend/api/post/image.php";
    //for local testing
    private final String postURL = "http://10.0.2.2:80/PHP-Backend/api/post/create.php";
    private final String readURL = "http://10.0.2.2:80/PHP-Backend/api/post/feed.php";
    private final String imageURL = "http://10.0.2.2:80/PHP-Backend/api/post/image.php";

    private SessionManagement sessionManagement;
    private String currentPhotoPath;
    private Uri pickedImgUri;
    private static final int UPLOAD_CODE = 1;
    private static final int CAMERA_CODE = 2;
    private static final int REQUEST_CODE = 3;

    private MediaType png;
    private EditText nameText, descriptionText, priceText;
    private ImageView imageView;

    private ImageView des_image,des_profile;
    private TextView des_title, des_contact, des_price, des_des;
    private int currentPosition;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManagement = new SessionManagement(MainActivity.this);
        findViewById(R.id.btnMakePost).setOnClickListener(view -> handlePostDialog());

        des_image = findViewById(R.id.des_image);
        des_image.setOnClickListener(v -> {
            //TODO: Click on the image, call show_image_dialogue
        });
        des_profile = findViewById(R.id.des_profile);
        des_title = findViewById(R.id.des_title);
        des_contact = findViewById(R.id.des_contact);
        des_price = findViewById(R.id.des_price);
        des_des = findViewById(R.id.des_des);
        currentPosition = 0;
        findViewById(R.id.btnPreviousPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePostInfo(--currentPosition);
            }
        });
        findViewById(R.id.btnNextPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePostInfo(++currentPosition);
            }
        });
        getPostInfo();

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set feed selected
        bottomNavigationView.setSelectedItemId(R.id.feed);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.feed:
                        return true;
                }
                return false;
            }
        });
    }

    /**
     *  Shows the dialog and allows the user to enter information:
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
        nameText = view.findViewById(R.id.inputName);
        descriptionText = view.findViewById(R.id.inputDescription);
        priceText = view.findViewById(R.id.inputPrice);

        postBtn.setOnClickListener(v -> makePost(builder));
        cameraBtn.setOnClickListener(view1 -> takePhoto());
        uploadBtn.setOnClickListener(v -> checkAndRequestForPermission());
    }
    /**
     * The method makePost will make a post by getting user input and send to the back-end
     * @param builder AlertDialog
     */
    private void makePost(AlertDialog builder) {
        //Get values from text field variables
        String textbook_name = nameText.getText().toString();
        String description_text = descriptionText.getText().toString();
        String suggested_price = priceText.getText().toString();

        //Check if all the fields have been filled in
        if(checkPostValidity(textbook_name, description_text, suggested_price)) {
            //Start a new thread to execute HTTP request
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Post data to server
                    try {
                        doPostRequest(postURL, textbook_name, suggested_price, description_text);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //Close the data_entry_dialog
            builder.dismiss();
        }
    }

    private void getPostInfo() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(readURL)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    String mMessage = e.getMessage();
                    Log.w("Failure Feed Response", mMessage);
                    //call.cancel();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String myResponse;
                    postList = new ArrayList<>();
                    try {
                        myResponse = Objects.requireNonNull(response.body()).string();
                        JSONArray postArray = new JSONArray(myResponse);
                        for(int i = 0; i < postArray.length(); i++){
                            JSONObject newPost = postArray.getJSONObject(i);
                            Post post = new Post(newPost.getInt("id"),newPost.getString("sfu_id"),
                                    newPost.getString("textbook_name"),newPost.getDouble("suggested_price"),
                                    null,newPost.getString("description_text"),
                                    newPost.getString("post_date"), newPost.getString("first_name"), newPost.getString("last_name"),
                                    newPost.getString("phone_number"));
                            postList.add(post);
                            getImageRequest(imageURL, newPost.getString("photo_filepath"), i);
                        }
                        updatePostInfo(0);
                    } catch (JSONException | InterruptedException e){
                        String mMessage = e.getMessage();
                        Log.w("JSON Failure", mMessage);
                    }
                }
            });
        }).start();
    }

    private void updatePostInfo(int position) {
        //Bitmap bitmap = BitmapFactory.decodeFile(postList.get(position).getPhoto_filepath());
        //des_image.setImageBitmap(bitmap);
        if (postList.size() > 0) {
            if (position == postList.size()) {
                position = 0;
                currentPosition = position;
                //showMessage("Current position: 0").show();
            }

            if (position < 0) {
                position = postList.size() - 1;
                currentPosition = position;
                //showMessage("Current position: " + Integer.toString(currentPosition)).show();
            }
            int finalPosition = position;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Post item = postList.get(finalPosition);
                    des_title.setText(item.getTextbook_name());
                    des_contact.setText("Name: " + item.getFirst_name() + " " + item.getLast_name() + "\n"
                            + "SFU ID: " + item.getSfu_id() + "\n"
                            + "Phone Number: " + item.getPhone_number());
                    String priceTag = "$ " + String.format(java.util.Locale.US, "%.2f", item.getSuggested_price());
                    des_image.setImageBitmap(item.getPhoto());
                    des_price.setText(priceTag);
                    des_des.setText(item.getDescription_text());
                }
            });
        }
    }


    /**
     * This function handles the post request when the post button is clicked
     */
    void doPostRequest(String url, String textbook_name, String suggested_price, String description_text) throws InterruptedException {
        //Get the file format of the image
        png = MediaType.parse(getContentResolver().getType(pickedImgUri));
        //Convert the Uri into byte[]
        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(pickedImgUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert iStream != null;
        byte[] inputData = getBytes(iStream);

        //Get uuid and sfu_id
        String sfu_id = sessionManagement.getSession();
        String uuid = sessionManagement.getUniqueID();

        //Create the http client
        OkHttpClient client = new OkHttpClient();
        //Setup the form data
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sfu_id", sfu_id)
                .addFormDataPart("uuid", uuid)
                .addFormDataPart("textbook_name", textbook_name)
                .addFormDataPart("suggested_price", suggested_price)
                .addFormDataPart("description_text", description_text)
                .addFormDataPart("file", getContentResolver().getType(pickedImgUri), RequestBody.create(png, inputData))
                .build();
        //Setup request to PHP script with form data
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        //Create client call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String mMessage = e.getMessage();
                Log.w("failure Response", mMessage);
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String mMessage = Objects.requireNonNull(response.body()).string();
                Log.e("success", mMessage);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    void getImageRequest(String url, String filepath, int index) throws InterruptedException {
        //Create the http client
        OkHttpClient client = new OkHttpClient();
        //Setup the form data
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("filepath", filepath)
                .build();
        //Setup request to PHP script with form data
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        //Create client call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String mMessage = e.getMessage();
                Log.w("failure Response", mMessage);
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if(response.isSuccessful()) {
                    postList.get(index).setPhoto(BitmapFactory.decodeStream(Objects.requireNonNull(response.body()).byteStream()));
                    Objects.requireNonNull(response.body()).close();
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
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
                showMessage("Please accept for required permission").show();
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
            pickedImgUri = data.getData();
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
     * @param suggestedPrice item price
     * @return true if all filled; false if at least one is empty
     */
    private boolean checkPostValidity (String itemName, String itemDescription, String suggestedPrice){
        boolean result = false;
        if(itemName.isEmpty())
            showMessage("All fields are required: Please enter the item name").show();
        else if(itemDescription.isEmpty())
            showMessage("All fields are required: Please enter the item description").show();
        else if(suggestedPrice.isEmpty())
            showMessage("All fields are required: Please add a suggested price").show();
        else
            result = true;
        return result;
    }

    //function used to convert the Uri into byte array
    //https://stackoverflow.com/questions/10296734/image-uri-to-bytesarray
    public byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while (true) {
            try {
                if ((len = inputStream.read(buffer)) == -1) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    //Helper function for displaying toast message
    private Toast showMessage (String text){
        return Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG);
    }

}