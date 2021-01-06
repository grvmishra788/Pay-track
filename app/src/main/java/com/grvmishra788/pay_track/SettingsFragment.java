package com.grvmishra788.pay_track;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.annotation.Nullable;

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = SettingsFragment.class.getName();     //constant Class TAG

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

}
