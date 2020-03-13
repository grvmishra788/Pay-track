package com.grvmishra788.pay_track;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_PARENT_CATEGORY;

public class AddTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddTransactionActivity.class.getName();

    private TextView tv_submit;
    private EditText et_amount, et_date, et_category, et_account, et_description;
    private Spinner transactionType;

    private String amount, category, description, account;
    private Date date;
    private GlobalConstants.TransactionType type;

    private ImageButton ib_date, ib_category, ib_account;

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
        setTitle(R.string.title_add_transaction);
        initViews();
        initDatePicker();
        initSpinner();
        initSubmitBtn();

        Log.i(TAG, "onCreate() ends!");
    }

    private void initDatePicker() {
        Log.i(TAG,"initDatePicker()");
        et_date = findViewById(R.id.et_date);
        ib_date = findViewById(R.id.ib_date);
        ib_date.setOnClickListener(dateClickListener);
        et_date.setOnClickListener(dateClickListener);
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
        ib_account = findViewById(R.id.ib_select_account);

        //set onclick listener for account
        et_account.setOnClickListener(selectAccountListener);
        ib_account.setOnClickListener(selectAccountListener);

        //set onclick listener for category
        et_category.setOnClickListener(selectCategoryListener);
        ib_category.setOnClickListener(selectCategoryListener);

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
            startActivityForResult(selectParentIntent, REQ_CODE_SELECT_PARENT_CATEGORY);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_SELECT_PARENT_CATEGORY){
                String categoryParent = (String) data.getStringExtra(SELECTED_CATEGORY_NAME);
                et_category.setText(categoryParent);
            } else if (requestCode == REQ_CODE_SELECT_ACCOUNT){
                String accountNickName = (String) data.getStringExtra(SELECTED_ACCOUNT_NAME);
                et_account.setText(accountNickName);
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
                    //set default balance as zero
                    Long transactionAmt = Long.valueOf(0);
                    if(!TextUtils.isEmpty(amount)){
                        transactionAmt = Long.parseLong(amount);
                    }

                    //create transaction & set result
                    Intent resultIntent = new Intent();
                    Transaction transaction = new Transaction(transactionAmt, category, date, description, type, account);
                    resultIntent.putExtra(GlobalConstants.TRANSACTION_OBJECT, transaction);
                    setResult(RESULT_OK, resultIntent);

                    Log.d(TAG, "Successfully added transaction - " + transaction.toString());

                    //finish activity
                    finish();

                } else {
                    Log.d(TAG,"Input not parsed properly - Some field entry is wrong");
                }
            }
        });

    }

    private boolean parseInput() {
        Log.i(TAG,"parseInput()");
        amount = String.valueOf(et_amount.getText()).trim();
        category = String.valueOf(et_category.getText()).trim();
        account = String.valueOf(et_account.getText()).trim();
        description = String.valueOf(et_description.getText()).trim();

        HashMap<String, Boolean> validInputs = new HashMap<>();

        validInputs.put(getString(R.string.amount), InputValidationUtilities.isValidNumber(amount));
        validInputs.put(getString(R.string.date), (date==null)?false:true);
        validInputs.put(getString(R.string.type), (type==null)?false:true);
        validInputs.put(getString(R.string.category), InputValidationUtilities.isValidString(category));
        validInputs.put(getString(R.string.account), InputValidationUtilities.isValidString(account));

        boolean isValid = true;
        if(validInputs==null){
            isValid = false;
        } else {
            Log.d(TAG, validInputs.toString());
            Iterator it = validInputs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(pair.getValue().equals(false)){
                    Log.d(TAG, "Error in "+ pair.getKey());
                    isValid = false;
                }
            }
        }
        return isValid;
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

        //get year, month and day from calendar instance && hours, minutes from earlier existing mDate
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        //create date object
        date = calendar.getTime();

        //convert date to string & display in text view
        SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_DAY_AND_DATE);
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
