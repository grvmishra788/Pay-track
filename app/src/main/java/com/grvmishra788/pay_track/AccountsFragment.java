package com.grvmishra788.pay_track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.CashAccount;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_ACCOUNT;

public class AccountsFragment extends Fragment {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AccountsFragment.class.getName();

    private FloatingActionButton addAccountButton;

    //Accounts list
    private ArrayList<CashAccount> mAccounts;

    //recyclerView variables
    private RecyclerView accountsRecyclerView;
    private RecyclerView.Adapter accountsRecyclerViewAdapter;
    private RecyclerView.LayoutManager accountsRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() starts...");
        View view = inflater.inflate(R.layout.layout_accounts_fragment, container, false);

        //init db
        payTrackDBHelper = new DbHelper(getContext());

        //init accounts list
        mAccounts = payTrackDBHelper.getAllAccounts();
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
            if(requestCode==REQ_CODE_ADD_ACCOUNT){
                Log.i(TAG, "Processing...");
                CashAccount account =  (CashAccount) data.getSerializableExtra(GlobalConstants.ACCOUNT_OBJECT);
                if(account!=null){
                    if(mAccounts==null){
                        mAccounts = new ArrayList<>();
                        accountsRecyclerViewAdapter = new AccountsAdapter(getContext(), mAccounts);
                    }
                    mAccounts.add(account);
                    if(payTrackDBHelper.insertDataToAccountsTable(account)){
                        Log.d(TAG,"Account inserted to db - " + account.toString());
                    } else {
                        Log.e(TAG,"Couldn't insert account to db - " + account.toString());
                    }
                    accountsRecyclerViewAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Added account - " + account.toString());
                }
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }
    }
}
