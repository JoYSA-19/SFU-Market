package com.example.myapplication;

import android.graphics.Bitmap;

public class Post {
    private int id;
    private String sfu_id;
    private String textbook_name;
    private double suggested_price;
    private Bitmap photo;
    private String description_text;
    private String post_date;
    private String first_name;
    private String last_name;
    private String phone_number;

    public Post(int id, String sfu_id, String textbook_name, double suggested_price,
                Bitmap photo, String description_text, String post_date, String first_name, String last_name, String phone_number) {
        this.id = id;
        this.sfu_id = sfu_id;
        this.textbook_name = textbook_name;
        this.suggested_price = suggested_price;
        this.photo = photo;
        this.description_text = description_text;
        this.post_date = post_date;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
    }

    public int getId() {
        return id;
    }

    public String getSfu_id() {
        return sfu_id;
    }

    public String getTextbook_name() {
        return textbook_name;
    }

    public double getSuggested_price() {
        return suggested_price;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getDescription_text() {
        return description_text;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSfu_id(String sfu_id) {
        this.sfu_id = sfu_id;
    }

    public void setTextbook_name(String textbook_name) {
        this.textbook_name = textbook_name;
    }

    public void setSuggested_price(double suggested_price) {
        this.suggested_price = suggested_price;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public void setDescription_text(String description_text) {
        this.description_text = description_text;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }
}
