package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * The home page including post button, which will open up a dialog for posting instantly
 */
public class MainActivity extends AppCompatActivity {

    private String URL = "http://10.0.2.2:80/PHP-Backend/api/post/create.php";
    private String currentPhotoPath;
    private Uri pickedImgUri = null;
    private static final int UPLOAD_CODE = 1;
    private static final int CAMERA_CODE = 2;
    private static final int REQUEST_CODE = 3;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText nameText, descriptionText, idText, priceText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        nameText = view.findViewById(R.id.inputName);
        descriptionText = view.findViewById(R.id.inputDescription);
        priceText = view.findViewById(R.id.inputPrice);
        idText = view.findViewById(R.id.userId);

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
        String stringUserId = idText.getText().toString();
        String stringItemPrice = priceText.getText().toString();
        //Convert user_id into an integer
        int user_id = 0;
        try {
            user_id = Integer.parseInt(stringUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //convert item_price into a double
        double item_price = 0;
        try {
            item_price = Double.parseDouble(stringItemPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        //Convert variables into valid JSON
        JSONObject json = createJson(user_id, textbook_name, item_price, description_text);

        //Start a new thread to execute HTTP request
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Post JSON to server
                doPostRequest(URL, String.valueOf(json));
            }
        }).start();

        //Close the data_entry_dialog
        builder.dismiss();
    }

    //Format data into JSON
    JSONObject createJson(Integer user_id, String textbook_name, Double item_price, String description_text) {
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", user_id);
            json.put("textbook_name", textbook_name);
            json.put("suggested_price", item_price);
            json.put("photo_filepath", "../../photos/gasps.png");
            json.put("description_text", description_text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    void doPostRequest(String url, String json) {
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

                String mMessage = response.body().string();
                Log.e("success", mMessage);
            }
        });
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