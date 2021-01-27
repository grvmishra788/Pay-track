package com.grvmishra788.pay_track;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.grvmishra788.pay_track.BackEnd.DbHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;

import static com.grvmishra788.pay_track.GlobalConstants.BULLET_SYMBOL;
import static com.grvmishra788.pay_track.GlobalConstants.RUPEE_SYMBOL;

public class Utilities {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + Utilities.class.getName();

    public static int getPxFromDip(Context context, int dpValue){
        float d = context.getResources().getDisplayMetrics().density;
        int px = (int)(dpValue * d); // margin in pixels
        return px;
    }

    public static String getAmountWithRupeeSymbol(Double amount) {
        return RUPEE_SYMBOL + " " + amount;
    }

    public static Date getStartDateOfMonthWithDefaultTime(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set day as 1st day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getRandomDateFromFuture() {
        //create date object
        Calendar calendar = Calendar.getInstance();
        //set year part of date from future
        calendar.set(Calendar.YEAR, 25000);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getRandomDateFromPast() {
        //create date object
        Calendar calendar = Calendar.getInstance();
        //set year part of date from past
        calendar.set(Calendar.YEAR, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }


    public static Date getTodayDateWithDefaultTime() {
        // set Today as default date
        //create date object
        Calendar calendar = Calendar.getInstance();
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getOneYearBackwardDate(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set time part of date as 0
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
        return calendar.getTime();
    }

    public static Date getOneYearForwardDate(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set time part of date as 0
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+1);
        return calendar.getTime();
    }

    public static Date getDateWithDefaultTime(Date date){
        //set date of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date getDateWithDefaultTime(int year, int month, int day){
        //set year, month and day of calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        //set time part of date as 0
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static boolean entryPresentInDB(Context mContext, String tableName, String colName, String value) {
        DbHelper dbHelper = new DbHelper(mContext);
        return dbHelper.entryPresentInDB(tableName, colName, value);
    }

    public static String checkHashMapForFalseValues( HashMap<String, Boolean> validInputs){
        String emptyFields = "";
        Log.d(TAG, validInputs.toString());
        Iterator it = validInputs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().equals(false)){
                emptyFields += "\n" + BULLET_SYMBOL + " " +pair.getKey();
                Log.d(TAG, "Error in "+ pair.getKey());
            }
        }
        return emptyFields;
    }

    public static boolean hasSpecificKeyError(Context mContext, HashMap<String, Boolean> validInputs) {
        Log.d(TAG, validInputs.toString());
        Iterator it = validInputs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().equals(false)){
                showErrorFromKey(mContext, (String)pair.getKey());
                return true;
            }
        }
        return false;
    }

    public static void showErrorFromKey(Context mContext, String key) {
        String msg = "Wrong input in " + key;
        if(key.equals(mContext.getString(R.string.account_number))){
            msg = mContext.getString(R.string.error_field_account_no);
        } else if (key.equals(mContext.getString(R.string.balance))){
            msg = mContext.getString(R.string.error_field_balance);
        } else if (key.equals(mContext.getString(R.string.email))){
            msg = mContext.getString(R.string.error_field_email);
        } else if (key.equals(mContext.getString(R.string.mobile))){
            msg = mContext.getString(R.string.error_field_mobile);
        } else if (key.equals(mContext.getString(R.string.amount))){
            msg = mContext.getString(R.string.error_field_amount);
        } else if (key.equals(mContext.getString(R.string.category_name))){
            msg = mContext.getString(R.string.error_field_category_name);
        }
        showSimpleErrorDialog(mContext, msg);
    }

    public static void showEmptyFieldsErrorDialog(Context mContext, String emptyFields) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(mContext.getString(R.string.error_field_empty_account) + emptyFields);
        builder.setTitle(R.string.title_general_error);
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showDuplicateFieldErrorDialog(Context mContext, String emptyFields) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(mContext.getString(R.string.error_duplicate_entry) + emptyFields);
        builder.setTitle(R.string.title_general_error);
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showSimpleErrorDialog(Context mContext, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(message);
        builder.setTitle(R.string.title_general_error);
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

}
