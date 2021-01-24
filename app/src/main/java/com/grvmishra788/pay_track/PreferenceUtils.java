package com.grvmishra788.pay_track;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;

import static com.grvmishra788.pay_track.GlobalConstants.DATE_SORT_RECENT_FIRST;
import static com.grvmishra788.pay_track.GlobalConstants.DEFAULT_FORMAT_DAY_AND_DATE;

public final class PreferenceUtils {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + PreferenceUtils.class.getName();

    public static String getDefaultDateFormat(Context context){
        String defaultDateFormat = DEFAULT_FORMAT_DAY_AND_DATE;
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (userPreferences != null) {
            defaultDateFormat = userPreferences.getString(context.getString(R.string.pref_key_date_format), DEFAULT_FORMAT_DAY_AND_DATE );
        }
        Log.d(TAG, "defaultDateFormat - " + defaultDateFormat);
        return defaultDateFormat;
    }

    public static SimpleDateFormat getDefaultMonthAndYearFormat(Context context) {
        String defaultDateFormat = getDefaultDateFormat(context);
        if(InputValidationUtilities.isValidString(defaultDateFormat) && (defaultDateFormat.equals("MMMM dd, yyyy")||defaultDateFormat.equals("dd MMMM, yyyy")))
            return new SimpleDateFormat(GlobalConstants.DATE_FORMAT_MONTH_AND_YEAR_LONG);
        else
            return new SimpleDateFormat(GlobalConstants.DATE_FORMAT_MONTH_AND_YEAR);
    }

    public static int getDefaultDateSortType(Context context){
        int defaultDateSortType = DATE_SORT_RECENT_FIRST;
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (userPreferences != null) {
            defaultDateSortType = Integer.parseInt(userPreferences.getString(context.getString(R.string.pref_key_date_sort), String.valueOf(DATE_SORT_RECENT_FIRST)));
        }
        Log.d(TAG, "defaultDateSortType - " + defaultDateSortType);
        return defaultDateSortType;
    }

    public static GlobalConstants.Filter getGroupTransactionFilter(Context context){
        GlobalConstants.Filter groupTransactionsFilter = GlobalConstants.Filter.BY_MONTH;
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (userPreferences != null) {
            String prefStr = userPreferences.getString(context.getString(R.string.pref_key_group_transactions),context.getResources().getStringArray(R.array.analyze_filterBy)[0]);
            if(prefStr.equals(context.getResources().getStringArray(R.array.analyze_filterBy)[1])){
                groupTransactionsFilter = GlobalConstants.Filter.BY_CATEGORY;
            }
        }
        Log.d(TAG, "groupTransactionsFilter - " + groupTransactionsFilter);
        return groupTransactionsFilter;
    }

}
