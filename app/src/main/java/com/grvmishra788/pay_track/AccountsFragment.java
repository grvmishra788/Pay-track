package com.grvmishra788.pay_track;

import android.annotation.SuppressLint;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.DS.CashAccount;

import java.util.ArrayList;
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
import static com.grvmishra788.pay_track.GlobalConstants.BULLET_SYMBOL;
import static com.grvmishra788.pay_track.GlobalConstants.ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.POSITION_ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_EDIT_ACCOUNT;

public class AccountsFragment extends Fragment {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AccountsFragment.class.getName();

    private FloatingActionButton addAccountButton;

    //Accounts list
    private ArrayList<CashAccount> mAccounts;

    //recyclerView variables
    private RecyclerView accountsRecyclerView;
    private AccountsAdapter accountsRecyclerViewAdapter;
    private RecyclerView.LayoutManager accountsRecyclerViewLayoutManager;

    // fields to help keep track of app’s state for Contextual Action Mode
    private boolean isMultiSelect = false;
    private TreeSet<Integer> selectedItems = new TreeSet<>();
    private ActionMode actionMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() starts...");
        View view = inflater.inflate(R.layout.layout_accounts_fragment, container, false);

        //init accounts list
        mAccounts = ((MainActivity) getActivity()).getPayTrackDBHelper().getAllAccounts();
        if(mAccounts == null){
            mAccounts = new ArrayList<>();
        }

        //init RecyclerView
        accountsRecyclerView = (RecyclerView) view.findViewById(R.id.show_account_recycler_view);
        accountsRecyclerView.setHasFixedSize(true);    //hasFixedSize=true increases app performance as Recyclerview is not going to change in size
        accountsRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        accountsRecyclerViewAdapter = new AccountsAdapter(getContext(), mAccounts);
        //TODO : set observer to check if data is empty
//        accountsRecyclerViewAdapter.registerAdapterDataObserver(observer); //register data observer for recyclerView
        accountsRecyclerView.setLayoutManager(accountsRecyclerViewLayoutManager);
        accountsRecyclerView.setAdapter(accountsRecyclerViewAdapter);

        accountsRecyclerViewAdapter.setmOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "onItemClick called at position - " + position);
                if (isMultiSelect) {
                    //if multiple selection is enabled then select item on single click
                    selectMultiple(position);
                } else {
                    Intent editActivityIntent = new Intent(getActivity(), AddAccountActivity.class);
                    editActivityIntent.putExtra(ITEM_TO_EDIT, mAccounts.get(position));
                    editActivityIntent.putExtra(POSITION_ITEM_TO_EDIT, position);
                    startActivityForResult(editActivityIntent, REQ_CODE_EDIT_ACCOUNT);
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
        
        //init FAB
        addAccountButton = view.findViewById(R.id.addItemFAB);
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addAccountButton::onClick()");
                Intent addAccountIntent = new Intent(getActivity(), AddAccountActivity.class);
                startActivityForResult(addAccountIntent, REQ_CODE_ADD_ACCOUNT);
            }
        });
        Log.i(TAG, "onCreateView() ends!");
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_ADD_ACCOUNT){      //Add account activity result
                Log.i(TAG, "Processing add account...");
                CashAccount account =  (CashAccount) data.getSerializableExtra(GlobalConstants.ACCOUNT_OBJECT);
                if(account!=null){
                    if(mAccounts==null){
                        mAccounts = new ArrayList<>();
                        accountsRecyclerViewAdapter = new AccountsAdapter(getContext(), mAccounts);
                    }
                    if(addAccount(account)){
                        Log.i(TAG, "Added account - " + account.toString());
                    }
                }
            } else if (requestCode == REQ_CODE_EDIT_ACCOUNT) {       //edit account activity result

                Log.i(TAG, "Processing edit account...");
                int position = data.getIntExtra(POSITION_ITEM_TO_EDIT, -1);
                CashAccount oldAccount = mAccounts.get(position);
                CashAccount newAccount =  (CashAccount) data.getSerializableExtra(GlobalConstants.ACCOUNT_OBJECT);
                if(newAccount!=null){
                    if (((MainActivity) getActivity()).getPayTrackDBHelper().updateDataInAccountsTable(oldAccount, newAccount)) {
                        Log.d(TAG, "Account updated in db - FROM : " + oldAccount.toString() + " TO : " + newAccount.toString());
                    } else {
                        Log.e(TAG, "Couldn't update category to db - " + oldAccount.toString());
                    }
                    mAccounts.set(position, newAccount);
                    accountsRecyclerViewAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Edited account - " + oldAccount.toString() + " to "+ newAccount.toString());
                }

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
            accountsRecyclerViewAdapter.setSelectedItems(selectedItems);
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
                        deleteAccount(pos);
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
            accountsRecyclerViewAdapter.setSelectedItems(new TreeSet<Integer>());
            isMultiSelect = false;
            selectedItems.clear();
            actionMode = null;
        }
    };

    public boolean addAccount(CashAccount account) {
        if(((MainActivity) getActivity()).getPayTrackDBHelper().insertDataToAccountsTable(account)){
            Log.d(TAG,"Account inserted to db - " + account.toString());
            mAccounts.add(account);
            accountsRecyclerViewAdapter.notifyDataSetChanged();
            return true;
        }

        Log.e(TAG,"Couldn't insert account to db - " + account.toString());
        return false;
    }

    private void deleteAccount(int position) {
        //remove category
        CashAccount account = mAccounts.get(position);

        int numLinksToCategoriesTable = ((MainActivity) getActivity()).getPayTrackDBHelper().getNumberOfLinksToCategoriesTable(account);
        int numLinksToSubCategoriesTable = ((MainActivity) getActivity()).getPayTrackDBHelper().getNumberOfLinksToSubCategoriesTable(account);
        int numLinksToTransactionsTable = ((MainActivity) getActivity()).getPayTrackDBHelper().getNumberOfLinksToTransactionsTable(account);
        int numLinksToDebtsTable = ((MainActivity) getActivity()).getPayTrackDBHelper().getNumberOfLinksToDebtsTable(account);

        if(numLinksToCategoriesTable!=0 || numLinksToSubCategoriesTable!=0 || numLinksToTransactionsTable!=0 || numLinksToDebtsTable!=0){
            String msg = account.getNickName() + " account has -\n" +
                    ((numLinksToCategoriesTable!=0) ? BULLET_SYMBOL + " " + numLinksToCategoriesTable + " link(s) to categories \n":"") +
                    ((numLinksToSubCategoriesTable!=0) ? BULLET_SYMBOL + " " + numLinksToSubCategoriesTable + " link(s) to sub-categories \n":"") +
                    ((numLinksToTransactionsTable!=0) ? BULLET_SYMBOL + " " + numLinksToTransactionsTable + " link(s) to transactions \n":"") +
                    ((numLinksToDebtsTable!=0) ? BULLET_SYMBOL + " " + numLinksToDebtsTable + " link(s) to debts \n":"") +
                    "\nDo you still want to delete this account? ";
                    forceDeleteDialog(msg, position);
        } else {
            if (((MainActivity) getActivity()).getPayTrackDBHelper().deleteDataFromAccountsTable(account)) {
                Log.d(TAG, "Account deleted from db : " + account.toString());
            } else {
                Log.e(TAG, "Couldn't delete Account from db : " + account.toString());
            }
            mAccounts.remove(position);
            accountsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void forceDeleteDialog(String msg, final int position){
        //create AlertDialog to check if user actually wants to delete Items
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Force Delete Items?");
        alert.setMessage(msg);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "Positive button clicked on Force Delete alert dialog!!");
                CashAccount account = mAccounts.get(position);
                if (((MainActivity) getActivity()).getPayTrackDBHelper().deleteDataFromAccountsTable(account)) {
                    Log.d(TAG, "Account deleted from db : " + account.toString());
                } else {
                    Log.e(TAG, "Couldn't delete Account from db : " + account.toString());
                }
                mAccounts.remove(position);
                accountsRecyclerViewAdapter.notifyDataSetChanged();
                
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
    }

}
