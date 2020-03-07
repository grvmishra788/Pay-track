package com.grvmishra788.pay_track;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = DatePickerFragment.class.getName();     //constant Class TAG

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateDialog() called");
        Calendar mCalendar = Calendar.getInstance();
        int year = mCalendar.get(mCalendar.YEAR);
        int month = mCalendar.get(mCalendar.MONTH);
        int day = mCalendar.get(mCalendar.DAY_OF_MONTH);
        Dialog mDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
        Log.d(TAG, "OnCreateDialog() call completed");
        return mDialog;
    }
}