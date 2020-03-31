package com.grvmishra788.pay_track;

import android.text.TextUtils;
import android.util.Log;

import com.grvmishra788.pay_track.BackEnd.DatabaseConstants;
import com.grvmishra788.pay_track.DS.BankAccount;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.DigitalAccount;
import com.grvmishra788.pay_track.DS.SubCategory;
import com.grvmishra788.pay_track.DS.Transaction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_DESCRIPTION;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.DIGITAL_ACCOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_DESCRIPTION;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_PARENT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_ACCOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_AMOUNT;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_CATEGORY;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_DATE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_DESCRIPTION;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_SUB_CATEGORY;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.TRANSACTIONS_TABLE_COL_TYPE;
import static com.grvmishra788.pay_track.GlobalConstants.DATE_FORMAT_SIMPLE;

public class CSVParser {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + CSVParser.class.getName();

    private String filePath;
    private String tableType;
    private HashMap<String, Integer> columnNamesHashMap;

    public CSVParser(String filePath, String tableType) throws FileNotFoundException {
        this.filePath = filePath;
        this.tableType = tableType;
        columnNamesHashMap = new HashMap<>();
        switch (tableType) {
            case DatabaseConstants.ACCOUNTS_TABLE:
                for(String columnName:DatabaseConstants.ACCOUNTS_COLUMN_NAMES){
                    columnNamesHashMap.put(columnName, -1);
                }
                break;

            case DatabaseConstants.CATEGORIES_TABLE:
                for(String columnName:DatabaseConstants.CATEGORIES_COLUMN_NAMES){
                    columnNamesHashMap.put(columnName, -1);
                }
                break;

            case DatabaseConstants.SUB_CATEGORIES_TABLE:
                for(String columnName:DatabaseConstants.SUB_CATEGORIES_COLUMN_NAMES){
                    columnNamesHashMap.put(columnName, -1);
                }
                break;

            case DatabaseConstants.TRANSACTIONS_TABLE:
                for(String columnName:DatabaseConstants.TRANSACTIONS_COLUMN_NAMES){
                    columnNamesHashMap.put(columnName, -1);
                }
                break;
        }
    }

    public boolean isValidTable(){
        Log.i(TAG,"isValidTable()");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        if(columnNamesHashMap==null) {
            return false;
        }
        try {
            String csvLine = reader.readLine();
            if (csvLine!=null) {
                //traverse columns
                String[] row = csvLine.split(",", -1);
                for(int i=0; i<row.length; i++){
                    String s = row[i].replaceAll("[\"]","");
                    if(!columnNamesHashMap.containsKey(s)){
                        Log.d(TAG, "Column : "+ s + " - should not be present in the spreadsheet");
                        return false;
                    } else {
                        columnNamesHashMap.put(s, i);
                    }
                }
                Iterator it = columnNamesHashMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if(pair.getValue().equals(-1)){
                        Log.d(TAG, "Column : "+ pair.getKey() + " - is not present in the spreadsheet");
                        return false;
                    }
                }
                return true;
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return false;
    }

    public ArrayList<CashAccount> getAllAccounts(){
        Log.i(TAG,"getAllAccounts()");
        if(!isValidTable()) return null;

        ArrayList<CashAccount> resultList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine = reader.readLine();
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",", -1);
                for(int i=0; i<row.length; i++) {
                    row[i] = row[i].replaceAll("[\"]", "");
                }
                String nickName = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_NICK_NAME)];
                String type = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_TYPE)];
                String bankName = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_BANK_NAME)];
                String serviceName = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_SERVICE_NAME)];
                String accountNumber = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_ACCOUNT_NO)];
                String email = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_EMAIL)];
                String mobileNumber = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_MOBILE)];
                String accountBalance = row[columnNamesHashMap.get(ACCOUNTS_TABLE_COL_BALANCE)];

                HashMap<String, Boolean> validInputs = new HashMap<>();
                CashAccount account = null;
                if(!InputValidationUtilities.isValidAccountType(type)){
                    continue;
                }
                int accountType = Integer.parseInt(type);
                String emptyFields = "";
                switch (accountType){
                    case BANK_ACCOUNT :
                        validInputs.put(ACCOUNTS_TABLE_COL_ACCOUNT_NO, InputValidationUtilities.isValidAccountNumber(accountNumber));
                        validInputs.put(ACCOUNTS_TABLE_COL_BANK_NAME, InputValidationUtilities.isValidString(bankName));
                        validInputs.put(ACCOUNTS_TABLE_COL_EMAIL, InputValidationUtilities.isValidEmail(email));
                        validInputs.put(ACCOUNTS_TABLE_COL_MOBILE, InputValidationUtilities.isValidMobileNumber(mobileNumber));
                        validInputs.put(ACCOUNTS_TABLE_COL_NICK_NAME, InputValidationUtilities.isValidString(nickName));
                        validInputs.put(ACCOUNTS_TABLE_COL_BALANCE, InputValidationUtilities.isValidBalance(accountBalance));

                        emptyFields = Utilities.checkHashMapForFalseValues(validInputs);
                        if (!InputValidationUtilities.isValidString(emptyFields)) {
                            Double balanceAcc = Double.valueOf(0);
                            if (!TextUtils.isEmpty(accountBalance)) {
                                balanceAcc = Double.parseDouble(accountBalance);
                            }
                            account = new BankAccount(nickName, balanceAcc, accountNumber, bankName, email, mobileNumber);
                        }
                        break;

                    case DIGITAL_ACCOUNT:
                        validInputs.put(ACCOUNTS_TABLE_COL_SERVICE_NAME, InputValidationUtilities.isValidString(serviceName));
                        validInputs.put(ACCOUNTS_TABLE_COL_EMAIL, InputValidationUtilities.isValidEmail(email));
                        validInputs.put(ACCOUNTS_TABLE_COL_MOBILE, InputValidationUtilities.isValidMobileNumber(mobileNumber));
                        validInputs.put(ACCOUNTS_TABLE_COL_NICK_NAME, InputValidationUtilities.isValidString(nickName));
                        validInputs.put(ACCOUNTS_TABLE_COL_BALANCE, InputValidationUtilities.isValidBalance(accountBalance));

                        emptyFields = Utilities.checkHashMapForFalseValues(validInputs);
                        if (!InputValidationUtilities.isValidString(emptyFields)) {
                            Double balanceAcc = Double.valueOf(0);
                            if (!TextUtils.isEmpty(accountBalance)) {
                                balanceAcc = Double.parseDouble(accountBalance);
                            }
                            account = new DigitalAccount(nickName, balanceAcc, serviceName, email, mobileNumber);
                        }
                        break;

                    case CASH_ACCOUNT:
                        validInputs.put(ACCOUNTS_TABLE_COL_NICK_NAME, InputValidationUtilities.isValidString(nickName));
                        validInputs.put(ACCOUNTS_TABLE_COL_BALANCE, InputValidationUtilities.isValidBalance(accountBalance));

                        emptyFields = Utilities.checkHashMapForFalseValues(validInputs);
                        if (!InputValidationUtilities.isValidString(emptyFields)) {
                            Double balanceAcc = Double.valueOf(0);
                            if (!TextUtils.isEmpty(accountBalance)) {
                                balanceAcc = Double.parseDouble(accountBalance);
                            }
                            account = new CashAccount(nickName, balanceAcc);
                        }
                        break;

                    default:
                        break;
                }

                if(account!=null)
                    resultList.add(account);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

        finally {
            try {
                inputStream.close();
            }
            catch (IOException ex) {
                throw new RuntimeException("Error while closing input stream: " + ex);
            }
        }
        return resultList;
    }

    public ArrayList<Category> getAllCategories(){
        Log.i(TAG,"getAllCategories()");
        if(!isValidTable()) return null;

        ArrayList<Category> resultList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine = reader.readLine();
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",", -1);
                for(int i=0; i<row.length; i++) {
                    row[i] = row[i].replaceAll("[\"]", "");
                }
                String categoryName = row[columnNamesHashMap.get(CATEGORIES_TABLE_COL_CATEGORY_NAME)];
                String accountNickName = row[columnNamesHashMap.get(CATEGORIES_TABLE_COL_ACCOUNT_NAME)];
                String description = row[columnNamesHashMap.get(CATEGORIES_TABLE_COL_DESCRIPTION)];

                HashMap<String, Boolean> validInputs = new HashMap<>();
                validInputs.put(CATEGORIES_TABLE_COL_CATEGORY_NAME, InputValidationUtilities.isValidCategory(categoryName));
                validInputs.put(CATEGORIES_TABLE_COL_ACCOUNT_NAME, InputValidationUtilities.isValidString(accountNickName));

                Category category = null;

                String emptyFields = Utilities.checkHashMapForFalseValues(validInputs);
                if(!InputValidationUtilities.isValidString(emptyFields))
                    category = new Category(categoryName, accountNickName, description);

                if(category!=null)
                    resultList.add(category);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

        finally {
            try {
                inputStream.close();
            }
            catch (IOException ex) {
                throw new RuntimeException("Error while closing input stream: " + ex);
            }
        }
        return resultList;
    }

    public ArrayList<SubCategory> getAllSubCategories(){
        Log.i(TAG,"getAllSubCategories()");
        if(!isValidTable()) return null;

        ArrayList<SubCategory> resultList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine = reader.readLine();
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",", -1);
                for(int i=0; i<row.length; i++) {
                    row[i] = row[i].replaceAll("[\"]", "");
                }
                String categoryName = row[columnNamesHashMap.get(SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME)];
                String accountNickName = row[columnNamesHashMap.get(SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME)];
                String description = row[columnNamesHashMap.get(SUB_CATEGORIES_TABLE_COL_DESCRIPTION)];
                String parent = row[columnNamesHashMap.get(SUB_CATEGORIES_TABLE_COL_PARENT)];

                HashMap<String, Boolean> validInputs = new HashMap<>();
                validInputs.put(SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME, InputValidationUtilities.isValidCategory(categoryName));
                validInputs.put(SUB_CATEGORIES_TABLE_COL_ACCOUNT_NAME, InputValidationUtilities.isValidString(accountNickName));
                validInputs.put(SUB_CATEGORIES_TABLE_COL_PARENT, InputValidationUtilities.isCategoryDiffFromParent(categoryName, parent));

                SubCategory subCategory = null;

                String emptyFields = Utilities.checkHashMapForFalseValues(validInputs);
                if(!InputValidationUtilities.isValidString(emptyFields))
                    subCategory = new SubCategory(categoryName, accountNickName, description, parent);

                if(subCategory!=null)
                    resultList.add(subCategory);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

        finally {
            try {
                inputStream.close();
            }
            catch (IOException ex) {
                throw new RuntimeException("Error while closing input stream: " + ex);
            }
        }
        return resultList;
    }

    public ArrayList<Transaction> getAllTransactions(){
        Log.i(TAG,"getAllTransactions()");
        if(!isValidTable()) return null;

        ArrayList<Transaction> resultList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine = reader.readLine();
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",", -1);
                for(int i=0; i<row.length; i++) {
                    row[i] = row[i].replaceAll("[\"]", "");
                }
                String amount = row[columnNamesHashMap.get(TRANSACTIONS_TABLE_COL_AMOUNT)];
                String description = row[columnNamesHashMap.get(TRANSACTIONS_TABLE_COL_DESCRIPTION)];
                String category = row[columnNamesHashMap.get(TRANSACTIONS_TABLE_COL_CATEGORY)];
                String subCategory = row[columnNamesHashMap.get(TRANSACTIONS_TABLE_COL_SUB_CATEGORY)];
                String dateStr = row[columnNamesHashMap.get(TRANSACTIONS_TABLE_COL_DATE)];
                String typeStr = row[columnNamesHashMap.get(TRANSACTIONS_TABLE_COL_TYPE)];
                String account = row[columnNamesHashMap.get(TRANSACTIONS_TABLE_COL_ACCOUNT)];

                HashMap<String, Boolean> validInputs = new HashMap<>();
                Transaction transaction = null;
                validInputs.put(TRANSACTIONS_TABLE_COL_AMOUNT, InputValidationUtilities.isValidAmount(amount));
                validInputs.put(TRANSACTIONS_TABLE_COL_CATEGORY, InputValidationUtilities.isValidCategory(category));
                validInputs.put(TRANSACTIONS_TABLE_COL_DATE, InputValidationUtilities.isValidDate(dateStr, DATE_FORMAT_SIMPLE));
                validInputs.put(TRANSACTIONS_TABLE_COL_TYPE, InputValidationUtilities.isValidTransactionType(typeStr));
                validInputs.put(TRANSACTIONS_TABLE_COL_ACCOUNT, InputValidationUtilities.isValidString(account));

                String emptyFields = Utilities.checkHashMapForFalseValues(validInputs);
                if (!InputValidationUtilities.isValidString(emptyFields)) {
                    //get transaction type
                    int typeVal = Integer.parseInt(typeStr);
                    GlobalConstants.TransactionType type = ((typeVal == 1) ? GlobalConstants.TransactionType.CREDIT : GlobalConstants.TransactionType.DEBIT);
                    //generate UUID for transaction
                    UUID id = UUID.randomUUID();
                    //set default amt as zero
                    Double transactionAmt = Double.valueOf(0);
                    if(!TextUtils.isEmpty(amount)){
                        transactionAmt = Double.parseDouble(amount);
                    }
                    //set Date
                    SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT_SIMPLE);
                    Date date = Utilities.getTodayDateWithDefaultTime();
                    try {
                        date = Utilities.getDateWithDefaultTime(sdf.parse(dateStr));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //create transaction object
                    transaction = new Transaction(id, transactionAmt, category, subCategory, date, description, type, account);
                }

                if(transaction!=null)
                    resultList.add(transaction);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

        finally {
            try {
                inputStream.close();
            }
            catch (IOException ex) {
                throw new RuntimeException("Error while closing input stream: " + ex);
            }
        }
        return resultList;
    }

}
