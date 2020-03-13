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

import com.grvmishra788.pay_track.DS.Debt;

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

public class AddDebtActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddDebtActivity.class.getName();

    private TextView tv_submit, tv_person;
    private EditText et_amount, et_date, et_person, et_account, et_description;
    private Spinner debtType;

    private String amount, person, description, account;
    private Date date;
    private GlobalConstants.DebtType type;

    private ImageButton ib_date, ib_account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt);
        setTitle(R.string.title_add_debt);
        initViews();
        initDatePicker();
        initSpinner();
        initSubmitBtn();
    }

    private void initViews() {
        Log.i(TAG,"initViews()");
        et_amount = findViewById(R.id.et_amount);
        et_date = findViewById(R.id.et_date);
        et_person = findViewById(R.id.et_person);
        et_account = findViewById(R.id.et_select_account);
        et_description = findViewById(R.id.et_description);

        et_amount.setOnFocusChangeListener(onFocusChangeListener);
        et_description.setOnFocusChangeListener(onFocusChangeListener);
        et_person.setOnFocusChangeListener(onFocusChangeListener);

        tv_person = findViewById(R.id.tv_person);
        ib_account = findViewById(R.id.ib_select_account);

        //set onclick listener for account
        et_account.setOnClickListener(selectAccountListener);
        ib_account.setOnClickListener(selectAccountListener);

    }

    private View.OnClickListener selectAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent selectAccountIntent = new Intent(getBaseContext(), SelectAccountActivity.class);
            startActivityForResult(selectAccountIntent, REQ_CODE_SELECT_ACCOUNT);
        }
    };

    private void initDatePicker() {
        Log.i(TAG,"initDatePicker()");
        et_date = findViewById(R.id.et_date);
        ib_date = findViewById(R.id.ib_date);
        ib_date.setOnClickListener(dateClickListener);
        et_date.setOnClickListener(dateClickListener);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if (requestCode == REQ_CODE_SELECT_ACCOUNT){
                String accountNickName = (String) data.getStringExtra(SELECTED_ACCOUNT_NAME);
                et_account.setText(accountNickName);
            } else {
                Log.e(TAG, "Wrong request code - " + requestCode);
            }
        } else {
            Log.e(TAG, "Wrong result code - " + resultCode);
        }

    }

    private void initSpinner() {
        Log.i(TAG,"initSpinner()");
        debtType = (Spinner) findViewById(R.id.spinner_type);
        // Create an ArrayAdapter using the string array and a custom spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.debt_type, R.layout.layout_custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        debtType.setAdapter(adapter);
        debtType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected() - index : " + i);
                switch (i) {
                    case 1:
                        //if credit selected, change type of debt
                        type = GlobalConstants.DebtType.RECEIVE;
                        tv_person.setText(getString(R.string.borrower));
                        break;
                    default:
                        //else keep it as debit
                        tv_person.setText(getString(R.string.lender));
                        type = GlobalConstants.DebtType.PAY;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                    Long debtAmt = Long.valueOf(0);
                    if(!TextUtils.isEmpty(amount)){
                        debtAmt = Long.parseLong(amount);
                    }

                    //create debt & set result
                    Intent resultIntent = new Intent();
                    Debt debt = new Debt(debtAmt, date, description, type, account, person);
                    resultIntent.putExtra(GlobalConstants.DEBT_OBJECT, debt);
                    setResult(RESULT_OK, resultIntent);

                    Log.d(TAG, "Successfully added debt - " + debt.toString());

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
        person = String.valueOf(et_person.getText()).trim();
        account = String.valueOf(et_account.getText()).trim();
        description = String.valueOf(et_description.getText()).trim();

        HashMap<String, Boolean> validInputs = new HashMap<>();

        validInputs.put(getString(R.string.amount), InputValidationUtilities.isValidNumber(amount));
        validInputs.put(getString(R.string.date), (date==null)?false:true);
        validInputs.put(getString(R.string.type), (type==null)?false:true);
        validInputs.put(getString(R.string.person), InputValidationUtilities.isValidString(person));
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
                    case R.id.et_person:
                        hint = getString(R.string.person);
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