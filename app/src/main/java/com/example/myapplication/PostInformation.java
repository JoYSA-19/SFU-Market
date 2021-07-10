package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * The elements of the post information includes the item name, description
 * and user's contact information
 */
public class PostInformation {
    private String textbook_name;
    private String description;
    private String contact;
    private float price;

    public PostInformation(String textbook_name, String description, String contact, float price) {
        this.textbook_name = textbook_name;
        this.description = description;
        this.contact = contact;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public String getContact() {
        return contact;
    }
}
