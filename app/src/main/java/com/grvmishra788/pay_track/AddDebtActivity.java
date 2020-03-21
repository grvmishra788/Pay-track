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
import com.grvmishra788.pay_track.DS.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import static com.grvmishra788.pay_track.GlobalConstants.DATE_FORMAT_DAY_AND_DATE;
import static com.grvmishra788.pay_track.GlobalConstants.ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.POSITION_ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_ITEM_TO_EDIT;

public class AddDebtActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddDebtActivity.class.getName();

    private TextView tv_submit, tv_person;
    private EditText et_amount, et_date, et_person, et_account, et_description;
    private Spinner debtType;

    private String amount, person, description, account;
    private Date date;
    private GlobalConstants.DebtType type;

    private ImageButton ib_date, ib_account, ib_cancelAccount;

    private Debt debtToEdit = null;
    private int positionDebtToEdit = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt);

        initViews();
        initDatePicker();
        initSpinner();
        initSubmitBtn();

        Intent activityStartingIntent = getIntent();
        if(activityStartingIntent.hasExtra(ITEM_TO_EDIT)) {
            setTitle(R.string.title_edit_debt);
            debtToEdit = (Debt) activityStartingIntent.getSerializableExtra(ITEM_TO_EDIT);
            positionDebtToEdit = activityStartingIntent.getIntExtra(POSITION_ITEM_TO_EDIT, -1);

            et_amount.setText(String.valueOf(debtToEdit.getAmount()));
            et_account.setText(debtToEdit.getAccount());

            //convert date to string & display in text view
            Date date = debtToEdit.getDate();
            SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT_DAY_AND_DATE);
            String dateString = sdf.format(date);
            et_date.setText(dateString);

            //set type
            if(debtToEdit.getType().equals(GlobalConstants.DebtType.RECEIVE)){
                debtType.setSelection(1);
                tv_person.setText(getString(R.string.borrower));
            } else {
                tv_person.setText(getString(R.string.lender));
                debtType.setSelection(0);
            }

            //set borrower/lender name & description
            et_person.setText(debtToEdit.getPerson());
            et_description.setText(debtToEdit.getDescription());

            ib_cancelAccount.setVisibility(View.VISIBLE);
        } else {
            setTitle(R.string.title_add_debt);
        }

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
        ib_cancelAccount = findViewById(R.id.ib_cancel_account);

        //set onclick listener for account
        et_account.setOnClickListener(selectAccountListener);
        ib_account.setOnClickListener(selectAccountListener);
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

    private void initDatePicker() {
        Log.i(TAG,"initDatePicker()");
        et_date = findViewById(R.id.et_date);
        ib_date = findViewById(R.id.ib_date);
        ib_date.setOnClickListener(dateClickListener);
        et_date.setOnClickListener(dateClickListener);

        date = Utilities.getTodayDateWithDefaultTime();
        //convert date to string & display in text view
        SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT_DAY_AND_DATE);
        String currentDateString = sdf.format(date);
        et_date.setText(currentDateString);
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
        
        //create date object
        date = Utilities.getDateWithDefaultTime(year,month,day);
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
                String accountNickName = data.getStringExtra(SELECTED_ACCOUNT_NAME);
                et_account.setText(accountNickName);
                ib_cancelAccount.setVisibility(View.VISIBLE);
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
                    if(debtToEdit==null){
                        //set default balance as zero
                        Double debtAmt = Double.valueOf(0);
                        if(!TextUtils.isEmpty(amount)){
                            debtAmt = Double.parseDouble(amount);
                        }

                        //generate UUID for transaction
                        UUID id = UUID.randomUUID();

                        //create debt & set result
                        Intent resultIntent = new Intent();
                        Debt debt = new Debt(id, debtAmt, date, description, type, account, person);
                        resultIntent.putExtra(GlobalConstants.DEBT_OBJECT, debt);
                        setResult(RESULT_OK, resultIntent);

                        Log.d(TAG, "Successfully added debt - " + debt.toString());

                        //finish activity
                        finish();

                    } else {

                        //set default balance as zero
                        Double debtAmt = Double.valueOf(0);
                        if(!TextUtils.isEmpty(amount)){
                            debtAmt = Double.parseDouble(amount);
                        }
                        Intent resultIntent = new Intent();
                        Debt newDebt =  debtToEdit.copy();
                        newDebt.setAmount(debtAmt);
                        newDebt.setPerson(person);
                        newDebt.setDate(date);
                        newDebt.setDescription(description);
                        newDebt.setType(type);
                        newDebt.setAccount(account);

                        resultIntent.putExtra(POSITION_ITEM_TO_EDIT, positionDebtToEdit);
                        resultIntent.putExtra(GlobalConstants.DEBT_OBJECT, newDebt);
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
        amount = String.valueOf(et_amount.getText()).trim();
        person = String.valueOf(et_person.getText()).trim();
        account = String.valueOf(et_account.getText()).trim();
        description = String.valueOf(et_description.getText()).trim();

        HashMap<String, Boolean> validInputs = new HashMap<>();

        validInputs.put(getString(R.string.amount), InputValidationUtilities.isValidString(amount));
        validInputs.put(getString(R.string.date), (date==null)?false:true);
        validInputs.put(getString(R.string.type), (type==null)?false:true);
        validInputs.put(getString(R.string.person), InputValidationUtilities.isValidString(person));
        validInputs.put(getString(R.string.account), InputValidationUtilities.isValidString(account));

        String emptyFields = Utilities.checkHashMapForFalseValues(validInputs);

        if (InputValidationUtilities.isValidString(emptyFields)) {
            Utilities.showEmptyFieldsErrorDialog(this, emptyFields);
            return false;
        } else {
            if(!InputValidationUtilities.isValidAmount(amount)){
                Utilities.showErrorFromKey(AddDebtActivity.this, getString(R.string.amount));
                return false;
            } else {
                return true;
            }
        }
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
