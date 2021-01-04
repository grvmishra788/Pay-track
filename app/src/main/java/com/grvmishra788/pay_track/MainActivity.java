package com.grvmishra788.pay_track;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.grvmishra788.pay_track.BackEnd.DatabaseConstants;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;
import com.grvmishra788.pay_track.DS.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.grvmishra788.pay_track.GlobalConstants.DATE_FORMAT_SIMPLE_UNDERSCORE;
import static com.grvmishra788.pay_track.GlobalConstants.MY_PERMISSIONS_REQUEST;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ANALYZE_TRANSACTIONS;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SHOW_PENDING_MESSAGES;
import static com.grvmishra788.pay_track.GlobalConstants.TRANSACTION_OBJECT;

public class MainActivity extends AppCompatActivity {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + MainActivity.class.getName();

    //tabLayout & viewPager variables
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    //db helper variable
    private DbHelper payTrackDBHelper;

    private DrawerLayout drawer;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        MY_PERMISSIONS_REQUEST);

            }
        }

        //init db
        payTrackDBHelper = new DbHelper(this);

        //init tabLayout and viewPager
        mTabLayout = findViewById(R.id.layoutBottomTabs);
        mViewPager = findViewById(R.id.viewPager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new AccountsFragment(), getString(R.string.tab_1_name));
        mViewPagerAdapter.addFragment(new TransactionsFragment(), getString(R.string.tab_2_name));
        mViewPagerAdapter.addFragment(new DebtsFragment(), getString(R.string.tab_3_name));
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        //set Transactions tab as default
        mViewPager.setCurrentItem(1);

        //setup action bar and navigation drawer
        setUpToolBarAndNavDrawer();
        Log.i(TAG, "onCreate() ends!");
    }

    //function to get db helper object
    public DbHelper getPayTrackDBHelper() {
        return payTrackDBHelper;
    }

    //function to setup action bar and navigation drawer
    private void setUpToolBarAndNavDrawer() {
        //set action bar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup drawer toggle
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setUpNavigationView();
    }

    private void setUpNavigationView() {
        Log.i(TAG,"setUpNavigationView()");
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "onNavigationItemSelected() called ");

                //get ID of menu item
                int id = item.getItemId();

                // Handle navigation view item clicks here.
                if (id == R.id.nav_about) {
                    //TODO::create AboutActivity
                } else if (id == R.id.nav_setting) {
                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent);
                    //finish this MainActivity after transferring control to SettingsActivity
                    finish();
                } else if (id == R.id.nav_categories){
                    Intent intent = new Intent(getBaseContext(), CategoryActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_pending_messages){
                    Intent intent = new Intent(getBaseContext(), TransactionMessagesActivity.class);
                    startActivityForResult(intent, REQ_CODE_SHOW_PENDING_MESSAGES);
                } else if (id == R.id.nav_analyze){
                    Intent intent = new Intent(getBaseContext(), AnalyzeActivity.class);
                    startActivityForResult(intent, REQ_CODE_ANALYZE_TRANSACTIONS);
                }
                else if (id == R.id.nav_all){
                    //just close drawers
                } else {
                    Log.e(TAG,"No match for navigation menu click!");
                }

                drawer.closeDrawer(GravityCompat.START);
                Log.d(TAG, "onNavigationItemSelected() completed ");
                return false; //false returned to keep 'ALL' menu item selected
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(): resultCode - " + resultCode + " requestCode - "+ requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQ_CODE_SHOW_PENDING_MESSAGES){
            ArrayList<Transaction> transactions = (ArrayList<Transaction>) data.getSerializableExtra(TRANSACTION_OBJECT);
            if(transactions!=null){
                for(Transaction transaction: transactions){
                    ((TransactionsFragment) mViewPagerAdapter.getItem(1)).addTransaction(transaction);
                }
            }
        }
    }

    //method to create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_export_account:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ExportDatabaseCSVTask(DatabaseConstants.ACCOUNTS_TABLE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ExportDatabaseCSVTask(DatabaseConstants.ACCOUNTS_TABLE).execute();
                }
                break;
            case R.id.action_export_debts:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ExportDatabaseCSVTask(DatabaseConstants.DEBTS_TABLE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ExportDatabaseCSVTask(DatabaseConstants.DEBTS_TABLE).execute();
                }
                break;
            case R.id.action_export_categories:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ExportDatabaseCSVTask(DatabaseConstants.CATEGORIES_TABLE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ExportDatabaseCSVTask(DatabaseConstants.CATEGORIES_TABLE).execute();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ExportDatabaseCSVTask(DatabaseConstants.SUB_CATEGORIES_TABLE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ExportDatabaseCSVTask(DatabaseConstants.SUB_CATEGORIES_TABLE).execute();
                }

                break;
            case R.id.action_export_transactions:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ExportDatabaseCSVTask(DatabaseConstants.TRANSACTIONS_TABLE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ExportDatabaseCSVTask(DatabaseConstants.TRANSACTIONS_TABLE).execute();
                }
                break;

            case R.id.action_import_account: {
                ImportTableDialog importTableDialog = new ImportTableDialog(this, DatabaseConstants.ACCOUNTS_TABLE);
                importTableDialog.setListener(new DialogListener() {
                    @Override
                    public void OnSelectedFile(String fileName) {

                        CSVParser csvParser = null;
                        try {
                            csvParser = new CSVParser(fileName, DatabaseConstants.ACCOUNTS_TABLE);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getBaseContext(), "Couldn't find the file in the location", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if (csvParser != null) {
                            if (!csvParser.isValidTable()) {
                                Toast.makeText(getBaseContext(), "Not A Valid Accounts Table", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayList<CashAccount> accounts = csvParser.getAllAccounts();
                                if (accounts != null) {
                                    int count = 0;
                                    for (CashAccount account : accounts) {
                                        if (((AccountsFragment) mViewPagerAdapter.getItem(0)).addAccount(account)) {
                                            count++;
                                        }
                                    }
                                    Toast.makeText(getBaseContext(), "Successfully added " + count + " out of " + String.valueOf(accounts.size()) + " valid accounts in spreadsheet.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Error parsing the file", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                importTableDialog.show();
                break;
            }
            case R.id.action_import_debts:
                break;
            case R.id.action_import_categories: {
                //Add SUB-CATEGORY dialog
                ImportTableDialog importSubCategoryTableDialog = new ImportTableDialog(this, DatabaseConstants.SUB_CATEGORIES_TABLE);
                importSubCategoryTableDialog.setListener(new DialogListener() {
                    @Override
                    public void OnSelectedFile(String fileName) {

                        CSVParser csvParser = null;
                        try {
                            csvParser = new CSVParser(fileName, DatabaseConstants.SUB_CATEGORIES_TABLE);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getBaseContext(), "Couldn't find the file in the location", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if (csvParser != null) {
                            if (!csvParser.isValidTable()) {
                                Toast.makeText(getBaseContext(), "Not A Valid Sub-Categories Table", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayList<SubCategory> subCategories = csvParser.getAllSubCategories();
                                if (subCategories != null) {
                                    int count = 0, invalidAccOrParentCount=0;
                                    for (SubCategory subCategory : subCategories) {
                                        boolean isAccountInDb = Utilities.entryPresentInDB(MainActivity.this, DatabaseConstants.ACCOUNTS_TABLE, DatabaseConstants.ACCOUNTS_TABLE_COL_NICK_NAME, subCategory.getAccountNickName());
                                        boolean isParentInDb = Utilities.entryPresentInDB(MainActivity.this, DatabaseConstants.CATEGORIES_TABLE, DatabaseConstants.CATEGORIES_TABLE_COL_CATEGORY_NAME, subCategory.getParent());
                                        if(isAccountInDb && isParentInDb){
                                            if (payTrackDBHelper.insertDataToSubCategoriesTable(subCategory)) {
                                                Log.d(TAG, "SubCategory inserted to db - " + subCategory.toString());
                                                count++;
                                            } else {
                                                Log.e(TAG, "Couldn't insert subCategory to db - " + subCategory.toString());
                                            }
                                        } else {
                                            invalidAccOrParentCount++;
                                        }
                                    }
                                    String toastText = "Successfully added " + count + " out of " + String.valueOf(subCategories.size()) + " valid sub categories in spreadsheet.";
                                    if(invalidAccOrParentCount>0){
                                        toastText += " Rest " + invalidAccOrParentCount + ((invalidAccOrParentCount>1)?" sub categories" : " sub category") + " couldn't be added due to corresponding account or parent missing from db";
                                    }
                                    Toast.makeText(getBaseContext(), toastText, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Error parsing the file", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                importSubCategoryTableDialog.show();

                //Add CATEGORY dialog
                ImportTableDialog importCategoryTableDialog = new ImportTableDialog(this, DatabaseConstants.CATEGORIES_TABLE);
                importCategoryTableDialog.setListener(new DialogListener() {
                    @Override
                    public void OnSelectedFile(String fileName) {

                        CSVParser csvParser = null;
                        try {
                            csvParser = new CSVParser(fileName, DatabaseConstants.CATEGORIES_TABLE);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getBaseContext(), "Couldn't find the file in the location", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if (csvParser != null) {
                            if (!csvParser.isValidTable()) {
                                Toast.makeText(getBaseContext(), "Not A Valid Categories Table", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayList<Category> categories = csvParser.getAllCategories();
                                if (categories != null) {
                                    int count = 0, invalidAccCount=0;
                                    for (Category category : categories) {
                                        if(Utilities.entryPresentInDB(MainActivity.this, DatabaseConstants.ACCOUNTS_TABLE, DatabaseConstants.ACCOUNTS_TABLE_COL_NICK_NAME, category.getAccountNickName())){
                                            if (payTrackDBHelper.insertDataToCategoriesTable(category)) {
                                                Log.d(TAG, "Category inserted to db - " + category.toString());
                                                count++;
                                            } else {
                                                Log.e(TAG, "Couldn't insert category to db - " + category.toString());
                                            }
                                        } else {
                                            invalidAccCount++;
                                        }
                                    }
                                    String toastText = "Successfully added " + count + " out of " + String.valueOf(categories.size()) + " valid categories in spreadsheet.";
                                    if(invalidAccCount>0){
                                        toastText += " Rest " + invalidAccCount + ((invalidAccCount>1)?" categories" : " category") + " couldn't be added due to corresponding account missing from db";
                                    }
                                    Toast.makeText(getBaseContext(), toastText, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Error parsing the file", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                importCategoryTableDialog.show();

                break;
            }
            case R.id.action_import_transactions: {
                ImportTableDialog importTableDialog = new ImportTableDialog(this, DatabaseConstants.TRANSACTIONS_TABLE);
                importTableDialog.setListener(new DialogListener() {
                    @Override
                    public void OnSelectedFile(String fileName) {

                        CSVParser csvParser = null;
                        try {
                            csvParser = new CSVParser(fileName, DatabaseConstants.TRANSACTIONS_TABLE);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getBaseContext(), "Couldn't find the file in the location", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if (csvParser != null) {
                            if (!csvParser.isValidTable()) {
                                Toast.makeText(getBaseContext(), "Not A Valid Transactions Table", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayList<Transaction> transactions = csvParser.getAllTransactions();
                                if (transactions != null) {
                                    int count = 0, inValidSubEntries=0, inValidInsertion=0;
                                    for (Transaction transaction : transactions) {
                                        boolean isAccountInDb = Utilities.entryPresentInDB(MainActivity.this, DatabaseConstants.ACCOUNTS_TABLE, DatabaseConstants.ACCOUNTS_TABLE_COL_NICK_NAME, transaction.getAccount());
                                        boolean isCategoryInDb = Utilities.entryPresentInDB(MainActivity.this, DatabaseConstants.CATEGORIES_TABLE, DatabaseConstants.CATEGORIES_TABLE_COL_CATEGORY_NAME,transaction.getCategory());
                                        boolean isSubCategoryInDb = (!InputValidationUtilities.isValidString(transaction.getSubCategory())) || Utilities.entryPresentInDB(MainActivity.this, DatabaseConstants.SUB_CATEGORIES_TABLE, DatabaseConstants.SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME,transaction.getSubCategory());
                                        if(isAccountInDb && isCategoryInDb && isSubCategoryInDb) {
                                            if (!payTrackDBHelper.transactionPresentInDb(transaction)) {
                                                if(((TransactionsFragment) mViewPagerAdapter.getItem(1)).addTransaction(transaction)) {
                                                    count++;
                                                }
                                            } else {
                                                inValidInsertion++;
                                            }
                                        } else {
                                            inValidSubEntries++;
                                        }
                                    }
                                    String toastText = "Successfully added " + count + " out of " + String.valueOf(transactions.size()) + " valid transactions in spreadsheet.";
                                    if(inValidInsertion>0){
                                        toastText += inValidInsertion + ((inValidInsertion>1)?" transactions" : " transaction") + " couldn't be added as they are already present in db";
                                    }
                                    if(inValidSubEntries>0){
                                        toastText += " Rest " + inValidSubEntries + ((inValidSubEntries>1)?" transactions" : " transaction") + " couldn't be added due to corresponding account, category or sub-category missing from db";
                                    }
                                    Toast.makeText(getBaseContext(), toastText, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Error parsing the file", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                importTableDialog.show();
                break;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "App can't run without the permissions requested", Toast.LENGTH_SHORT ).show();
                    finish();
                }
                break;
            }
            default:
                break;
        }
        return;
    }

    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {

        private String exportTableName;
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        DbHelper dbhelper;
        private File exportDir = null;

        public ExportDatabaseCSVTask(String tableName){
            this.exportTableName = tableName;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting table...");
            this.dialog.show();
            dbhelper = new DbHelper(MainActivity.this);
        }

        protected Boolean doInBackground(final String... args) {
            if (exportTableName == DatabaseConstants.CATEGORIES_TABLE || exportTableName == DatabaseConstants.SUB_CATEGORIES_TABLE){
                exportDir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.app_name) + "/Categories/");
            }else {
                exportDir = new File(Environment.getExternalStorageDirectory(), "/" + getString(R.string.app_name) + "/");
            }
            if (!exportDir.exists()) { exportDir.mkdirs(); }

            Date date = Utilities.getTodayDateWithDefaultTime();
            SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT_SIMPLE_UNDERSCORE);
            String dateString = sdf.format(date);

            exportDir = new File(this.exportDir, exportTableName + "_" + dateString+".csv");
            try {
                exportDir.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(exportDir));
                Cursor curCSV = dbhelper.rawTable(exportTableName);
                if(curCSV!=null){
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while(curCSV.moveToNext()) {
                        String arrStr[]=null;
                        String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                        for(int i=0;i<curCSV.getColumnNames().length;i++)
                        {
                            mySecondStringArray[i] =curCSV.getString(i);
                        }
                        csvWrite.writeNext(mySecondStringArray);
                    }
                    csvWrite.close();
                    curCSV.close();
                    return true;
                }
                return false;
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) { this.dialog.dismiss(); }
            if (success) {
                Toast.makeText(MainActivity.this, "Exported to - " + exportDir, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
