package com.grvmishra788.pay_track;

public abstract class GlobalConstants {

    //Number of Tabs in bottom tab toolbar
    public static final int NUMBER_OF_TABS = 3;

    //Intent request codes
    public static final int REQ_CODE_ADD_ACCOUNT = 1001;
    public static final int REQ_CODE_ADD_TRANSACTION = 1002;

    //Intent params
    public static final String ACCOUNT_OBJECT = "com.grvmishra788.pay_track.account_object";
    public static final String TRANSACTION_OBJECT = "com.grvmishra788.pay_track.account_transaction";

    //Date format string to show Day and Date
    public static final String DATE_FORMAT_DAY_AND_DATE = "EEE - MMM dd, yyyy";

    //enums
    public static enum TransactionType{
        DEBIT,CREDIT
    }

}
