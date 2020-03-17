package com.grvmishra788.pay_track;

import android.text.TextUtils;
import android.util.Patterns;

public class InputValidationUtilities {

    public static boolean isValidString(String target) {
        return !TextUtils.isEmpty(target);
    }

    public static boolean isValidCategory(String target) {
        return (!TextUtils.isEmpty(target) && !(target.contains("/")));
    }

    public static boolean isValidNumber(String target) {
        //function to return valid number - can be zero
        return (TextUtils.isEmpty(target)) || (!TextUtils.isEmpty(target) && target.matches("[0-9]+"));
    }

    public static boolean isValidAmount(String target) {
        //function to return valid number - can't be zero
        return (!TextUtils.isEmpty(target) && target.matches("[0-9]+"));
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

    public static Boolean isCategoryDiffFromParent(String categoryName, String parent) {
        if (isValidString(categoryName) && isValidString(parent)) {
            return !TextUtils.equals(categoryName,parent);
        } else {
            return true;
        }
    }
}
