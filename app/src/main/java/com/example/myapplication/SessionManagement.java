package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SFUID = "SFU_ID";
    String PREF_UNIQUE_ID = "SFU_MARKET_UNIQUE_ID";

    public SessionManagement(Context context) {
        sharedPreferences = context.getSharedPreferences(SFUID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String uniqueID = sharedPreferences.getString(PREF_UNIQUE_ID, null);
        if(uniqueID == null) {
            uniqueID = UUID.randomUUID().toString();
            editor.putString(PREF_UNIQUE_ID, uniqueID).commit();
        }
    }

    public void saveSession(String sfu_id) {
        editor.putString(SFUID, sfu_id).commit();
    }

    public void endSession() {
        editor.putString(SFUID, null).commit();
    }

    public String getUniqueID() {
        return sharedPreferences.getString(PREF_UNIQUE_ID, null);
    }

    public String getSession() {
        return sharedPreferences.getString(SFUID, null);
    }

}
