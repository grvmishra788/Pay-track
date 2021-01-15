package com.grvmishra788.pay_track;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.annotation.Nullable;

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = SettingsFragment.class.getName();     //constant Class TAG

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        addPreferencesFromResource(R.xml.preferences);

        //init list preferences
        initListPreference(getString(R.string.pref_key_date_format));
        initListPreference(getString(R.string.pref_key_date_sort));
    }

    private void initListPreference(String type){
        //insert values in ListPreference summary & set OnPreferenceChangeListener
        Log.d(TAG, "initListPreference() called for type - "+type);
        ListPreference listPreference = (ListPreference) findPreference(type);
        if(listPreference.getValue()==null) {
            // to ensure we don't get a null value
            // set first value by default
            listPreference.setValueIndex(0);
        }
        int index = listPreference.findIndexOfValue(listPreference.getValue());
        if(index>=0)
            listPreference.setSummary(listPreference.getEntries()[index]);
        listPreference.setOnPreferenceChangeListener(mOnPreferenceChangeListener);
        Log.d(TAG, "initListPreference() completed for type - "+type);
    }

    Preference.OnPreferenceChangeListener mOnPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            ListPreference listPreference = (ListPreference)preference;
            int id = 0;
            for (int i = 0; i < listPreference.getEntryValues().length; i++)
            {
                if(listPreference.getEntryValues()[i].equals(newValue.toString())){
                    id = i;
                    break;
                }
            }
            preference.setSummary(listPreference.getEntries()[id]);
            return true;
        }
    };

}
