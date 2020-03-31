package com.grvmishra788.pay_track;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.grvmishra788.pay_track.BackEnd.DatabaseConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class InputValidationUtilities {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + InputValidationUtilities.class.getName();

    public static boolean isValidString(String target) {
        return !TextUtils.isEmpty(target);
    }

    public static boolean isValidCategory(String target) {
        return (!TextUtils.isEmpty(target) && !(target.contains("/")));
    }

    public static boolean isValidBalance(String target) {
        //function to return valid number - can be zero
        if(isValidString(target)){
            Double d;
            try {
                d = Double.parseDouble(target);
            } catch (NumberFormatException e) {
                Log.e(TAG,"Couldn't parse double from string - " + target);
                return false;
            }
            return true;
        }
        //return true if empty
        return true;
    }

    public static boolean isValidAmount(String target) {
        //function to return valid number - can't be zero or empty
        if(isValidString(target)){
            Double d;
            try {
                d = Double.parseDouble(target);
            } catch (NumberFormatException e) {
                Log.e(TAG,"Couldn't parse double from string - " + target);
                return false;
            }

            if(d.equals((double) 0)){
                //return false if equal to zero
                return false;
            }
            return true;
        }
        //return false if empty
        return false;
    }

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidMobileNumber(String target) {
        return (!TextUtils.isEmpty(target) && target.matches("[1-9][0-9]{9}"));
    }

    public static boolean isValidAccountNumber(String target) {
        //a valid account number should have 8 digits or more
        return (!TextUtils.isEmpty(target) && target.matches("[0-9]{7}[0-9]+"));
    }

    public static boolean isValidAccountType(String typeStr) {
        int target = -1;
        try {
            target = Integer.parseInt(typeStr);
        } catch (NumberFormatException e){
            Log.e(TAG, "Error parsing account type : " + typeStr + e.getMessage());
            return false;
        }
        //a valid account type should be either bank account, digital account or a cash account
        return (target== DatabaseConstants.BANK_ACCOUNT || target==DatabaseConstants.DIGITAL_ACCOUNT || target==DatabaseConstants.CASH_ACCOUNT);
    }

    public static Boolean isValidTransactionType(String typeStr) {
        /*
        ((typeVal == 1) ? GlobalConstants.TransactionType.CREDIT : GlobalConstants.TransactionType.DEBIT)
         */
        int typeVal = -1;
        try {
            typeVal = Integer.parseInt(typeStr);
        } catch (NumberFormatException e){
            Log.e(TAG, "Error parsing transaction type : " + typeStr + e.getMessage());
            return false;
        }
        return (typeVal==0||typeVal==1);
    }

    public static Boolean isCategoryDiffFromParent(String categoryName, String parent) {
        if (isValidString(categoryName) && isValidString(parent)) {
            return !TextUtils.equals(categoryName,parent);
        } else {
            return true;
        }
    }

    public static boolean isValidDate(String dateStr, String dateFormat) {
        SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
