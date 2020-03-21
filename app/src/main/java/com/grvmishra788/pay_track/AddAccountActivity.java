package com.grvmishra788.pay_track;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.BankAccount;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.DigitalAccount;

import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.ACCOUNTS_TABLE_COL_NICK_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.BULLET_SYMBOL;
import static com.grvmishra788.pay_track.GlobalConstants.ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.POSITION_ITEM_TO_EDIT;

public class AddAccountActivity extends AppCompatActivity {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddAccountActivity.class.getName();

    private TextView tv_nickName, tv_type, tv_bankName, tv_serviceName, tv_accountNumber, tv_email, tv_mobile, tv_balance;
    private EditText et_nickName, et_bankName, et_serviceName, et_accountNumber, et_email, et_mobile, et_balance;
    private String nickName, bankName, serviceName, accountNumber, email, mobileNumber, accountBalance;

    private Spinner accountType;
    private TextView tv_submit;

    private CashAccount accountToEdit = null;
    private int positionAccountToEdit = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        initViews();
        initSpinner();
        initSubmitBtn();

        Intent activityStartingIntent = getIntent();
        if(activityStartingIntent.hasExtra(ITEM_TO_EDIT)){

            setTitle(R.string.title_edit_account);
            accountToEdit = (CashAccount) activityStartingIntent.getSerializableExtra(ITEM_TO_EDIT);
            positionAccountToEdit = activityStartingIntent.getIntExtra(POSITION_ITEM_TO_EDIT, -1);

            if(accountToEdit instanceof BankAccount){

                enableBankAccountInput();
                et_nickName.setText(accountToEdit.getNickName());
                et_bankName.setText(((BankAccount) accountToEdit).getBankName());
                et_accountNumber.setText(((BankAccount) accountToEdit).getAccountNumber());
                et_email.setText(((BankAccount) accountToEdit).getEmail());
                et_mobile.setText(((BankAccount) accountToEdit).getMobileNumber());
                et_balance.setText(String.valueOf(accountToEdit.getAccountBalance()));
                accountType.setSelection(0);

            }  else if (accountToEdit instanceof  DigitalAccount) {

                enableDigitalAccountInput();
                et_nickName.setText(accountToEdit.getNickName());
                et_serviceName.setText(((DigitalAccount) accountToEdit).getServiceName());
                et_email.setText(((DigitalAccount) accountToEdit).getEmail());
                et_mobile.setText(((DigitalAccount) accountToEdit).getMobileNumber());
                et_balance.setText(String.valueOf(accountToEdit.getAccountBalance()));
                accountType.setSelection(1);

            } else {

                enableCashAccountInput();
                et_nickName.setText(accountToEdit.getNickName());
                et_balance.setText(String.valueOf(accountToEdit.getAccountBalance()));
                accountType.setSelection(2);

            }

        } else {
            setTitle(R.string.title_add_account);
        }

        Log.i(TAG, "onCreate() ends!");
    }

    private void initSubmitBtn() {
        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashAccount account = null;
                if (parseInput()) {

                    if(accountToEdit == null) { //Add activity

                        if (Utilities.entryPresentInDB(AddAccountActivity.this, ACCOUNTS_TABLE, ACCOUNTS_TABLE_COL_NICK_NAME, nickName)) {
                            //show alert
                            String error = "\n" + BULLET_SYMBOL + " " + getString(R.string.nick_name) + " : " + nickName;
                            Utilities.showDuplicateFieldErrorDialog(AddAccountActivity.this, error);
                            Log.d(TAG, "Entry with  name - " + nickName + " already present in account table");
                        } else {
                            //set default balance as zero
                            Double balanceAcc = Double.valueOf(0);
                            if (!TextUtils.isEmpty(accountBalance)) {
                                balanceAcc = Double.parseDouble(accountBalance);
                            }
                            switch (accountType.getSelectedItemPosition()) {
                                case 0:
                                    Log.d(TAG, "Successfully added bank account");
                                    account = new BankAccount(nickName, balanceAcc, accountNumber, bankName, email, mobileNumber);
                                    break;
                                case 1:
                                    Log.d(TAG, "Successfully added digital account");
                                    account = new DigitalAccount(nickName, balanceAcc, serviceName, email, mobileNumber);
                                    break;
                                case 2:
                                    Log.d(TAG, "Successfully added cash account");
                                    account = new CashAccount(nickName, balanceAcc);
                                    break;
                                default:
                                    break;
                            }

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(GlobalConstants.ACCOUNT_OBJECT, account);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }

                    } else {  //Edit activity

                        if (!nickName.equals(accountToEdit.getNickName()) && Utilities.entryPresentInDB(AddAccountActivity.this, ACCOUNTS_TABLE, ACCOUNTS_TABLE_COL_NICK_NAME, nickName)) {
                            //show alert
                            String error = "\n" + BULLET_SYMBOL + " " + getString(R.string.nick_name) + " : " + nickName;
                            Utilities.showDuplicateFieldErrorDialog(AddAccountActivity.this, error);
                            Log.d(TAG, "Entry with  name - " + nickName + " already present in account table");
                        } else {

                            //set default balance as zero
                            Double balanceAcc = Double.valueOf(0);
                            if (!TextUtils.isEmpty(accountBalance)) {
                                balanceAcc = Double.parseDouble(accountBalance);
                            }
                            switch (accountType.getSelectedItemPosition()) {
                                case 0:
                                    Log.d(TAG, "Successfully edited account to bank account");
                                    accountToEdit = new BankAccount(nickName, balanceAcc, accountNumber, bankName, email, mobileNumber);
                                    break;
                                case 1:
                                    Log.d(TAG, "Successfully edited account to digital account");
                                    accountToEdit = new DigitalAccount(nickName, balanceAcc, serviceName, email, mobileNumber);
                                    break;
                                case 2:
                                    Log.d(TAG, "Successfully edited account to cash account");
                                    accountToEdit = new CashAccount(nickName, balanceAcc);
                                    break;
                                default:
                                    break;
                            }


                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(POSITION_ITEM_TO_EDIT, positionAccountToEdit);
                            resultIntent.putExtra(GlobalConstants.ACCOUNT_OBJECT, accountToEdit);
                            setResult(RESULT_OK, resultIntent);
                            finish();

                        }
                    }
                }
            }
        });
    }

    private boolean parseInput() {
        accountNumber = String.valueOf(et_accountNumber.getText()).trim();
        accountBalance = String.valueOf(et_balance.getText()).trim();
        bankName = String.valueOf(et_bankName.getText()).trim();
        serviceName = String.valueOf(et_serviceName.getText()).trim();
        email = String.valueOf(et_email.getText()).trim().toLowerCase();
        mobileNumber = String.valueOf(et_mobile.getText()).trim();
        nickName = String.valueOf(et_nickName.getText()).trim();

        HashMap<String, Boolean> validInputs = new HashMap<>();

        int accountTypeID = accountType.getSelectedItemPosition();
        switch (accountTypeID) {
            case 0:
                validInputs.put(getString(R.string.account_number), InputValidationUtilities.isValidString(accountNumber));
                validInputs.put(getString(R.string.bank_name), InputValidationUtilities.isValidString(bankName));
                validInputs.put(getString(R.string.email), InputValidationUtilities.isValidString(email));
                validInputs.put(getString(R.string.mobile), InputValidationUtilities.isValidString(mobileNumber));
                validInputs.put(getString(R.string.nick_name), InputValidationUtilities.isValidString(nickName));
                break;
            case 1:
                validInputs.put(getString(R.string.service_name), InputValidationUtilities.isValidString(serviceName));
                validInputs.put(getString(R.string.email), InputValidationUtilities.isValidString(email));
                validInputs.put(getString(R.string.mobile), InputValidationUtilities.isValidString(mobileNumber));
                validInputs.put(getString(R.string.nick_name), InputValidationUtilities.isValidString(nickName));
                break;
            case 2:
                validInputs.put(getString(R.string.nick_name), InputValidationUtilities.isValidString(nickName));
                break;
            default:
                validInputs = null;
                break;
        }

        String emptyFields = Utilities.checkHashMapForFalseValues(validInputs);

        if (InputValidationUtilities.isValidString(emptyFields)) {
            Utilities.showEmptyFieldsErrorDialog(this, emptyFields);
            return false;
        } else {
            validInputs.clear();
            switch (accountTypeID) {
                case 0:
                    validInputs.put(getString(R.string.account_number), InputValidationUtilities.isValidAccountNumber(accountNumber));
                    validInputs.put(getString(R.string.balance), InputValidationUtilities.isValidBalance(accountBalance));
                    validInputs.put(getString(R.string.email), InputValidationUtilities.isValidEmail(email));
                    validInputs.put(getString(R.string.mobile), InputValidationUtilities.isValidMobileNumber(mobileNumber));
                    break;
                case 1:
                    validInputs.put(getString(R.string.balance), InputValidationUtilities.isValidBalance(accountBalance));
                    validInputs.put(getString(R.string.email), InputValidationUtilities.isValidEmail(email));
                    validInputs.put(getString(R.string.mobile), InputValidationUtilities.isValidMobileNumber(mobileNumber));
                    break;
                case 2:
                    validInputs.put(getString(R.string.balance), InputValidationUtilities.isValidBalance(accountBalance));
                    break;
                default:
                    validInputs = null;
                    break;
            }

            return !Utilities.hasSpecificKeyError(AddAccountActivity.this, validInputs);
        }
    }

    private void initSpinner() {
        accountType = (Spinner) findViewById(R.id.spinner_type);
        // Create an ArrayAdapter using the string array and a custom spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_type, R.layout.layout_custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        accountType.setAdapter(adapter);
        accountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected() - index : " + i);
                switch (i) {
                    case 0:
                        enableBankAccountInput();
                        break;
                    case 1:
                        enableDigitalAccountInput();
                        break;
                    case 2:
                        enableCashAccountInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void enableDigitalAccountInput() {
        Log.i(TAG, "enableDigitalAccountInput()");
        tv_accountNumber.setVisibility(View.GONE);
        tv_balance.setVisibility(View.VISIBLE);
        tv_bankName.setVisibility(View.GONE);
        tv_serviceName.setVisibility(View.VISIBLE);
        tv_email.setVisibility(View.VISIBLE);
        tv_mobile.setVisibility(View.VISIBLE);
        tv_nickName.setVisibility(View.VISIBLE);
        tv_type.setVisibility(View.VISIBLE);

        et_accountNumber.setVisibility(View.GONE);
        et_balance.setVisibility(View.VISIBLE);
        et_bankName.setVisibility(View.GONE);
        et_serviceName.setVisibility(View.VISIBLE);
        et_email.setVisibility(View.VISIBLE);
        et_mobile.setVisibility(View.VISIBLE);
        et_nickName.setVisibility(View.VISIBLE);

    }

    private void enableCashAccountInput() {
        Log.i(TAG, "enableCashAccountInput()");
        tv_accountNumber.setVisibility(View.GONE);
        tv_balance.setVisibility(View.VISIBLE);
        tv_bankName.setVisibility(View.GONE);
        tv_serviceName.setVisibility(View.GONE);
        tv_email.setVisibility(View.GONE);
        tv_mobile.setVisibility(View.GONE);
        tv_nickName.setVisibility(View.VISIBLE);
        tv_type.setVisibility(View.VISIBLE);

        et_accountNumber.setVisibility(View.GONE);
        et_balance.setVisibility(View.VISIBLE);
        et_bankName.setVisibility(View.GONE);
        et_serviceName.setVisibility(View.GONE);
        et_email.setVisibility(View.GONE);
        et_mobile.setVisibility(View.GONE);
        et_nickName.setVisibility(View.VISIBLE);

    }

    private void enableBankAccountInput() {
        Log.i(TAG, "enableBankAccountInput()");
        tv_accountNumber.setVisibility(View.VISIBLE);
        tv_balance.setVisibility(View.VISIBLE);
        tv_bankName.setVisibility(View.VISIBLE);
        tv_serviceName.setVisibility(View.GONE);
        tv_email.setVisibility(View.VISIBLE);
        tv_mobile.setVisibility(View.VISIBLE);
        tv_nickName.setVisibility(View.VISIBLE);
        tv_type.setVisibility(View.VISIBLE);

        et_accountNumber.setVisibility(View.VISIBLE);
        et_balance.setVisibility(View.VISIBLE);
        et_bankName.setVisibility(View.VISIBLE);
        et_serviceName.setVisibility(View.GONE);
        et_email.setVisibility(View.VISIBLE);
        et_mobile.setVisibility(View.VISIBLE);
        et_nickName.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        tv_accountNumber = findViewById(R.id.tv_account_number);
        tv_balance = findViewById(R.id.tv_balance);
        tv_bankName = findViewById(R.id.tv_bank_name);
        tv_serviceName = findViewById(R.id.tv_service_name);
        tv_email = findViewById(R.id.tv_email);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_nickName = findViewById(R.id.tv_nick_name);
        tv_type = findViewById(R.id.tv_type);

        et_accountNumber = findViewById(R.id.et_account_number);
        et_balance = findViewById(R.id.et_balance);
        et_bankName = findViewById(R.id.et_bank_name);
        et_serviceName = findViewById(R.id.et_service_name);
        et_email = findViewById(R.id.et_email);
        et_mobile = findViewById(R.id.et_mobile);
        et_nickName = findViewById(R.id.et_nick_name);

        //set onFocusChangeListener for editTexts
        et_accountNumber.setOnFocusChangeListener(onFocusChangeListener);
        et_balance.setOnFocusChangeListener(onFocusChangeListener);
        et_bankName.setOnFocusChangeListener(onFocusChangeListener);
        et_serviceName.setOnFocusChangeListener(onFocusChangeListener);
        et_email.setOnFocusChangeListener(onFocusChangeListener);
        et_mobile.setOnFocusChangeListener(onFocusChangeListener);
        et_nickName.setOnFocusChangeListener(onFocusChangeListener);

    }

    private EditText.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            Log.d(TAG, "onFocusChange() - viewID: " + view.getId() + " hasFocus:" + hasFocus);
            if (hasFocus) {
                //make hint disappear on edit view focus
                ((EditText) view).setHint("");
            } else {
                String hint = "";
                switch (view.getId()) {
                    case R.id.et_account_number:
                        hint = getString(R.string.account_number);
                        break;
                    case R.id.et_balance:
                        hint = getString(R.string.balance);
                        break;
                    case R.id.et_bank_name:
                        hint = getString(R.string.bank_name);
                        break;
                    case R.id.et_service_name:
                        hint = getString(R.string.service_name);
                        break;
                    case R.id.et_email:
                        hint = getString(R.string.email);
                        break;
                    case R.id.et_mobile:
                        hint = getString(R.string.mobile);
                        break;
                    case R.id.et_nick_name:
                        hint = getString(R.string.nick_name);
                        break;
                    default:
                        hint = "";
                }
                ((EditText) view).setHint(hint);
            }
        }
    };
}
