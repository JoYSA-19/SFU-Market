package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * The elements of the post information includes the item name, description
 * and user's contact information
 */
public class PostInformation {
    public String name;
    public String description;
    public String contact;
    public float price;
    public Uri imageUri;
}
