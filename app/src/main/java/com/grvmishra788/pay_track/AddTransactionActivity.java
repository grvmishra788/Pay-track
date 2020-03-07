package com.grvmishra788.pay_track;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.TransactionCategory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class AddTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddTransactionActivity.class.getName();

    private TextView tv_amount, tv_type, tv_description, tv_date;
    private EditText et_amount, et_description, et_date;
    private Spinner transactionType;

    private long amount;
    private TransactionCategory transactionCategory;
    private Date date;
    private String description;
    private GlobalConstants.TransactionType type;
    private CashAccount account;

    private ImageButton ib_date;

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
        Log.i(TAG, "onCreate() ends!");
    }

    private void initDatePicker() {
        et_date = findViewById(R.id.et_date);
        ib_date = findViewById(R.id.ib_date);
        ib_date.setOnClickListener(dateClickListener);
        et_date.setOnClickListener(dateClickListener);
    }

    private void initViews() {
        tv_amount = findViewById(R.id.tv_amount);
        tv_description = findViewById(R.id.tv_description);
        tv_date = findViewById(R.id.tv_date);

        et_amount = findViewById(R.id.et_amount);
        et_description = findViewById(R.id.et_description);
        et_date = findViewById(R.id.et_date);
    }

    private void initSpinner() {
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
                    case 0:
                        break;
                    case 1:
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.d(TAG, "OnDateSetListener() called");

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

        Log.d(TAG, "OnDateSetListener() call completed");

    }
}
