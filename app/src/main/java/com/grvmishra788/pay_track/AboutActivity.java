package com.grvmishra788.pay_track;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = "Pay-Track: " + AboutActivity.class.getName(); //constant Class TAG
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate() called ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //init close activity button on left hand top side of activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set title of activity
        setTitle(R.string.title_about);

        Log.d(TAG, "OnCreate() completed ");
    }
}
