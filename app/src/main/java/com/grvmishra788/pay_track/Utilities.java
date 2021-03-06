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
