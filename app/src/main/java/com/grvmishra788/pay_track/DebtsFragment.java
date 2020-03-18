package com.grvmishra788.pay_track;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.Debt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.grvmishra788.pay_track.GlobalConstants.ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.POSITION_ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_DEBT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_EDIT_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_EDIT_DEBT;

public class DebtsFragment extends Fragment {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + DebtsFragment.class.getName();

    private FloatingActionButton addDebtButton;

    //Debts list
    private ArrayList<Debt> mDebts;

    //recyclerView variables
    private RecyclerView debtsRecyclerView;
    private DebtsAdapter debtsRecyclerViewAdapter;
    private RecyclerView.LayoutManager debtsRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;

    // fields to help keep track of app’s state for Contextual Action Mode
    private boolean isMultiSelect = false;
    private TreeSet<Integer> selectedItems = new TreeSet<>();
    private ActionMode actionMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_debts_fragment, container, false);

        //init db
        payTrackDBHelper = new DbHelper(getContext());

        //init debts list
        mDebts = payTrackDBHelper.getAllDebts();

        if(mDebts == null){
            Log.d(TAG, "mDebts is null");
            mDebts = new ArrayList<>();
        }



        //init RecyclerView
        debtsRecyclerView = (RecyclerView) view.findViewById(R.id.show_debt_recycler_view);
        debtsRecyclerView.setHasFixedSize(true);
        debtsRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        debtsRecyclerViewAdapter = new DebtsAdapter(getContext(), mDebts);
        debtsRecyclerView.setLayoutManager(debtsRecyclerViewLayoutManager);
        debtsRecyclerView.setAdapter(debtsRecyclerViewAdapter);

        debtsRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "onItemClick called at position - " + position);
                if (isMultiSelect) {
                    //if multiple selection is enabled then select item on single click
                    selectMultiple(position);
                } else {
//                    Intent editActivityIntent = new Intent(getActivity(), AddDebtActivity.class);
//                    editActivityIntent.putExtra(ITEM_TO_EDIT, mDebts.get(position));
//                    editActivityIntent.putExtra(POSITION_ITEM_TO_EDIT, position);
//                    startActivityForResult(editActivityIntent, REQ_CODE_EDIT_DEBT);
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onItemLongClick(int position) {
                Log.d(TAG, "onItemLongClick called at position - " + position);
                if (!isMultiSelect) {
                    //init select items and isMultiSelect on long click
                    selectedItems = new TreeSet<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        //show ActionMode on long click
                        actionMode = ((AppCompatActivity)getContext()).startSupportActionMode(actionModeCallbacks);
                    }
                    selectMultiple(position);
                }
            }
        });

        addDebtButton = view.findViewById(R.id.addItemFAB);
        addDebtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addDebtButton::onClick()");
                Intent addDebtIntent = new Intent(getActivity(), AddDebtActivity.class);
                startActivityForResult(addDebtIntent, REQ_CODE_ADD_DEBT);
            }
        });
        
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_ADD_DEBT){
                Log.i(TAG, "Processing add debt...");
                Debt debt =  (Debt) data.getSerializableExtra(GlobalConstants.DEBT_OBJECT);
                if(debt!=null){
                    if(mDebts==null){
                        mDebts = new ArrayList<>();
                    }
                    mDebts.add(debt);

                    if(payTrackDBHelper.insertDataToDebtsTable(debt)){
                        Log.d(TAG,"Debt inserted to db - " + debt.toString());
                    } else {
                        Log.e(TAG,"Couldn't insert debt to db - " + debt.toString());
                    }
                    debtsRecyclerViewAdapter.notifyDataSetChanged();
                }
                Log.i(TAG, "Added debt - " + debt.toString());
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }
    }

    //function to multi-select once contextual action mode is launched
    private void selectMultiple(int position) {
        Log.d(TAG, "selectMultiple() called at position - "+String.valueOf(position));
        if (actionMode != null) {
            if (selectedItems.contains(position))
                selectedItems.remove(position);
            else
                selectedItems.add(position);

            if (selectedItems.size() > 0) {
                actionMode.setTitle(String.valueOf(selectedItems.size())); //show selected item count on action mode.
            } else{
                actionMode.setTitle(""); //remove item count from action mode.
                actionMode.finish(); //hide action mode.
            }
            debtsRecyclerViewAdapter.setSelectedItems(selectedItems);
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
                    //delete items from end to start so as to avoid accidental damage to items
                    Iterator<Integer> iterator = selectedItems.descendingIterator();
                    while (iterator.hasNext()) {
                        int pos = iterator.next();
                        deleteDebt(pos);
                    }
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
            debtsRecyclerViewAdapter.setSelectedItems(new TreeSet<Integer>());
            isMultiSelect = false;
            selectedItems.clear();
            actionMode = null;
        }
    };

    private void deleteDebt(int position) {
        Debt debt = mDebts.get(position);
        if (payTrackDBHelper.deleteDataFromDebtsTable(debt)) {
            Log.d(TAG, "Account deleted from db : " + debt.toString());
        } else {
            Log.e(TAG, "Couldn't delete Account from db : " + debt.toString());
        }
        mDebts.remove(position);
        debtsRecyclerViewAdapter.notifyDataSetChanged();
    }
}
