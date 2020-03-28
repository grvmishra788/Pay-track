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
    public static final String DEBTS_TABLE = "debts_table";
    public static final String TRANSACTION_MESSAGES_TABLE = "messages_table";

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
    public static final String CATEGORIES_TABLE_COL_ACCOUNT_NAME = "Account_Name";
    public static final String CATEGORIES_TABLE_COL_DESCRIPTION = "Description";

    public static final String SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME = "Sub_Category_Name";
    public static final String SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME = "Account_Name";
    public static final String SUB_CATEGORIES_TABLE_COL_DESCRIPTION = "Description";
    public static final String SUB_CATEGORIES_TABLE_COL_PARENT = "Parent";

    public static final String TRANSACTIONS_TABLE_COL_ID = "ID";
    public static final String TRANSACTIONS_TABLE_COL_AMOUNT = "Amount";
    public static final String TRANSACTIONS_TABLE_COL_DESCRIPTION = "Description";
    public static final String TRANSACTIONS_TABLE_COL_CATEGORY = "Category";
    public static final String TRANSACTIONS_TABLE_COL_SUB_CATEGORY = "SubCategory";
    public static final String TRANSACTIONS_TABLE_COL_TYPE = "Type";
    public static final String TRANSACTIONS_TABLE_COL_DATE = "Date";
    public static final String TRANSACTIONS_TABLE_COL_ACCOUNT = "Account";

    public static final String DEBTS_TABLE_COL_ID = "ID";
    public static final String DEBTS_TABLE_COL_AMOUNT = "Amount";
    public static final String DEBTS_TABLE_COL_DESCRIPTION = "Description";
    public static final String DEBTS_TABLE_COL_PERSON = "Person";
    public static final String DEBTS_TABLE_COL_TYPE = "Type";
    public static final String DEBTS_TABLE_COL_DATE = "Date";
    public static final String DEBTS_TABLE_COL_ACCOUNT = "Account";

    public static final String TRANSACTION_MESSAGES_TABLE_COL_ID = "ID";
    public static final String TRANSACTION_MESSAGES_TABLE_COL_SRC = "Src";
    public static final String TRANSACTION_MESSAGES_TABLE_COL_BODY = "Body";
    public static final String TRANSACTION_MESSAGES_TABLE_COL_DATE = "Date";

    //account types
    public static final int BANK_ACCOUNT = 101;
    public static final int DIGITAL_ACCOUNT = 102;
    public static final int CASH_ACCOUNT = 103;

    //string arrays for column names
    public static final String[] ACCOUNTS_COLUMN_NAMES = {
            ACCOUNTS_TABLE_COL_NICK_NAME, ACCOUNTS_TABLE_COL_TYPE, ACCOUNTS_TABLE_COL_BANK_NAME,
            ACCOUNTS_TABLE_COL_SERVICE_NAME, ACCOUNTS_TABLE_COL_ACCOUNT_NO, ACCOUNTS_TABLE_COL_EMAIL,
            ACCOUNTS_TABLE_COL_MOBILE, ACCOUNTS_TABLE_COL_BALANCE
    };

}
