package com.grvmishra788.pay_track.BackEnd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grvmishra788.pay_track.DS.BankAccount;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.Debt;
import com.grvmishra788.pay_track.DS.DigitalAccount;
import com.grvmishra788.pay_track.DS.SubCategory;
import com.grvmishra788.pay_track.DS.Transaction;
import com.grvmishra788.pay_track.GlobalConstants;

import java.util.ArrayList;
import java.util.Date;

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
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_DESCRIPTION;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DATABASE_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE_COL_ACCOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE_COL_AMOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE_COL_DATE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE_COL_DESCRIPTION;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE_COL_ID;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE_COL_PERSON;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DEBTS_TABLE_COL_TYPE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DIGITAL_ACCOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_DESCRIPTION;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_PARENT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_AMOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_CATEGORY;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_DESCRIPTION;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_ID;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_ACCOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_DATE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_TYPE;

public class DbHelper extends SQLiteOpenHelper {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + DbHelper.class.getName();


    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createAccountsTable(sqLiteDatabase);
        createCategoriesTable(sqLiteDatabase);
        createSubCategoriesTable(sqLiteDatabase);
        createTransactionsTable(sqLiteDatabase);
        createDebtsTable(sqLiteDatabase);
    }

    private void createAccountsTable(SQLiteDatabase sqLiteDatabase) {
        String createAccountsTableSQLQuery = "create table IF NOT EXISTS " + ACCOUNTS_TABLE + " (" +
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
            sqLiteDatabase.execSQL(createAccountsTableSQLQuery);
            Log.i(TAG, "Successfully executed query - " + createAccountsTableSQLQuery);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute query - " + createAccountsTableSQLQuery);
            e.printStackTrace();
        }
    }

    private void createCategoriesTable(SQLiteDatabase sqLiteDatabase) {
        String createCategoriesTableSQLQuery = "create table IF NOT EXISTS " + CATEGORIES_TABLE + " (" +
                CATEGORIES_TABLE_COL_CATEGORY_NAME + " TEXT COLLATE NOCASE PRIMARY KEY, " +
                CATEGORIES_TABLE_COL_ACCOUNT_NAME + " TEXT, " +
                CATEGORIES_TABLE_COL_DESCRIPTION + " TEXT, " +
                " FOREIGN KEY (" + CATEGORIES_TABLE_COL_ACCOUNT_NAME + ") REFERENCES " + ACCOUNTS_TABLE + " (" + ACCOUNTS_TABLE_COL_NICK_NAME + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ")";

        try {
            sqLiteDatabase.execSQL(createCategoriesTableSQLQuery);
            Log.i(TAG, "Successfully executed query - " + createCategoriesTableSQLQuery);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute query - " + createCategoriesTableSQLQuery);
            e.printStackTrace();
        }
    }

    private void createSubCategoriesTable(SQLiteDatabase sqLiteDatabase) {
        String createSubCategoriesTableSQLQuery = "create table IF NOT EXISTS " + SUB_CATEGORIES_TABLE + " (" +
                SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME + " TEXT COLLATE NOCASE PRIMARY KEY, " +
                SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME + " TEXT, " +
                SUB_CATEGORIES_TABLE_COL_DESCRIPTION + " TEXT, " +
                SUB_CATEGORIES_TABLE_COL_PARENT + " TEXT, " +
                " FOREIGN KEY (" + SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME + ") REFERENCES " + ACCOUNTS_TABLE + " (" + ACCOUNTS_TABLE_COL_NICK_NAME + ") ON UPDATE CASCADE ON DELETE CASCADE," +
                " FOREIGN KEY (" + SUB_CATEGORIES_TABLE_COL_PARENT + ") REFERENCES " + CATEGORIES_TABLE + " (" + CATEGORIES_TABLE_COL_CATEGORY_NAME + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ")";

        try {
            sqLiteDatabase.execSQL(createSubCategoriesTableSQLQuery);
            Log.i(TAG, "Successfully executed query - " + createSubCategoriesTableSQLQuery);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute query - " + createSubCategoriesTableSQLQuery);
            e.printStackTrace();
        }
    }

    private void createTransactionsTable(SQLiteDatabase sqLiteDatabase) {
        String createTransactionsTableSQLQuery = "create table IF NOT EXISTS " + TRANSACTIONS_TABLE + " (" +
                TRANSACTIONS_TABLE_COL_ID + " INTEGER AUTO INCREMENT PRIMARY KEY, " +
                TRANSACTIONS_TABLE_COL_AMOUNT + " INTEGER, " +
                TRANSACTIONS_TABLE_COL_DESCRIPTION + " TEXT, " +
                TRANSACTIONS_TABLE_COL_CATEGORY + " TEXT, " +
                TRANSACTIONS_TABLE_COL_TYPE + " INTEGER, " +
                TRANSACTIONS_TABLE_COL_DATE + " DATE, " +
                TRANSACTIONS_TABLE_COL_ACCOUNT + " TEXT, " +
                " FOREIGN KEY (" + TRANSACTIONS_TABLE_COL_CATEGORY + ") REFERENCES " + CATEGORIES_TABLE + " (" + CATEGORIES_TABLE_COL_CATEGORY_NAME + ") ON UPDATE CASCADE ON DELETE CASCADE," +
                " FOREIGN KEY (" + TRANSACTIONS_TABLE_COL_ACCOUNT + ") REFERENCES " + ACCOUNTS_TABLE + " (" + ACCOUNTS_TABLE_COL_NICK_NAME + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ")";

        try {
            sqLiteDatabase.execSQL(createTransactionsTableSQLQuery);
            Log.i(TAG, "Successfully executed query - " + createTransactionsTableSQLQuery);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute query - " + createTransactionsTableSQLQuery);
            e.printStackTrace();
        }
    }

    private void createDebtsTable(SQLiteDatabase sqLiteDatabase) {
        String createDebtsTableSQLQuery = "create table IF NOT EXISTS " + DEBTS_TABLE + " (" +
                DEBTS_TABLE_COL_ID + " INTEGER AUTO INCREMENT PRIMARY KEY, " +
                DEBTS_TABLE_COL_AMOUNT + " INTEGER, " +
                DEBTS_TABLE_COL_DESCRIPTION + " TEXT, " +
                DEBTS_TABLE_COL_PERSON + " TEXT, " +
                DEBTS_TABLE_COL_TYPE + " INTEGER, " +
                DEBTS_TABLE_COL_DATE + " DATE, " +
                DEBTS_TABLE_COL_ACCOUNT + " TEXT, " +
                " FOREIGN KEY (" + DEBTS_TABLE_COL_ACCOUNT + ") REFERENCES " + ACCOUNTS_TABLE + " (" + ACCOUNTS_TABLE_COL_NICK_NAME + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ")";

        try {
            sqLiteDatabase.execSQL(createDebtsTableSQLQuery);
            Log.i(TAG, "Successfully executed query - " + createDebtsTableSQLQuery);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute query - " + createDebtsTableSQLQuery);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUB_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DEBTS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public boolean insertDataToDebtsTable(Debt debt) {
        Log.i(TAG, "insertDataToDebtsTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEBTS_TABLE_COL_AMOUNT, debt.getAmount());
        contentValues.put(DEBTS_TABLE_COL_DESCRIPTION, debt.getDescription());
        contentValues.put(DEBTS_TABLE_COL_PERSON, debt.getPerson());
        contentValues.put(DEBTS_TABLE_COL_TYPE, (debt.getType() == GlobalConstants.DebtType.RECEIVE) ? 1 : 0);
        contentValues.put(DEBTS_TABLE_COL_DATE, debt.getDate().getTime());
        contentValues.put(DEBTS_TABLE_COL_ACCOUNT, debt.getAccount());

        long success = database.insert(DEBTS_TABLE, null, contentValues);
        if (success == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean insertDataToTransactionsTable(Transaction transaction) {
        Log.i(TAG, "insertDataToTransactionsTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTIONS_TABLE_COL_AMOUNT, transaction.getAmount());
        contentValues.put(TRANSACTIONS_TABLE_COL_DESCRIPTION, transaction.getDescription());
        contentValues.put(TRANSACTIONS_TABLE_COL_CATEGORY, transaction.getCategory());
        contentValues.put(TRANSACTIONS_TABLE_COL_TYPE, (transaction.getType() == GlobalConstants.TransactionType.CREDIT) ? 1 : 0);
        contentValues.put(TRANSACTIONS_TABLE_COL_DATE, transaction.getDate().getTime());
        contentValues.put(TRANSACTIONS_TABLE_COL_ACCOUNT, transaction.getAccount());

        long success = database.insert(TRANSACTIONS_TABLE, null, contentValues);
        if (success == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean insertDataToSubCategoriesTable(SubCategory subCategory) {
        Log.i(TAG, "insertDataToSubCategoriesTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME, subCategory.getSubCategoryName());
        contentValues.put(SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME, subCategory.getAccountNickName());
        contentValues.put(SUB_CATEGORIES_TABLE_COL_DESCRIPTION, subCategory.getDescription());
        contentValues.put(SUB_CATEGORIES_TABLE_COL_PARENT, subCategory.getParent());
        long success = -1;
        try {
            success = database.insert(SUB_CATEGORIES_TABLE, null, contentValues);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute insert query - error code : " + e.getMessage());
        }

        if (success == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean updateDataInSubCategoriesTable(SubCategory oldSubCategory, SubCategory newSubCategory) {
        Log.i(TAG, "updateDataInSubCategoriesTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME, newSubCategory.getSubCategoryName());
        contentValues.put(SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME, newSubCategory.getAccountNickName());
        contentValues.put(SUB_CATEGORIES_TABLE_COL_DESCRIPTION, newSubCategory.getDescription());
        contentValues.put(SUB_CATEGORIES_TABLE_COL_PARENT, newSubCategory.getParent());

        long success = -1;
        try {
            success = database.update(SUB_CATEGORIES_TABLE, contentValues, SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME + ("='" + oldSubCategory.getSubCategoryName() + "'"), null);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute update query - error code : " + e.getMessage());
        }

        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteDataInSubCategoriesTable(SubCategory oldSubCategory) {
        Log.i(TAG, "deleteDataInSubCategoriesTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        long success = -1;
        try {
            success = database.delete(SUB_CATEGORIES_TABLE, SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME + ("='" + oldSubCategory.getSubCategoryName() + "'"), null);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute delete query - error code : " + e.getMessage());
        }

        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean insertDataToCategoriesTable(Category category) {
        Log.i(TAG, "insertDataToCategoriesTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORIES_TABLE_COL_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(CATEGORIES_TABLE_COL_ACCOUNT_NAME, category.getAccountNickName());
        contentValues.put(CATEGORIES_TABLE_COL_DESCRIPTION, category.getDescription());
        long success = -1;
        try {
            success = database.insert(CATEGORIES_TABLE, null, contentValues);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute insert query - error code : " + e.getMessage());
        }

        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateDataInCategoriesTable(Category oldCategory, Category newCategory) {
        Log.i(TAG, "updateDataInCategoriesTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORIES_TABLE_COL_CATEGORY_NAME, newCategory.getCategoryName());
        contentValues.put(CATEGORIES_TABLE_COL_ACCOUNT_NAME, newCategory.getAccountNickName());
        contentValues.put(CATEGORIES_TABLE_COL_DESCRIPTION, newCategory.getDescription());

        long success = -1;
        try {
            success = database.update(CATEGORIES_TABLE, contentValues, CATEGORIES_TABLE_COL_CATEGORY_NAME + ("='" + oldCategory.getCategoryName() + "'"), null);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute update query - error code : " + e.getMessage());
        }

        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteDataInCategoriesTable(Category category) {
        Log.i(TAG, "deleteDataInCategoriesTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        long success = -1;
        try {
            success = database.delete(CATEGORIES_TABLE, CATEGORIES_TABLE_COL_CATEGORY_NAME + ("='" + category.getCategoryName() + "'"), null);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute delete query - error code : " + e.getMessage());
        }

        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertDataToAccountsTable(CashAccount account) {
        Log.i(TAG, "insertDataToAccountsTable()");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (account instanceof BankAccount) {

            contentValues.put(ACCOUNTS_TABLE_COL_NICK_NAME, account.getNickName());
            contentValues.put(ACCOUNTS_TABLE_COL_TYPE, BANK_ACCOUNT);
            contentValues.put(ACCOUNTS_TABLE_COL_BANK_NAME, ((BankAccount) account).getBankName());
            contentValues.put(ACCOUNTS_TABLE_COL_ACCOUNT_NO, ((BankAccount) account).getAccountNumber());
            contentValues.put(ACCOUNTS_TABLE_COL_EMAIL, ((BankAccount) account).getEmail());
            contentValues.put(ACCOUNTS_TABLE_COL_MOBILE, ((BankAccount) account).getMobileNumber());
            contentValues.put(ACCOUNTS_TABLE_COL_BALANCE, account.getAccountBalance());

        } else if (account instanceof DigitalAccount) {

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
        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int getNumberOfLinksToCategoriesTable(CashAccount account){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + CATEGORIES_TABLE + " WHERE " + CATEGORIES_TABLE_COL_ACCOUNT_NAME + "=?";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{account.getNickName()});
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute delete query - error code : " + e.getMessage());
        }
        if(cursor==null ) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    public int getNumberOfLinksToSubCategoriesTable(CashAccount account){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + SUB_CATEGORIES_TABLE + " WHERE " + SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME + "=?";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{account.getNickName()});
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute delete query - error code : " + e.getMessage());
        }
        if(cursor==null ) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    public int getNumberOfLinksToTransactionsTable(CashAccount account){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + TRANSACTIONS_TABLE + " WHERE " + TRANSACTIONS_TABLE_COL_ACCOUNT + "=?";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{account.getNickName()});
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute delete query - error code : " + e.getMessage());
        }
        if(cursor==null ) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    public int getNumberOfLinksToDebtsTable(CashAccount account){
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + DEBTS_TABLE + " WHERE " + DEBTS_TABLE_COL_ACCOUNT + "=?";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{account.getNickName()});
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute delete query - error code : " + e.getMessage());
        }
        if(cursor==null ) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    public boolean deleteDataFromAccountsTable(CashAccount account) {
        Log.i(TAG, "deleteDataFromAccountsTable()");

        SQLiteDatabase database = this.getWritableDatabase();

        long success = -1;
        try {
            success = database.delete(ACCOUNTS_TABLE, ACCOUNTS_TABLE_COL_NICK_NAME + ("='" + account.getNickName() + "'"), null);
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute delete query - error code : " + e.getMessage());
        }

        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Debt> getAllDebts() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * FROM " + DEBTS_TABLE, null);
        if (cursor.getCount() == 0) {
            Log.d(TAG, "No debts in db!");
            return null;
        } else {
            ArrayList<Debt> debts = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long amount = cursor.getLong(cursor.getColumnIndex(DEBTS_TABLE_COL_AMOUNT));
                String person = cursor.getString(cursor.getColumnIndex(DEBTS_TABLE_COL_PERSON));
                Date date = new Date(cursor.getLong(cursor.getColumnIndex(DEBTS_TABLE_COL_DATE)));
                String description = cursor.getString(cursor.getColumnIndex(DEBTS_TABLE_COL_DESCRIPTION));

                int typeVal = cursor.getInt(cursor.getColumnIndex(DEBTS_TABLE_COL_TYPE));
                GlobalConstants.DebtType type = ((typeVal == 1) ? GlobalConstants.DebtType.RECEIVE : GlobalConstants.DebtType.PAY);

                String account = cursor.getString(cursor.getColumnIndex(DEBTS_TABLE_COL_ACCOUNT));

                debts.add(new Debt(amount, date, description, type, account, person));
            }
            return debts;
        }
    }

    public ArrayList<Transaction> getAllTransactions() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * FROM " + TRANSACTIONS_TABLE, null);
        if (cursor.getCount() == 0) {
            Log.d(TAG, "No transactions in db!");
            return null;
        } else {
            ArrayList<Transaction> transactions = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long amount = cursor.getLong(cursor.getColumnIndex(TRANSACTIONS_TABLE_COL_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndex(TRANSACTIONS_TABLE_COL_CATEGORY));
                Date date = new Date(cursor.getLong(cursor.getColumnIndex(TRANSACTIONS_TABLE_COL_DATE)));
                String description = cursor.getString(cursor.getColumnIndex(TRANSACTIONS_TABLE_COL_DESCRIPTION));

                int typeVal = cursor.getInt(cursor.getColumnIndex(TRANSACTIONS_TABLE_COL_TYPE));
                GlobalConstants.TransactionType type = ((typeVal == 1) ? GlobalConstants.TransactionType.CREDIT : GlobalConstants.TransactionType.DEBIT);

                String account = cursor.getString(cursor.getColumnIndex(TRANSACTIONS_TABLE_COL_ACCOUNT));

                transactions.add(new Transaction(amount, category, date, description, type, account));
            }
            return transactions;
        }
    }

    public ArrayList<SubCategory> getAllSubCategories() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * FROM " + SUB_CATEGORIES_TABLE, null);
        if (cursor.getCount() == 0) {
            Log.d(TAG, "No subcategories in db!");
            return null;
        } else {
            ArrayList<SubCategory> subCategories = new ArrayList<>();
            while (cursor.moveToNext()) {
                String subCategoryName = cursor.getString(cursor.getColumnIndex(SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME));
                String accountNickName = cursor.getString(cursor.getColumnIndex(SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME));
                String description = cursor.getString(cursor.getColumnIndex(SUB_CATEGORIES_TABLE_COL_DESCRIPTION));
                String parent = cursor.getString(cursor.getColumnIndex(SUB_CATEGORIES_TABLE_COL_PARENT));
                subCategories.add(new SubCategory(subCategoryName, accountNickName, description, parent));
            }
            return subCategories;
        }
    }

    public ArrayList<SubCategory> getAllSubCategoriesInParent(String parent) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(SUB_CATEGORIES_TABLE, null, SUB_CATEGORIES_TABLE_COL_PARENT + "=?", new String[]{parent}, null, null, null, null);

        if (cursor.getCount() == 0) {
            Log.d(TAG, "No subcategories in db with parent - " + parent);
            return null;
        } else {
            ArrayList<SubCategory> subCategoriesUnderParent = new ArrayList<>();
            while (cursor.moveToNext()) {
                String subCategoryName = cursor.getString(cursor.getColumnIndex(SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME));
                String accountNickName = cursor.getString(cursor.getColumnIndex(SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME));
                String description = cursor.getString(cursor.getColumnIndex(SUB_CATEGORIES_TABLE_COL_DESCRIPTION));
                subCategoriesUnderParent.add(new SubCategory(subCategoryName, accountNickName, description, parent));
            }
            return subCategoriesUnderParent;
        }
    }

    public ArrayList<Category> getAllCategories() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * FROM " + CATEGORIES_TABLE, null);
        if (cursor.getCount() == 0) {
            Log.d(TAG, "No categories in db!");
            return null;
        } else {
            ArrayList<Category> categories = new ArrayList<>();
            while (cursor.moveToNext()) {
                String categoryName = cursor.getString(cursor.getColumnIndex(CATEGORIES_TABLE_COL_CATEGORY_NAME));
                String accountNickName = cursor.getString(cursor.getColumnIndex(CATEGORIES_TABLE_COL_ACCOUNT_NAME));
                String description = cursor.getString(cursor.getColumnIndex(CATEGORIES_TABLE_COL_DESCRIPTION));
                ArrayList<SubCategory> subCategories = getAllSubCategoriesInParent(categoryName);
                categories.add(new Category(categoryName, accountNickName, description, subCategories));
            }
            return categories;
        }
    }

    public ArrayList<CashAccount> getAllAccounts() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * FROM " + ACCOUNTS_TABLE, null);
        if (cursor.getCount() == 0) {
            Log.d(TAG, "No accounts in db!");
            return null;
        } else {
            ArrayList<CashAccount> accounts = new ArrayList<>();
            while (cursor.moveToNext()) {
                int type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ACCOUNTS_TABLE_COL_TYPE)));
                if (type == BANK_ACCOUNT) {

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
            cursor = database.query(tableName, null, colName + "=?", new String[]{value}, null, null, null, null);
            Log.d(TAG, "Successfully executed query.");
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute query!");
        }

        if (cursor == null)
            return false;

        if (cursor.getCount() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Category getCategory(String name) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(CATEGORIES_TABLE, null, CATEGORIES_TABLE_COL_CATEGORY_NAME + "=?", new String[]{name}, null, null, null, null);
            Log.d(TAG, "Successfully executed query.");
        } catch (SQLException e) {
            Log.e(TAG, "Unable to execute query!");
        }

        if (cursor == null)
            return null;

        if (cursor.getCount() == 1) {
            while (cursor.moveToNext()) {
                String categoryName = cursor.getString(cursor.getColumnIndex(CATEGORIES_TABLE_COL_CATEGORY_NAME));
                String accountNickName = cursor.getString(cursor.getColumnIndex(CATEGORIES_TABLE_COL_ACCOUNT_NAME));
                String description = cursor.getString(cursor.getColumnIndex(CATEGORIES_TABLE_COL_DESCRIPTION));
                ArrayList<SubCategory> subCategories = getAllSubCategoriesInParent(categoryName);
                return new Category(categoryName, accountNickName, description, subCategories);
            }
        }
        return null;
    }

}
