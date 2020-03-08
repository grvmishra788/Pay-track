package com.grvmishra788.pay_track.BackEnd;

import com.grvmishra788.pay_track.R;

public class DatabaseConstants {
    //database name
    public static final String DATABASE_NAME = "pay_track.db";
    //table names
    public static final String ACCOUNTS_TABLE = "accounts_table";
    //coloumn names
    public static final String ACCOUNTS_TABLE_COL_NICK_NAME = "Nick_Name";
    public static final String ACCOUNTS_TABLE_COL_TYPE = "Type";
    public static final String ACCOUNTS_TABLE_COL_BANK_NAME = "Bank_Name";
    public static final String ACCOUNTS_TABLE_COL_SERVICE_NAME = "Service_Name";
    public static final String ACCOUNTS_TABLE_COL_ACCOUNT_NO = "Account_No";
    public static final String ACCOUNTS_TABLE_COL_EMAIL = "Email";
    public static final String ACCOUNTS_TABLE_COL_MOBILE = "Mobile";
    public static final String ACCOUNTS_TABLE_COL_BALANCE = "Balance";
    //account types
    public static final int BANK_ACCOUNT = 101;
    public static final int DIGITAL_ACCOUNT = 102;
    public static final int CASH_ACCOUNT = 103;

}
