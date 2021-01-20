package com.grvmishra788.pay_track;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Transaction;
import com.grvmishra788.pay_track.DS.TransactionMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import static com.grvmishra788.pay_track.GlobalConstants.DEFAULT_FORMAT_DAY_AND_DATE;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_PARENT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.TRANSACTION_MESSAGE_OBJECT;

public class AddTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddTransactionActivity.class.getName();

    private TextView tv_submit;
    private EditText et_amount, et_date, et_category, et_account, et_description;
    private Spinner transactionType;

    private String amount, category, subCategory, description, account;
    private Date date;
    private GlobalConstants.TransactionType type;

    private ImageButton ib_date, ib_category, ib_account, ib_cancelParent, ib_cancelAccount;

    private Transaction transactionToEdit = null;

    private TransactionMessage transactionMessage;

    private View.OnClickListener dateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClickListener called for date");
            DialogFragment mDatePicker = new DatePickerFragment();
            mDatePicker.show(getSupportFragmentManager(), "Date Picker Dialog");
            Log.d(TAG, "onClickListener finished for date");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initViews();
        initDatePicker();
        initSpinner();
        initSubmitBtn();

        Intent activityStartingIntent = getIntent();
        if(activityStartingIntent.hasExtra(SUB_ITEM_TO_EDIT)){
            setTitle(R.string.title_edit_transaction);
            transactionToEdit = (Transaction) activityStartingIntent.getSerializableExtra(SUB_ITEM_TO_EDIT);

            et_amount.setText(String.valueOf(transactionToEdit.getAmount()));
            et_account.setText(transactionToEdit.getAccount());

            //convert date to string & display in text view
            date = transactionToEdit.getDate();
            SimpleDateFormat sdf=new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(this));
            String dateString = sdf.format(date);
            et_date.setText(dateString);

            //set type
            if(transactionToEdit.getType().equals(GlobalConstants.TransactionType.CREDIT)){
                transactionType.setSelection(1);
            } else {
                transactionType.setSelection(0);
            }

            //set category
            String subCategory = transactionToEdit.getSubCategory();
            String fullCategoryStr = transactionToEdit.getCategory();
            if(InputValidationUtilities.isValidString(subCategory)){
                fullCategoryStr += ("/" + subCategory);
            }
            et_category.setText(fullCategoryStr);

            et_description.setText(transactionToEdit.getDescription());

            ib_cancelParent.setVisibility(View.VISIBLE);
            ib_cancelAccount.setVisibility(View.VISIBLE);
        } else if(activityStartingIntent.hasExtra(TRANSACTION_MESSAGE_OBJECT)){
            transactionMessage = (TransactionMessage) activityStartingIntent.getSerializableExtra(TRANSACTION_MESSAGE_OBJECT);
            String msg = transactionMessage.getBody();
            //get Account from Transaction Message
            String accountNickName = TransactionMessageParser.getAccountNickName(this, transactionMessage.getSrc(), msg);
            if(InputValidationUtilities.isValidString(accountNickName)){
                et_account.setText(accountNickName);
            }
            //get transaction type from Transaction Message
            GlobalConstants.TransactionType transType = TransactionMessageParser.getTransactionType(msg);
            if(transType.equals(GlobalConstants.TransactionType.CREDIT)){
                transactionType.setSelection(1);
            } else {
                transactionType.setSelection(0);
            }
            // get Amt From Transaction Message
            et_amount.setText(TransactionMessageParser.getAmountStr(msg));
            // get Date From Transaction Message
            date = transactionMessage.getDate();
            //convert date to string & display in text view
            SimpleDateFormat sdf = new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(this));
            String dateString = sdf.format(date);
            et_date.setText(dateString);

            et_description.setText(transactionMessage.toString());
            setTitle(R.string.title_add_transaction);

        } else {
            setTitle(R.string.title_add_transaction);
        }

        Log.i(TAG, "onCreate() ends!");
    }

    private void initDatePicker() {
        Log.i(TAG,"initDatePicker()");
        et_date = findViewById(R.id.et_date);
        ib_date = findViewById(R.id.ib_date);
        ib_date.setOnClickListener(dateClickListener);
        et_date.setOnClickListener(dateClickListener);

        date = Utilities.getTodayDateWithDefaultTime();
        //convert date to string & display in text view
        SimpleDateFormat sdf=new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(this));
        String currentDateString = sdf.format(date);
        et_date.setText(currentDateString);
    }

    private void initViews() {
        Log.i(TAG,"initViews()");
        et_amount = findViewById(R.id.et_amount);
        et_date = findViewById(R.id.et_date);
        et_category = findViewById(R.id.et_select_category);
        et_account = findViewById(R.id.et_select_account);
        et_description = findViewById(R.id.et_description);

        et_amount.setOnFocusChangeListener(onFocusChangeListener);
        et_description.setOnFocusChangeListener(onFocusChangeListener);

        ib_category = findViewById(R.id.ib_select_category);
        ib_cancelParent = findViewById(R.id.ib_cancel_parent);

        ib_account = findViewById(R.id.ib_select_account);
        ib_cancelAccount = findViewById(R.id.ib_cancel_account);

        //set onclick listener for account
        et_account.setOnClickListener(selectAccountListener);
        ib_account.setOnClickListener(selectAccountListener);

        //set onclick listener for category
        et_category.setOnClickListener(selectCategoryListener);
        ib_category.setOnClickListener(selectCategoryListener);

        //set cancel listeners
        ib_cancelParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_category.setText("");
                et_category.setHint(getString(R.string.default_parent));
                ib_cancelParent.setVisibility(View.INVISIBLE);
            }
        });

        ib_cancelAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear account field as well
                et_account.setText("");
                et_account.setHint(getString(R.string.default_account));
                ib_cancelAccount.setVisibility(View.INVISIBLE);
            }
        });

    }

    private View.OnClickListener selectAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent selectAccountIntent = new Intent(getBaseContext(), SelectAccountActivity.class);
            startActivityForResult(selectAccountIntent, REQ_CODE_SELECT_ACCOUNT);
        }
    };

    private View.OnClickListener selectCategoryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent selectParentIntent = new Intent(getBaseContext(), CategoryActivity.class);
            selectParentIntent.putExtra(GlobalConstants.CATEGORY_INTENT_TYPE, SELECT_CATEGORY);
            startActivityForResult(selectParentIntent, REQ_CODE_SELECT_CATEGORY);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_SELECT_CATEGORY){
                if(data.hasExtra(SELECTED_CATEGORY_NAME)){
                    String category = data.getStringExtra(SELECTED_CATEGORY_NAME);
                    String account = data.getStringExtra(SELECTED_CATEGORY_ACCOUNT_NAME);
                    et_category.setText(category);
                    ib_cancelParent.setVisibility(View.VISIBLE);
                    if(!InputValidationUtilities.isValidString(String.valueOf(et_account.getText()).trim())){
                        et_account.setText(account);
                        ib_cancelAccount.setVisibility(View.VISIBLE);
                    }
                } else if(data.hasExtra(SELECTED_SUB_CATEGORY_NAME)){
                    String category = data.getStringExtra(SELECTED_SUB_CATEGORY_NAME);
                    String parent = data.getStringExtra(SELECTED_SUB_CATEGORY_PARENT_NAME);
                    String account = data.getStringExtra(SELECTED_SUB_CATEGORY_ACCOUNT_NAME);
                    et_category.setText(parent+"/"+category);
                    ib_cancelParent.setVisibility(View.VISIBLE);
                    if(!InputValidationUtilities.isValidString(String.valueOf(et_account.getText()).trim())){
                        et_account.setText(account);
                        ib_cancelAccount.setVisibility(View.VISIBLE);
                    }
                }
            } else if (requestCode == REQ_CODE_SELECT_ACCOUNT){
                String accountNickName = (String) data.getStringExtra(SELECTED_ACCOUNT_NAME);
                et_account.setText(accountNickName);
                ib_cancelAccount.setVisibility(View.VISIBLE);
            } else {
                Log.e(TAG, "Wrong request code - " + requestCode);
            }
        } else {
            Log.e(TAG, "Wrong result code - " + resultCode);
        }

    }

    private void initSubmitBtn() {
        Log.i(TAG,"initSubmitBtn()");
        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"SUBMIT btn :: onClick()");
                if(parseInput()) {

                    if(transactionToEdit == null) {     //Add transaction activity

                        //set default balance as zero
                        Double transactionAmt = Double.valueOf(0);
                        if(!TextUtils.isEmpty(amount)){
                            transactionAmt = Double.parseDouble(amount);
                        }

                        //generate UUID for transaction
                        UUID id = UUID.randomUUID();

                        //create transaction & set result
                        Intent resultIntent = new Intent();
                        Transaction transaction = new Transaction(id, transactionAmt, category, subCategory, date, description, type, account);
                        resultIntent.putExtra(GlobalConstants.TRANSACTION_OBJECT, transaction);
                        if(transactionMessage!=null){
                            resultIntent.putExtra(TRANSACTION_MESSAGE_OBJECT, transactionMessage);
                        }
                        setResult(RESULT_OK, resultIntent);

                        Log.d(TAG, "Successfully added transaction - " + transaction.toString());

                        //finish activity
                        finish();

                    } else {        //Edit transaction activity

                        //set default balance as zero
                        Double transactionAmt = Double.valueOf(0);
                        if(!TextUtils.isEmpty(amount)){
                            transactionAmt = Double.parseDouble(amount);
                        }
                        Intent resultIntent = new Intent();
                        Transaction newTransaction =  transactionToEdit.copy();
                        newTransaction.setAmount(transactionAmt);
                        newTransaction.setCategory(category);
                        newTransaction.setSubCategory(subCategory);
                        newTransaction.setDate(date);
                        newTransaction.setDescription(description);
                        newTransaction.setType(type);
                        newTransaction.setAccount(account);

                        resultIntent.putExtra(GlobalConstants.OLD_TRANSACTION_OBJECT, transactionToEdit);
                        resultIntent.putExtra(GlobalConstants.NEW_TRANSACTION_OBJECT, newTransaction);
                        setResult(RESULT_OK, resultIntent);
                        finish();

                    }
                } else {
                    Log.d(TAG,"Input not parsed properly - Some field entry is wrong");
                }
            }
        });

    }

    private boolean parseInput() {
        Log.i(TAG,"parseInput()");
        //set amount
        amount = String.valueOf(et_amount.getText()).trim();
        //set category and sub-caegory
        String str = String.valueOf(et_category.getText()).trim();
        String[] strings = str.split("/");
        category = strings[0];
        if(strings.length==2){
            subCategory = strings[1];
        } else {
            subCategory = null;
        }
        //set account & description
        account = String.valueOf(et_account.getText()).trim();
        description = String.valueOf(et_description.getText()).trim();

        HashMap<String, Boolean> validInputs = new HashMap<>();

        validInputs.put(getString(R.string.amount), InputValidationUtilities.isValidString(amount));
        validInputs.put(getString(R.string.date), (date==null)?false:true);
        validInputs.put(getString(R.string.type), (type==null)?false:true);
        validInputs.put(getString(R.string.category), InputValidationUtilities.isValidString(category));
        validInputs.put(getString(R.string.account), InputValidationUtilities.isValidString(account));

        String emptyFields = Utilities.checkHashMapForFalseValues(validInputs);

        if (InputValidationUtilities.isValidString(emptyFields)) {
            Utilities.showEmptyFieldsErrorDialog(this, emptyFields);
            return false;
        } else {
            if(!InputValidationUtilities.isValidAmount(amount)){
                Utilities.showErrorFromKey(AddTransactionActivity.this, getString(R.string.amount));
                return false;
            } else {
                return true;
            }
        }
    }

    private void initSpinner() {
        Log.i(TAG,"initSpinner()");
        transactionType = (Spinner) findViewById(R.id.spinner_type);
        // Create an ArrayAdapter using the string array and a custom spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_type, R.layout.layout_custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        transactionType.setAdapter(adapter);
        transactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected() - index : " + i);
                switch (i) {
                    case 1:
                        //if credit selected, change type of transaction
                        type = GlobalConstants.TransactionType.CREDIT;
                        break;
                    default:
                        //else keep it as debit
                        type = GlobalConstants.TransactionType.DEBIT;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i(TAG, "OnDateSetListener() called");

        //create date object
        date = Utilities.getDateWithDefaultTime(year, month, day);
        //convert date to string & display in text view
        SimpleDateFormat sdf=new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(this));
        String currentDateTimeString = sdf.format(date);
        et_date.setText(currentDateTimeString);

        Log.d(TAG, "OnDateSetListener() call completed - date : " + currentDateTimeString);

    }

    private EditText.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            Log.d(TAG,"onFocusChange() - viewID: " + view.getId() +" hasFocus:" + hasFocus);
            if (hasFocus) {
                //make hint disappear on edit view focus
                ((EditText) view).setHint("");
            } else {
                String hint = "";
                switch (view.getId()) {
                    case R.id.et_amount:
                        hint = getString(R.string.amount);
                        break;
                    case R.id.et_description:
                        hint = getString(R.string.description);
                        break;
                    default:
                        hint = "";
                }
                ((EditText) view).setHint(hint);
            }
        }
    };
}
