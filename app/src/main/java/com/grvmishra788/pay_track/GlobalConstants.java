package com.grvmishra788.pay_track;

public abstract class GlobalConstants {

    //Number of Tabs in bottom tab toolbar
    public static final int NUMBER_OF_TABS = 3;

    public static final String BULLET_SYMBOL = "\u2022";  // •

    //Category Activity Types
    public static final int SHOW_CATEGORY= 101;
    public static final int SELECT_CATEGORY = 102;
    public static final int SELECT_PARENT_CATEGORY = 103;

    //Intent request codes
    public static final int REQ_CODE_ADD_ACCOUNT = 1001;
    public static final int REQ_CODE_ADD_CATEGORY = 1002;
    public static final int REQ_CODE_ADD_TRANSACTION = 1003;
    public static final int REQ_CODE_ADD_DEBT = 1004;

    public static final int REQ_CODE_SELECT_ACCOUNT = 1101;
    public static final int REQ_CODE_SELECT_PARENT_CATEGORY = 1102;

    //Intent params
    public static final String CATEGORY_INTENT_TYPE = "com.grvmishra788.pay_track.category_intent_type";

    public static final String SELECTED_ACCOUNT_NAME = "com.grvmishra788.pay_track.selected_account_name";
    public static final String SELECTED_CATEGORY_NAME = "com.grvmishra788.pay_track.selected_category_name";

    public static final String SELECTED_CATEGORY_ACCOUNT_NAME = "com.grvmishra788.pay_track.selected_category_account_name";

    public static final String ACCOUNT_OBJECT = "com.grvmishra788.pay_track.object_account";
    public static final String CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_category";
    public static final String SUB_CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_sub_category";
    public static final String TRANSACTION_OBJECT = "com.grvmishra788.pay_track.object_transaction";
    public static final String DEBT_OBJECT = "com.grvmishra788.pay_track.object_debt";

    //Date format string to show Day and Date
    public static final String DATE_FORMAT_DAY_AND_DATE = "EEE - MMM dd, yyyy";

    //enums
    public static enum TransactionType{
        DEBIT, CREDIT
    }

    public static enum DebtType{
        PAY, RECEIVE
    }

}
