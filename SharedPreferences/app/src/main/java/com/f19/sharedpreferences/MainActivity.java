package com.f19.sharedpreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String SHARED_PREF = "name";
    public static final String KEY_NAME = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //There is a class named SharedPreferences
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        //Write into shared preferences
        //sharedPreferences.edit().putString(KEY_NAME, "Hello World").apply();

        //Read from sharedpreferences
        String data = sharedPreferences.getString(KEY_NAME, "no data");

        Log.i(TAG, "onCreate: " + data);

        ArrayList<String> names = new ArrayList<>(Arrays.asList("trex", "tricerotops", "pterodactyl", "stygimolich"));

        /** Commenting this out because we will use ObjecSerializer
        sharedPreferences.edit().putStringSet("names", new HashSet<String>(names)).apply();

        //Retrieve data
        Set<String> recvnames = sharedPreferences.getStringSet("names", new HashSet<String>());
        Log.i(TAG, "onCreate: " + recvnames.toString());
         */

        try {
            sharedPreferences.edit().putString("names", ObjectSerializer.serialize(names)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> recvNames = new ArrayList<>();
        try {
            recvNames = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("names", ObjectSerializer.serialize(new ArrayList<>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "onCreate: " + recvNames.toString());

    }
}
