package com.grvmishra788.pay_track.BackEnd;

import com.grvmishra788.pay_track.R;

public class DatabaseConstants {
    //database name
    public static final String DATABASE_NAME = "pay_track.db";
    //table names
    public static final String ACCOUNTS_TABLE = "accounts_table";
    public static final String CATEGORIES_TABLE = "categories_table";
    public static final String SUB_CATEGORIES_TABLE = "sub_categories_table";
    public static final String TRANSACTIONS_TABLE = "transactions_table";
    //coloumn names
    public static final String ACCOUNTS_TABLE_COL_NICK_NAME = "Nick_Name";
    public static final String ACCOUNTS_TABLE_COL_TYPE = "Type";
    public static final String ACCOUNTS_TABLE_COL_BANK_NAME = "Bank_Name";
    public static final String ACCOUNTS_TABLE_COL_SERVICE_NAME = "Service_Name";
    public static final String ACCOUNTS_TABLE_COL_ACCOUNT_NO = "Account_No";
    public static final String ACCOUNTS_TABLE_COL_EMAIL = "Email";
    public static final String ACCOUNTS_TABLE_COL_MOBILE = "Mobile";
    public static final String ACCOUNTS_TABLE_COL_BALANCE = "Balance";

    public static final String CATEGORIES_TABLE_COL_CATEGORY_NAME = "Category_Name";
    public static final String CATEGORIES_TABLE_COL_ASSOCIATED_ACCOUNT = "Associate_Account_Name";
    public static final String CATEGORIES_TABLE_COL_DESCRIPTION = "Description";

    public static final String SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME = "Sub_Category_Name";
    public static final String SUB_CATEGORIES_TABLE_COL_ASSOCIATED_ACCOUNT = "Associate_Account_Name";
    public static final String SUB_CATEGORIES_TABLE_COL_DESCRIPTION = "Description";
    public static final String SUB_CATEGORIES_TABLE_COL_PARENT = "Parent";

    public static final String TRANSACTIONS_TABLE_COL_ID = "ID";
    public static final String TRANSACTIONS_TABLE_COL_AMOUNT = "Amount";
    public static final String TRANSACTIONS_TABLE_COL_DESCRIPTION = "Description";
    public static final String TRANSACTIONS_TABLE_COL_CATEGORY = "Category";
    public static final String TRANSACTIONS_TABLE_COL_TYPE = "Type";
    public static final String TRANSACTIONS_TABLE_COL_DATE = "Date";
    public static final String TRANSACTIONS_TABLE_COL_ACCOUNT = "Account";

    //account types
    public static final int BANK_ACCOUNT = 101;
    public static final int DIGITAL_ACCOUNT = 102;
    public static final int CASH_ACCOUNT = 103;

}
