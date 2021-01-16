package com.grvmishra788.pay_track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity  extends AppCompatActivity {
    private static final String TAG = "Pay-Track: " + SettingsActivity.class.getName(); //constant Class TAG
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate() called ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //init close activity button on left hand top side of activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set title of activity
        setTitle(R.string.title_settings);

        //replace content with SettingsFragment
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        Log.d(TAG, "OnCreate() completed ");
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp() called ");
        //call onBackPressed to finish activity and launch MainActivity
        onBackPressed();
        Log.d(TAG, "onSupportNavigateUp() completed ");
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() called ");
        //Restart Main Activity onBackPressed() to reflect changes
        startActivity(new Intent(this, MainActivity.class));
        //finish Current Activity
        finish();
        Log.d(TAG, "onBackPressed() completed ");
    }

}
