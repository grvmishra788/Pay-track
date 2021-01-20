package com.grvmishra788.pay_track;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;
import com.grvmishra788.pay_track.DS.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.grvmishra788.pay_track.GlobalConstants.DEFAULT_FORMAT_DAY_AND_DATE;
public class AnalyzeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AnalyzeActivity.class.getName();

    private DbHelper payTrackDBHelper;
    private ArrayList<Transaction> mTransactions;

    private HashMap<Date, ArrayList<Transaction>> filterTransactionHashMap;
    private HashMap<Category, ArrayList<Transaction>> filterCategoryTransactionHashMap;
    private HashMap<SubCategory, ArrayList<Transaction>> filterSubCategoryTransactionHashMap;

    private CardView filterLayout;
    private Spinner filterBy;
    private EditText et_startDate, et_endDate;
    private ImageButton ib_startDate, ib_endDate;
    private Date startDate, endDate;
    private int dateType = -1;
    private GlobalConstants.Filter type = GlobalConstants.Filter.BY_DATE;

    //recyclerView variables
    private RecyclerView analyzeRecyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> analyzeRecyclerViewAdapter;
    private AnalyzeAdapter dateAdapter;
    private AnalyzeCategoryAdapter categoryAdapter;

    private RecyclerView.LayoutManager analyzeRecyclerViewLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        setTitle(R.string.title_analyze);

        //init view filter layout
        filterLayout = findViewById(R.id.layout_filter);

        //init db
        payTrackDBHelper = new DbHelper(this);

        //init transactions list
        mTransactions = payTrackDBHelper.getAllTransactions();
        if(mTransactions == null){
            Log.d(TAG, "mTransactions is null");
            mTransactions = new ArrayList<>();
        }

        if(mTransactions.size()==0){
            Log.d(TAG, "No transactions present!");
            filterLayout.setVisibility(View.GONE);

        } else {
            initFilterSpinner();
            initDatePickers();
            //init filterTransactionHashMap
            initFilterTransactionHashMap();
            initFilterCategoryTransactionHashMap();
            initFilterSubCategoryTransactionHashMap();
        }

        //init RecyclerView
        analyzeRecyclerView = (RecyclerView) findViewById(R.id.analyze_recycler_view);
        analyzeRecyclerView.setHasFixedSize(true);
        analyzeRecyclerViewLayoutManager = new LinearLayoutManager(this);
        dateAdapter = new AnalyzeAdapter(this, filterTransactionHashMap);
        categoryAdapter = new AnalyzeCategoryAdapter(this, filterCategoryTransactionHashMap, filterSubCategoryTransactionHashMap);
        if(type== GlobalConstants.Filter.BY_DATE) {
            analyzeRecyclerViewAdapter = dateAdapter;
        } else if (type == GlobalConstants.Filter.BY_CATEGORY) {
            analyzeRecyclerViewAdapter = categoryAdapter;
            ((AnalyzeCategoryAdapter)analyzeRecyclerViewAdapter).setFilterSubCategoryTransactionHashMap(filterSubCategoryTransactionHashMap);
        }
        analyzeRecyclerView.setLayoutManager(analyzeRecyclerViewLayoutManager);
        analyzeRecyclerView.setAdapter(analyzeRecyclerViewAdapter);

    }

    private void initFilterSpinner() {
        Log.i(TAG,"initSpinner()");
        //FILTER-TYPE SPINNER
        filterBy = (Spinner) findViewById(R.id.spinner_type);
        // Create an ArrayAdapter using the string array and a custom spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.analyze_filterBy, R.layout.layout_custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        filterBy.setAdapter(adapter);
        filterBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected() - index : " + i);
                switch (i) {
                    case 0:
                        type = GlobalConstants.Filter.BY_CATEGORY;
                        analyzeRecyclerViewAdapter = categoryAdapter;
                        break;
                    default:
                        type = GlobalConstants.Filter.BY_DATE;
                        analyzeRecyclerViewAdapter = dateAdapter;
                        break;
                }
                analyzeRecyclerView.setAdapter(analyzeRecyclerViewAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initDatePickers() {
        Log.i(TAG,"initDatePickers()");

        et_startDate = findViewById(R.id.et_start_date);
        ib_startDate = findViewById(R.id.ib_start_date);
        ib_startDate.setOnClickListener(dateClickListener);
        et_startDate.setOnClickListener(dateClickListener);

        et_endDate = findViewById(R.id.et_end_date);
        ib_endDate = findViewById(R.id.ib_end_date);
        ib_endDate.setOnClickListener(dateClickListener);
        et_endDate.setOnClickListener(dateClickListener);

        endDate = Utilities.getTodayDateWithDefaultTime();
        startDate = Utilities.getOneYearBackwardDate(endDate);
        //convert start date to string & display in text view
        SimpleDateFormat sdf=new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(this));
        String currentDateTimeString = sdf.format(startDate);
        et_startDate.setText(currentDateTimeString);
        //convert end date to string & display in text view
        currentDateTimeString = sdf.format(endDate);
        et_endDate.setText(currentDateTimeString);
    }

    private void initFilterTransactionHashMap() {
        if(filterTransactionHashMap==null)
            filterTransactionHashMap = new HashMap<>();
        else
            filterTransactionHashMap.clear();

        if(startDate==null || endDate==null){
            Log.e(TAG,"Either or both of start and end dates are null!");
            return;
        }
        if(mTransactions!=null){
            for(Transaction transaction:mTransactions) {
                if(transaction.getDate().before(startDate) || transaction.getDate().after(endDate)){
                    continue;
                }
                addTransactionToFilterHashMap(transaction);
            }
        } else {
            Log.d(TAG, "mTransactions is null or empty!");
        }
    }

    private void addTransactionToFilterHashMap(Transaction transaction) {
        Date dateOfTransaction = transaction.getDate();
        if(filterTransactionHashMap==null){
            filterTransactionHashMap = new HashMap<>();
        }
        if(dateOfTransaction!=null){
            Date monthStartDate = Utilities.getStartDateOfMonthWithDefaultTime(dateOfTransaction);
            ArrayList<Transaction> curMonthTransactions = null;
            if(filterTransactionHashMap.containsKey(monthStartDate)){
                curMonthTransactions = filterTransactionHashMap.get(monthStartDate);
            }
            if (curMonthTransactions == null) {
                curMonthTransactions = new ArrayList<>();
            }
            curMonthTransactions.add(transaction);
            filterTransactionHashMap.put(monthStartDate, curMonthTransactions);

        } else {
            Log.e(TAG,"Date of " + transaction.toString() + " is null!");
        }
    }

    private void initFilterCategoryTransactionHashMap() {
        if(filterCategoryTransactionHashMap==null)
            filterCategoryTransactionHashMap = new HashMap<>();
        else
            filterCategoryTransactionHashMap.clear();

        if(startDate==null || endDate==null){
            Log.e(TAG,"Either or both of start and end dates are null!");
            return;
        }
        if(mTransactions!=null){
            for(Transaction transaction:mTransactions) {
                if(transaction.getDate().before(startDate) || transaction.getDate().after(endDate)){
                    continue;
                }
                addTransactionToFilterCategoryHashMap(transaction);
            }
        } else {
            Log.d(TAG, "mTransactions is null or empty!");
        }
    }

    private void addTransactionToFilterCategoryHashMap(Transaction transaction) {
        Category categoryOfTransaction = payTrackDBHelper.getCategory(transaction.getCategory());
        if(filterCategoryTransactionHashMap==null){
            filterCategoryTransactionHashMap = new HashMap<>();
        }
        if(categoryOfTransaction!=null){
            ArrayList<Transaction> curMonthTransactions = null;
            if(filterCategoryTransactionHashMap.containsKey(categoryOfTransaction)){
                curMonthTransactions = filterCategoryTransactionHashMap.get(categoryOfTransaction);
            }
            if (curMonthTransactions == null) {
                curMonthTransactions = new ArrayList<>();
            }
            curMonthTransactions.add(transaction);
            filterCategoryTransactionHashMap.put(categoryOfTransaction, curMonthTransactions);

        } else {
            Log.e(TAG,"Category of " + transaction.toString() + " is null!");
        }
    }

    private void initFilterSubCategoryTransactionHashMap() {
        if(filterSubCategoryTransactionHashMap==null)
            filterSubCategoryTransactionHashMap = new HashMap<>();
        else
            filterSubCategoryTransactionHashMap.clear();

        if(startDate==null || endDate==null){
            Log.e(TAG,"Either or both of start and end dates are null!");
            return;
        }
        if(mTransactions!=null){
            for(Transaction transaction:mTransactions) {
                if(transaction.getDate().before(startDate) || transaction.getDate().after(endDate)){
                    continue;
                }
                addTransactionToFilterSubCategoryHashMap(transaction);
            }
        } else {
            Log.d(TAG, "mTransactions is null or empty!");
        }
    }

    private void addTransactionToFilterSubCategoryHashMap(Transaction transaction) {
        SubCategory subCategoryOfTransaction = payTrackDBHelper.getSubCategory(transaction.getSubCategory());
        if(filterSubCategoryTransactionHashMap==null){
            filterSubCategoryTransactionHashMap = new HashMap<>();
        }
        if(subCategoryOfTransaction!=null){
            ArrayList<Transaction> curSubCategoryTransactions = null;
            if(filterSubCategoryTransactionHashMap.containsKey(subCategoryOfTransaction)){
                curSubCategoryTransactions = filterSubCategoryTransactionHashMap.get(subCategoryOfTransaction);
            }
            if (curSubCategoryTransactions == null) {
                curSubCategoryTransactions = new ArrayList<>();
            }
            curSubCategoryTransactions.add(transaction);
            filterSubCategoryTransactionHashMap.put(subCategoryOfTransaction, curSubCategoryTransactions);

        } else {
            Log.e(TAG,"SubCategory of " + transaction.toString() + " is null!");
        }
    }


    private View.OnClickListener dateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dateType = view.getId();
            if(dateType!=-1 && (dateType==et_endDate.getId() || dateType==ib_endDate.getId())){
                Log.d(TAG, "onClickListener called for end date");
                if(startDate==null){
                    Toast.makeText(AnalyzeActivity.this, "You must select start date first!", Toast.LENGTH_SHORT).show();
                } else {
                    DialogFragment mDatePicker = new DatePickerFragment(startDate);
                    mDatePicker.show(getSupportFragmentManager(), "Start Date Picker Dialog");
                }
                Log.d(TAG, "onClickListener finished for end date");
            } else if(dateType!=-1 && (dateType==et_startDate.getId() || dateType==ib_startDate.getId())) {
                DialogFragment mDatePicker = new DatePickerFragment();
                mDatePicker.show(getSupportFragmentManager(), "End Date Picker Dialog");
            }
        }
    };

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.i(TAG, "OnDateSetListener() called");

        //create date object
        if(dateType!=-1 && (dateType==et_startDate.getId() || dateType==ib_startDate.getId())){
            startDate = Utilities.getDateWithDefaultTime(year, month, day);
            //convert date to string & display in text view
            SimpleDateFormat sdf=new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(this));
            String currentDateTimeString = sdf.format(startDate);
            et_startDate.setText(currentDateTimeString);
            if(endDate!=null && endDate.before(startDate)){
                endDate = Utilities.getOneYearForwardDate(endDate);
                //convert end date to string & display in text view
               currentDateTimeString = sdf.format(endDate);
                et_endDate.setText(currentDateTimeString);
            }
            Log.d(TAG, "OnDateSetListener() call completed - date : " + currentDateTimeString);
        } else if (dateType!=-1 && (dateType==et_endDate.getId() || dateType==ib_endDate.getId())){
            endDate = Utilities.getDateWithDefaultTime(year, month, day);
            //convert date to string & display in text view
            SimpleDateFormat sdf=new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(this));
            String currentDateTimeString = sdf.format(endDate);
            et_endDate.setText(currentDateTimeString);
            Log.d(TAG, "OnDateSetListener() call completed - date : " + currentDateTimeString);
        }

        initFilterTransactionHashMap();
        initFilterCategoryTransactionHashMap();
        initFilterSubCategoryTransactionHashMap();

        if(type== GlobalConstants.Filter.BY_DATE) {
            ((AnalyzeAdapter)analyzeRecyclerViewAdapter).setFilterTransactionHashMap(filterTransactionHashMap);
            ((AnalyzeAdapter)analyzeRecyclerViewAdapter).refreshMonthsList();
        } else if (type == GlobalConstants.Filter.BY_CATEGORY) {
            ((AnalyzeCategoryAdapter)analyzeRecyclerViewAdapter).setFilterTransactionHashMap(filterCategoryTransactionHashMap);
            ((AnalyzeCategoryAdapter)analyzeRecyclerViewAdapter).setFilterSubCategoryTransactionHashMap(filterSubCategoryTransactionHashMap);
            ((AnalyzeCategoryAdapter)analyzeRecyclerViewAdapter).refreshCategoriesList();
        }

    }
}
