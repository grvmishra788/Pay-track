package com.grvmishra788.pay_track;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_TRANSACTION;

public class TransactionsFragment extends Fragment {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + TransactionsFragment.class.getName();

    private FloatingActionButton addTransactionButton;

    //Transactions list
    private ArrayList<Transaction> mTransactions;

    private HashMap<String, ArrayList<Transaction>> datedTransactionHashMap;

    //recyclerView variables
    private RecyclerView transactionsRecyclerView;
    private TransactionsAdapter transactionsRecyclerViewAdapter;
    private RecyclerView.LayoutManager transactionsRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() starts...");
        View view = inflater.inflate(R.layout.layout_transactions_fragment, container, false);

        //init db
        payTrackDBHelper = new DbHelper(getContext());

        //init transasctions list
        mTransactions = payTrackDBHelper.getAllTransactions();

        //init datedTransactionHashMap
        datedTransactionHashMap = new HashMap<>();
        initDatedTransactionHashMap();

        if(mTransactions == null){
            Log.d(TAG, "mTransactions is null");
            mTransactions = new ArrayList<>();
        }



        //init RecyclerView
        transactionsRecyclerView = (RecyclerView) view.findViewById(R.id.show_transaction_recycler_view);
        transactionsRecyclerView.setHasFixedSize(true);
        transactionsRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        transactionsRecyclerViewAdapter = new TransactionsAdapter(getContext(), datedTransactionHashMap);
        transactionsRecyclerView.setLayoutManager(transactionsRecyclerViewLayoutManager);
        transactionsRecyclerView.setAdapter(transactionsRecyclerViewAdapter);


        addTransactionButton = view.findViewById(R.id.addItemFAB);
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addTransactionButton::onClick()");
                Intent addTransactionIntent = new Intent(getActivity(), AddTransactionActivity.class);
                startActivityForResult(addTransactionIntent, REQ_CODE_ADD_TRANSACTION);
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
            if(requestCode==REQ_CODE_ADD_TRANSACTION){
                Log.i(TAG, "Processing add transaction...");
                Transaction transaction =  (Transaction) data.getSerializableExtra(GlobalConstants.TRANSACTION_OBJECT);
                if(transaction!=null){
                    if(mTransactions==null){
                        mTransactions = new ArrayList<>();
                    }
                    mTransactions.add(transaction);
                    addTransactionToHashMap(transaction);
                    if(payTrackDBHelper.insertDataToTransactionsTable(transaction)){
                        Log.d(TAG,"Transaction inserted to db - " + transaction.toString());
                    } else {
                        Log.e(TAG,"Couldn't insert transaction to db - " + transaction.toString());
                    }
                    transactionsRecyclerViewAdapter.notifyDataSetChanged();
                }
                Log.i(TAG, "Added transaction - " + transaction.toString());
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }
    }

    private void addTransactionToHashMap(Transaction transaction) {
        Date dateOfTransaction = transaction.getDate();
        if(datedTransactionHashMap==null){
            datedTransactionHashMap = new HashMap<>();
        }
        if(dateOfTransaction!=null){
            ArrayList<Transaction> curDateTransactions = null;
            SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_DAY_AND_DATE);
            String dateString = sdf.format(dateOfTransaction);
            if(datedTransactionHashMap.containsKey(dateString)){
                curDateTransactions = datedTransactionHashMap.get(dateString);
            }
            if (curDateTransactions == null) {
                curDateTransactions = new ArrayList<>();
            }
            curDateTransactions.add(transaction);
            datedTransactionHashMap.put(dateString, curDateTransactions);

        } else {
            Log.e(TAG,"Date of " + transaction.toString() + " is null!");
        }
    }

    private void initDatedTransactionHashMap() {
        if(mTransactions!=null || mTransactions.size()==0){
            for(Transaction transaction:mTransactions){
                addTransactionToHashMap(transaction);
            }
        } else {
            Log.d(TAG, "mTransactions is null or empty!");
        }
    }



}
