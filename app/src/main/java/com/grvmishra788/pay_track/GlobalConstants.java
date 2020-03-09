package com.grvmishra788.pay_track;

public abstract class GlobalConstants {

    //Number of Tabs in bottom tab toolbar
    public static final int NUMBER_OF_TABS = 3;

    //Intent request codes
    public static final int REQ_CODE_ADD_ACCOUNT = 1001;
    public static final int REQ_CODE_ADD_CATEGORY = 1002;
    public static final int REQ_CODE_ADD_TRANSACTION = 1003;

    public static final int REQ_CODE_SELECT_ACCOUNT = 1101;

    //Intent params
    public static final String IS_SELECT_ACCOUNT_INTENT = "com.grvmishra788.pay_track.is_select_account_intent";

    public static final String SELECTED_CATEGORY_NAME = "com.grvmishra788.pay_track.is_selected_category_name";

    public static final String ACCOUNT_OBJECT = "com.grvmishra788.pay_track.object_account";
    public static final String CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_category";
    public static final String SUB_CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_sub_category";
    public static final String TRANSACTION_OBJECT = "com.grvmishra788.pay_track.object_transaction";


    //Date format string to show Day and Date
    public static final String DATE_FORMAT_DAY_AND_DATE = "EEE - MMM dd, yyyy";

    //enums
    public static enum TransactionType{
        DEBIT,CREDIT
    }

}
