package com.grvmishra788.pay_track;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.DS.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import static android.app.Activity.RESULT_OK;
import static com.grvmishra788.pay_track.GlobalConstants.DATE_SORT_RECENT_LAST;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_TRANSACTION;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_EDIT_TRANSACTION;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_ITEM_TO_EDIT;

public class TransactionsFragment extends Fragment {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + TransactionsFragment.class.getName();

    private FloatingActionButton addTransactionButton;

    //Transactions list
    private ArrayList<Transaction> mTransactions;
    private HashMap<Date, ArrayList<Transaction>> filterTransactionHashMap; // month -> transactions
    private SortedList<Date> filterMonths;
    private HashMap<String, ArrayList<Transaction>> filterCategoryTransactionHashMap; // category -> transactions
    private SortedList<String> filterCategories;
    private HashMap<Date, ArrayList<Transaction>> datedTransactionHashMap; // date -> transactions

    //recyclerView variables
    private RecyclerView transactionsRecyclerView;
    private TransactionsAdapter transactionsRecyclerViewAdapter;
    private RecyclerView.LayoutManager transactionsRecyclerViewLayoutManager;

    // fields to help keep track of app’s state for Contextual Action Mode
    private boolean isMultiSelect = false;
    private TreeSet<Integer> selectedItems = new TreeSet<>();
    private ActionMode actionMode;

    //Variable to store transactions when launching Contextual action mode
    private TreeSet<Transaction> selectedTransactions = new TreeSet<>();

    //Variable to store filter type - BY_MONTH or BY_CATEGORY
    private GlobalConstants.Filter type = GlobalConstants.Filter.BY_MONTH;

    //Variable to store date range
    private Spinner filterTransactions;
    private ArrayAdapter<String> filterAdapter;
    private ArrayList<String> months;
    private ArrayList<String> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() starts...");
        View view = inflater.inflate(R.layout.layout_transactions_fragment, container, false);

        //init transasctions list
        mTransactions = ((MainActivity) getActivity()).getPayTrackDBHelper().getAllTransactions();
        if(mTransactions == null){
            Log.d(TAG, "mTransactions is null");
            mTransactions = new ArrayList<>();
        }

        //init filterTransactionHashMap and filterCategoryTransactionHashMap
        filterTransactionHashMap = new HashMap<>();
        filterCategoryTransactionHashMap = new HashMap<>();
        initFilterTransactionHashMaps();

        //init datedTransactionHashMap
        datedTransactionHashMap = new HashMap<>();
        initDatedTransactionHashMap();

        //init RecyclerView
        transactionsRecyclerView = (RecyclerView) view.findViewById(R.id.show_transaction_recycler_view);
        transactionsRecyclerView.setHasFixedSize(true);
        transactionsRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        transactionsRecyclerViewAdapter = new TransactionsAdapter(getContext(), datedTransactionHashMap);
        transactionsRecyclerView.setLayoutManager(transactionsRecyclerViewLayoutManager);
        transactionsRecyclerView.setAdapter(transactionsRecyclerViewAdapter);

        transactionsRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                //Response to click on grouped item only if multi-select is on
                if (isMultiSelect) {
                    //if multiple selection is enabled then select item on single click
                    selectMultiple(position);
                }
            }

            @Override
            public void onItemLongClick(int position) {
                Log.d(TAG, "onItemLongClick called at position - " + position);
                if (!isMultiSelect) {
                    //init select items and isMultiSelect on long click
                    selectedItems = new TreeSet<>();
                    selectedTransactions = new TreeSet<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        //show ActionMode on long click
                        actionMode = ((AppCompatActivity)getContext()).startSupportActionMode(actionModeCallbacks);
                    }
                    selectMultiple(position);
                }
            }
        });

        transactionsRecyclerViewAdapter.setOnTransactionClickListener(new OnTransactionClickListener() {
            @Override
            public void onItemClick(int position, Transaction transaction) {
                if (!isMultiSelect) {
                    Intent editActivityIntent = new Intent(getActivity(), AddTransactionActivity.class);
                    editActivityIntent.putExtra(SUB_ITEM_TO_EDIT, transaction);
                    startActivityForResult(editActivityIntent, REQ_CODE_EDIT_TRANSACTION);
                } else {
                    selectMultiple(transaction);
                }
            }

            @Override
            public void onItemLongClick(int position, Transaction transaction) {
                Log.d(TAG, "onItemLongClick called at position - " + position);
                if (!isMultiSelect) {
                    //init select items and isMultiSelect on long click
                    selectedItems = new TreeSet<>();
                    selectedTransactions = new TreeSet<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        //show ActionMode on long click
                        actionMode = ((AppCompatActivity)getContext()).startSupportActionMode(actionModeCallbacks);
                    }
                }
                selectMultiple(transaction);
            }
        });


        addTransactionButton = view.findViewById(R.id.addItemFAB);
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addTransactionButton::onClick()");
                Intent addTransactionIntent = new Intent(getActivity(), AddTransactionActivity.class);
                startActivityForResult(addTransactionIntent, REQ_CODE_ADD_TRANSACTION);
            }
        });

        initSpinner(view);

        Log.i(TAG, "onCreateView() ends!");
        return view;
    }

    private void initSpinner(View v) {
        Log.i(TAG,"initSpinner()");
        filterTransactions = (Spinner) v.findViewById(R.id.spinner_filter);
        //init list of months
        initMonthsAndCategoriesList();
        //set visibility for spinner
        if((type== GlobalConstants.Filter.BY_MONTH && months.size()==0) || (type== GlobalConstants.Filter.BY_CATEGORY && categories.size()==0)){
            filterTransactions.setVisibility(View.GONE);
        } else if(filterTransactions.getVisibility()==View.GONE){
            filterTransactions.setVisibility(View.VISIBLE);
        }
        // Create an ArrayAdapter using the string array and a custom spinner layout
        if((type== GlobalConstants.Filter.BY_CATEGORY && categories.size()!=0)){
            filterAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_custom_spinner_item_transpose, categories);
        } else {
            filterAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_custom_spinner_item_transpose, months);
        }
        // Specify the layout to use when the list of choices appears
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        filterTransactions.setAdapter(filterAdapter);
        filterTransactions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected() - index : " + i);
                transactionsRecyclerViewAdapter.setSelectedMonthOrCategoryString(filterAdapter.getItem(i));
                transactionsRecyclerViewAdapter.setTypeAndInitDatedHM(type);
                transactionsRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initMonthsAndCategoriesList() {
        //INIT MONTHS
        months = new ArrayList<>();
        for (int i=0; i<filterMonths.size();i++){
            Date date = filterMonths.get(i);
            SimpleDateFormat sdf = PreferenceUtils.getDefaultMonthAndYearFormat(getContext());
            String dateString = sdf.format(date);
            months.add(dateString);
        }

        //INIT CATEGORIES
        categories = new ArrayList<>();
        for (int i=0; i<filterCategories.size();i++){
            String categoryString = filterCategories.get(i);
            categories.add(categoryString);
        }

    }

    private void refreshSpinner(){
        int oldPos = -1, newPos=-1;
        String selectedItem = "";
        if(filterTransactions!=null && filterAdapter!=null){
            selectedItem = (String) filterTransactions.getSelectedItem();
            oldPos = filterAdapter.getPosition(selectedItem);
        }
        createSortedMonthsAndCategoriesList();
        initMonthsAndCategoriesList();
        if(filterTransactions!=null){
            //set visibility for spinner
            if((type== GlobalConstants.Filter.BY_MONTH && months.size()==0) || (type== GlobalConstants.Filter.BY_CATEGORY && categories.size()==0)){
                filterTransactions.setVisibility(View.GONE);
            } else if(filterTransactions.getVisibility()==View.GONE){
                filterTransactions.setVisibility(View.VISIBLE);
            }
        }
        if(filterAdapter!=null) {
            filterAdapter.clear();
            if (type== GlobalConstants.Filter.BY_CATEGORY) {
                filterAdapter.addAll(categories);
            } else {
                filterAdapter.addAll(months);
            }

            filterAdapter.notifyDataSetChanged();
            int size = 0;
            if(InputValidationUtilities.isValidString(selectedItem)){
                if(type== GlobalConstants.Filter.BY_CATEGORY) {
                    newPos = categories.indexOf(selectedItem);
                    size = categories.size();
                }
                else {
                    newPos = months.indexOf(selectedItem);
                    size = months.size();
                }
            }
            if(newPos>=0){
                filterTransactions.setSelection(newPos);
            } else if(size>0){
                filterTransactions.setSelection(0); //select 0th index if index missing
                selectedItem = (String) filterTransactions.getSelectedItem();
                transactionsRecyclerViewAdapter.setSelectedMonthOrCategoryString(selectedItem);
                transactionsRecyclerViewAdapter.setTypeAndInitDatedHM(type);
                transactionsRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_ADD_TRANSACTION){
                Log.i(TAG, "Processing add transaction...");
                Transaction transaction =  (Transaction) data.getSerializableExtra(GlobalConstants.TRANSACTION_OBJECT);
                addTransaction(transaction);
            } else if(requestCode==REQ_CODE_EDIT_TRANSACTION){

                Transaction oldTransaction = (Transaction) data.getSerializableExtra(GlobalConstants.OLD_TRANSACTION_OBJECT);
                Transaction newTransaction = (Transaction) data.getSerializableExtra(GlobalConstants.NEW_TRANSACTION_OBJECT);
                if (((MainActivity) getActivity()).getPayTrackDBHelper().updateDataInTransactionsTable(oldTransaction, newTransaction)) {
                    Log.d(TAG, "Transaction updated in db - FROM : " + oldTransaction.toString() + " TO : " + newTransaction.toString());
                } else {
                    Log.e(TAG, "Couldn't update Transaction to db - " + oldTransaction.toString());
                }
                mTransactions.remove(oldTransaction);
                mTransactions.add(newTransaction);

                removeTransactionFromFilterHashMaps(oldTransaction);
                refreshSpinner();
                removeTransactionFromHashMap(oldTransaction);
                transactionsRecyclerViewAdapter.setTypeAndInitDatedHM(type);
                transactionsRecyclerViewAdapter.notifyDataSetChanged();
                addTransactionToFilterHashMaps(newTransaction);
                refreshSpinner();
                addTransactionToDatedHashMap(newTransaction);
                transactionsRecyclerViewAdapter.setTypeAndInitDatedHM(type);
                transactionsRecyclerViewAdapter.notifyDataSetChanged();

            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }
    }

    private void addTransactionToFilterHashMaps(Transaction transaction) {
        //Add to filterTransactionHashMap
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

        //Add to filterCategoryTransactionHashMap
        String categoryOfTransaction = transaction.getCategory();
        String subCategoryOfTransaction = transaction.getSubCategory();
        String overallCategory = categoryOfTransaction;
        if(InputValidationUtilities.isValidString(subCategoryOfTransaction)){
            overallCategory+="/"+subCategoryOfTransaction;
        }

        if(filterCategoryTransactionHashMap==null){
            filterCategoryTransactionHashMap = new HashMap<>();
        }
        if(InputValidationUtilities.isValidString(overallCategory)){
            ArrayList<Transaction> curCategoryTransactions = null;
            if(filterCategoryTransactionHashMap.containsKey(overallCategory)){
                curCategoryTransactions = filterCategoryTransactionHashMap.get(overallCategory);
            }
            if (curCategoryTransactions == null) {
                curCategoryTransactions = new ArrayList<>();
            }
            curCategoryTransactions.add(transaction);
            filterCategoryTransactionHashMap.put(overallCategory, curCategoryTransactions);

        } else {
            Log.e(TAG,"Category of " + transaction.toString() + " is null!");
        }
    }


    private void addTransactionToDatedHashMap(Transaction transaction) {
        Date dateOfTransaction = transaction.getDate();
        if(datedTransactionHashMap==null){
            datedTransactionHashMap = new HashMap<>();
        }
        if(dateOfTransaction!=null){
            ArrayList<Transaction> curDateTransactions = null;
            if(datedTransactionHashMap.containsKey(dateOfTransaction)){
                curDateTransactions = datedTransactionHashMap.get(dateOfTransaction);
            }
            if (curDateTransactions == null) {
                curDateTransactions = new ArrayList<>();
            }
            curDateTransactions.add(transaction);
            datedTransactionHashMap.put(dateOfTransaction, curDateTransactions);

        } else {
            Log.e(TAG,"Date of " + transaction.toString() + " is null!");
        }
    }

    private void removeTransactionFromFilterHashMaps(Transaction transaction) {
        //Remove from filterTransactionHashMap
        Date dateOfTransaction = transaction.getDate();
        if(dateOfTransaction!=null){
            ArrayList<Transaction> curDateTransactions = null;
            Date monthStartDate = Utilities.getStartDateOfMonthWithDefaultTime(dateOfTransaction);
            if(filterTransactionHashMap.containsKey(monthStartDate)){
                curDateTransactions = filterTransactionHashMap.get(monthStartDate);
                for(int i=0; i<curDateTransactions.size(); i++){
                    if(curDateTransactions.get(i).getId().equals(transaction.getId())){
                        curDateTransactions.remove(i);
                        break;
                    }
                }
                if(curDateTransactions==null || curDateTransactions.size()==0){
                    filterTransactionHashMap.remove(monthStartDate);
                }
            }
        } else {
            Log.e(TAG,"Date of " + transaction.toString() + " is null!");
        }

        //Remove from filterCategoryTransactionHashMap
        String categoryOfTransaction = transaction.getCategory();
        String subCategoryOfTransaction = transaction.getSubCategory();
        String overallCategory = categoryOfTransaction;
        if(InputValidationUtilities.isValidString(subCategoryOfTransaction)){
            overallCategory+="/"+subCategoryOfTransaction;
        }
        if(InputValidationUtilities.isValidString(overallCategory)){
            ArrayList<Transaction> curCategoryTransactions = null;
            if(filterCategoryTransactionHashMap.containsKey(overallCategory)){
                curCategoryTransactions = filterCategoryTransactionHashMap.get(overallCategory);
                for(int i=0; i<curCategoryTransactions.size(); i++){
                    if(curCategoryTransactions.get(i).getId().equals(transaction.getId())){
                        curCategoryTransactions.remove(i);
                        break;
                    }
                }
                if(curCategoryTransactions==null || curCategoryTransactions.size()==0){
                    filterCategoryTransactionHashMap.remove(overallCategory);
                }
            }
        } else {
            Log.e(TAG,"Category of " + transaction.toString() + " is null!");
        }
    }

    private void removeTransactionFromHashMap(Transaction transaction) {
        Date dateOfTransaction = transaction.getDate();
        if(dateOfTransaction!=null){
            ArrayList<Transaction> curDateTransactions = null;
            if(datedTransactionHashMap.containsKey(dateOfTransaction)){
                curDateTransactions = datedTransactionHashMap.get(dateOfTransaction);
                for(int i=0; i<curDateTransactions.size();i++){
                    if(curDateTransactions.get(i).getId().equals(transaction.getId())){
                        curDateTransactions.remove(i);
                        break;
                    }
                }
                if(curDateTransactions==null || curDateTransactions.size()==0){
                    datedTransactionHashMap.remove(dateOfTransaction);
                }
            }
        } else {
            Log.e(TAG,"Date of " + transaction.toString() + " is null!");
        }
    }

    private void initFilterTransactionHashMaps() {
        if(mTransactions!=null){
            for(Transaction transaction:mTransactions){
                addTransactionToFilterHashMaps(transaction);
            }
            refreshSpinner();
        } else {
            Log.d(TAG, "mTransactions is null or empty!");
        }
    }

    private void createSortedMonthsAndCategoriesList() {
        //CREATE MONTHS LIST
        List<Date> monthsList = new ArrayList<Date>(filterTransactionHashMap.keySet()); // <== Set to List
        if(filterMonths==null)
            filterMonths = new SortedList<Date>(Date.class, new SortedList.Callback<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                if(PreferenceUtils.getDefaultDateSortType(getContext())==DATE_SORT_RECENT_LAST)
                    return o1.compareTo(o2); // o1 compares to o2 for ascending order
                else
                    return o2.compareTo(o1); // o2 compares to o1 for descending order
            }

            @Override
            public void onChanged(int position, int count) {
            }

            @Override
            public boolean areContentsTheSame(Date oldItem, Date newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Date item1, Date item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
            }

            @Override
            public void onRemoved(int position, int count) {
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
            }
        });
        filterMonths.clear();
        filterMonths.beginBatchedUpdates();
        for (int i = 0; i < monthsList.size(); i++) {
            filterMonths.add(monthsList.get(i));
        }
        filterMonths.endBatchedUpdates();
        Log.d(TAG,"All months - " + filterMonths);

        //CREATE CATEGORIES LIST
        List<String> categoriesList = new ArrayList<String>(filterCategoryTransactionHashMap.keySet()); // <== Set to List
        if(filterCategories==null){
            filterCategories = new SortedList<String>(String.class, new SortedList.Callback<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);    // sort A to Z
                }

                @Override
                public void onChanged(int position, int count) {

                }

                @Override
                public boolean areContentsTheSame(String oldItem, String newItem) {
                    return (oldItem.toLowerCase()).equals(newItem.toLowerCase());
                }

                @Override
                public boolean areItemsTheSame(String oldItem, String newItem) {
                    return (oldItem.toLowerCase()).equals(newItem.toLowerCase());
                }

                @Override
                public void onInserted(int position, int count) {

                }

                @Override
                public void onRemoved(int position, int count) {

                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {

                }
            });
        }
        filterCategories.clear();
        filterCategories.beginBatchedUpdates();
        for (int i = 0; i < categoriesList.size(); i++) {
            filterCategories.add(categoriesList.get(i));
        }
        filterCategories.endBatchedUpdates();
        Log.d(TAG,"All categories - " + filterCategories);
    }

    private void initDatedTransactionHashMap() {
        if(mTransactions!=null){
            for(Transaction transaction:mTransactions){
                addTransactionToDatedHashMap(transaction);
            }
        } else {
            Log.d(TAG, "mTransactions is null or empty!");
        }
    }

    // ActionMode.Callback for contextual action mode
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isMultiSelect = true;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_mode_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

            //create AlertDialog to check if user actually wants to delete Items
            final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Delete Items");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Positive button clicked on Delete alert dialog!!");
                    //delete transactions from end to start so as to avoid accidental damage to list
                    Iterator<Integer> iterator = selectedItems.descendingIterator();
                    while (iterator.hasNext()) {
                        int pos = iterator.next();
                        deleteGroupOfTransactions(pos);
                    }

                    Iterator<Transaction> transactionIterator = selectedTransactions.descendingIterator();
                    while (transactionIterator.hasNext()) {
                        Transaction transaction = transactionIterator.next();
                        deleteTransaction(transaction);
                    }
                    transactionsRecyclerViewAdapter.setTypeAndInitDatedHM(type);
                    transactionsRecyclerViewAdapter.notifyDataSetChanged();
                    mode.finish();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Negative button clicked on Delete alert dialog!!");
                    dialogInterface.cancel();
                }
            });

            //show dialog
            alert.show();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            transactionsRecyclerViewAdapter.setSelectedItems(new TreeSet<Integer>());
            transactionsRecyclerViewAdapter.setSelectedTransactions(new TreeSet<Transaction>());
            isMultiSelect = false;
            selectedItems.clear();
            selectedTransactions.clear();
            actionMode = null;
        }
    };

    public boolean addTransaction(Transaction transaction){
        if(((MainActivity) getActivity()).getPayTrackDBHelper().insertDataToTransactionsTable(transaction)){
            Log.d(TAG,"Transaction inserted to db - " + transaction.toString());
            mTransactions.add(transaction);
            addTransactionToFilterHashMaps(transaction);
            refreshSpinner();
            addTransactionToDatedHashMap(transaction);
            transactionsRecyclerViewAdapter.setTypeAndInitDatedHM(type);
            transactionsRecyclerViewAdapter.notifyDataSetChanged();
            Log.i(TAG, "Added transaction - " + transaction.toString());
            return true;
        } else {
            Log.e(TAG,"Couldn't insert transaction to db - " + transaction.toString());
            return false;
        }
    }

    //func to delete a single transaction
    //make sure to call notifyDataSetChanged() after execution of this method
    private void deleteTransaction(Transaction transaction) {
        if (((MainActivity) getActivity()).getPayTrackDBHelper().deleteDataInTransactionsTable(transaction)) {
            Log.d(TAG, "Transaction deleted from db : " + transaction.toString());
        } else {
            Log.e(TAG, "Couldn't delete transaction from db : " + transaction.toString());
        }

        mTransactions.remove(transaction);
        removeTransactionFromFilterHashMaps(transaction);
        refreshSpinner();
        removeTransactionFromHashMap(transaction);
        transactionsRecyclerViewAdapter.setTypeAndInitDatedHM(type);
        transactionsRecyclerViewAdapter.notifyDataSetChanged();
    }

    //func to delete multiple transactions
    //make sure to call notifyDataSetChanged() after execution of this method
    private void deleteGroupOfTransactions(int position) {
        //remove transaction
        Date date = transactionsRecyclerViewAdapter.getDates().get(position);
        datedTransactionHashMap.remove(date);
        //delete from mTransactions & db
        for (int  i = mTransactions.size()-1; i>=0; i--){
            Transaction transaction = mTransactions.get(i);
            if(transaction.getDate().equals(date)) {
                deleteTransaction(transaction);
            }
        }
    }

    //function to multi-select Transaction once contextual action mode is launched
    private void selectMultiple(int position) {
        Log.d(TAG, "selectMultiple() called at position - " + String.valueOf(position));
        if (actionMode != null) {
            if (selectedItems.contains(position))
                selectedItems.remove(position);
            else {
                selectedItems.add(position);
                Date date = transactionsRecyclerViewAdapter.getDates().get(position);
                ArrayList<Transaction> transactions = datedTransactionHashMap.get(date);
                if (transactions != null) {
                    for (Transaction transaction : transactions) {
                        if (selectedTransactions.contains(transaction)) {
                            selectedTransactions.remove(transaction);
                        }
                    }
                }
            }
            if (selectedTransactions.size() + selectedItems.size() > 0) {
                actionMode.setTitle(String.valueOf(selectedTransactions.size() + selectedItems.size())); //show selected item count on action mode.
            } else {
                actionMode.setTitle(""); //remove item count from action mode.
                actionMode.finish(); //hide action mode.
            }
            transactionsRecyclerViewAdapter.setSelectedItems(selectedItems);
        }
    }

    private void selectMultiple(Transaction transaction) {
        Log.d(TAG, "selectMultiple() called at transaction - " + transaction.toString());
        if (actionMode != null) {
            int parentPos = (transactionsRecyclerViewAdapter.getDates().indexOf(transaction.getDate()));
            if (!selectedItems.contains(parentPos)) {

                if (selectedTransactions.contains(transaction))
                    selectedTransactions.remove(transaction);
                else
                    selectedTransactions.add(transaction);

                if (selectedTransactions.size() + selectedItems.size() > 0) {
                    actionMode.setTitle(String.valueOf(selectedTransactions.size() + selectedItems.size())); //show selected item count on action mode.
                } else {
                    actionMode.setTitle(""); //remove item count from action mode.
                    actionMode.finish(); //hide action mode.
                }
                transactionsRecyclerViewAdapter.setSelectedTransactions(selectedTransactions);

            }
        }
    }

}
