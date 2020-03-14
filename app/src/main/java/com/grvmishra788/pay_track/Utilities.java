package com.grvmishra788.pay_track;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.grvmishra788.pay_track.BackEnd.DbHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;

import static com.grvmishra788.pay_track.GlobalConstants.BULLET_SYMBOL;

public class Utilities {
    //constant Class TAG
    private static final String TAG = Utilities.class.getName();

    public static boolean entryPresentInDB(Context mContext, String tableName, String colName, String value) {
        DbHelper dbHelper = new DbHelper(mContext);
        return dbHelper.entryPresentInDB(tableName, colName, value);
    }

    public static String checkHashMapForFalseValues( HashMap<String, Boolean> validInputs){
        String emptyFields = "";
        int count = 0;
        Log.d(TAG, validInputs.toString());
        Iterator it = validInputs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().equals(false)){
                count++;
                emptyFields += "\n" + BULLET_SYMBOL + " " +pair.getKey();
                Log.d(TAG, "Error in "+ pair.getKey());
            }
        }
        return emptyFields;
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
