package com.grvmishra788.pay_track.BackEnd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grvmishra788.pay_track.AccountsFragment;
import com.grvmishra788.pay_track.DS.BankAccount;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.DigitalAccount;

import java.util.ArrayList;

import androidx.annotation.Nullable;

import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_ACCOUNT_NO;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_BALANCE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_BANK_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_EMAIL;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_MOBILE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_NICK_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_SERVICE_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_TYPE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.BANK_ACCOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CASH_ACCOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DATABASE_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DIGITAL_ACCOUNT;

public class DbHelper extends SQLiteOpenHelper {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + DbHelper.class.getName();


    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableSQLQuery =
                "create table IF NOT EXISTS " + ACCOUNTS_TABLE + " (" +
                        ACCOUNTS_TABLE_COL_NICK_NAME + " TEXT COLLATE NOCASE PRIMARY KEY, " +
                        ACCOUNTS_TABLE_COL_TYPE + " INTEGER, " +
                        ACCOUNTS_TABLE_COL_BANK_NAME + " TEXT, " +
                        ACCOUNTS_TABLE_COL_SERVICE_NAME + " TEXT, " +
                        ACCOUNTS_TABLE_COL_ACCOUNT_NO + " TEXT, " +
                        ACCOUNTS_TABLE_COL_EMAIL + " TEXT, " +
                        ACCOUNTS_TABLE_COL_MOBILE + " TEXT, " +
                        ACCOUNTS_TABLE_COL_BALANCE + " INTEGER" +
                        ")";

        try {
            sqLiteDatabase.execSQL(createTableSQLQuery);
            Log.i(TAG,"Successfully executed query - " + createTableSQLQuery);
        } catch (SQLException e){
            Log.e(TAG, "Unable to execute query - " + createTableSQLQuery);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public boolean insertDataToAccountsTable(CashAccount account){
        Log.i(TAG,"insertDataToAccountsTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(account instanceof BankAccount){

            contentValues.put(ACCOUNTS_TABLE_COL_NICK_NAME, account.getNickName());
            contentValues.put(ACCOUNTS_TABLE_COL_TYPE, BANK_ACCOUNT);
            contentValues.put(ACCOUNTS_TABLE_COL_BANK_NAME, ((BankAccount) account).getBankName());
            contentValues.put(ACCOUNTS_TABLE_COL_ACCOUNT_NO, ((BankAccount) account).getAccountNumber());
            contentValues.put(ACCOUNTS_TABLE_COL_EMAIL, ((BankAccount) account).getEmail());
            contentValues.put(ACCOUNTS_TABLE_COL_MOBILE, ((BankAccount) account).getMobileNumber());
            contentValues.put(ACCOUNTS_TABLE_COL_BALANCE, account.getAccountBalance());

        } else if (account instanceof DigitalAccount){

            contentValues.put(ACCOUNTS_TABLE_COL_NICK_NAME, account.getNickName());
            contentValues.put(ACCOUNTS_TABLE_COL_TYPE, DIGITAL_ACCOUNT);
            contentValues.put(ACCOUNTS_TABLE_COL_SERVICE_NAME, ((DigitalAccount) account).getServiceName());
            contentValues.put(ACCOUNTS_TABLE_COL_EMAIL, ((DigitalAccount) account).getEmail());
            contentValues.put(ACCOUNTS_TABLE_COL_MOBILE, ((DigitalAccount) account).getMobileNumber());
            contentValues.put(ACCOUNTS_TABLE_COL_BALANCE, account.getAccountBalance());

        } else {

            contentValues.put(ACCOUNTS_TABLE_COL_NICK_NAME, account.getNickName());
            contentValues.put(ACCOUNTS_TABLE_COL_TYPE, CASH_ACCOUNT);
            contentValues.put(ACCOUNTS_TABLE_COL_BALANCE, account.getAccountBalance());

        }

        long success = database.insert(ACCOUNTS_TABLE, null, contentValues);
        if(success==-1){
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<CashAccount> getAllAccounts(){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * FROM " + ACCOUNTS_TABLE, null);
        if(cursor.getCount()==0){
            Log.d(TAG,"No accounts in db!");
            return null;
        } else {
            ArrayList<CashAccount> accounts = new ArrayList<>();
            while (cursor.moveToNext()){
                int type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_TYPE)));
                if(type==BANK_ACCOUNT){

                    String nick_name = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_NICK_NAME));
                    String bank_name = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_BANK_NAME));
                    String account_no = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_ACCOUNT_NO));
                    String email = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_EMAIL));
                    String mobile = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_MOBILE));
                    Long balance = Long.parseLong(cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_BALANCE)));

                    accounts.add(new BankAccount(nick_name, balance, account_no, bank_name, email, mobile));

                } else if (type == DIGITAL_ACCOUNT) {

                    String nick_name = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_NICK_NAME));
                    String service_name = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_SERVICE_NAME));
                    String email = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_EMAIL));
                    String mobile = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_MOBILE));
                    Long balance = Long.parseLong(cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_BALANCE)));

                    accounts.add(new DigitalAccount(nick_name, balance, service_name, email, mobile));
                } else {

                    String nick_name = cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_NICK_NAME));
                    Long balance = Long.parseLong(cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_BALANCE)));

                    accounts.add(new CashAccount(nick_name, balance));
                }
            }
            return accounts;
        }
    }

    public boolean entryPresentInDB(String tableName, String colName, String value) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(tableName, null, colName+"=?", new String[]{value}, null, null, null, null);
            Log.d(TAG,"Successfully executed query.");
        } catch (SQLException e){
            Log.e(TAG,"Unable to execute query!");
        }

        if(cursor==null)
            return false;

        if(cursor.getCount()==0){
            return false;
        } else {
            return true;
        }
    }
}
