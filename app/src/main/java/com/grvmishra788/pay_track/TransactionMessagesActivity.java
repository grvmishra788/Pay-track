package com.grvmishra788.pay_track;

import android.os.Bundle;
import android.util.Log;

import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.Transaction;
import com.grvmishra788.pay_track.DS.TransactionMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionMessagesActivity extends AppCompatActivity {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + TransactionMessagesActivity.class.getName();

    //TransactionMessages list
    private ArrayList<TransactionMessage> mTransactionMessages;

    private HashMap<Date, ArrayList<TransactionMessage>> datedTransactionMessageHashMap;

    //recyclerView variables
    private RecyclerView transactionMessagesRecyclerView;
    private TransactionMessagesAdapter transactionMessagesRecyclerViewAdapter;
    private RecyclerView.LayoutManager transactionMessagesRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_messages);
        setTitle(R.string.title_transaction_message);

        //init db
        payTrackDBHelper = new DbHelper(this);

        //init transasctions list
        mTransactionMessages = payTrackDBHelper.getAllTransactionMessages();

        //init datedTransactionMessageHashMap
        datedTransactionMessageHashMap = new HashMap<>();
        initDatedTransactionMessageHashMap();

        if(mTransactionMessages == null){
            Log.d(TAG, "mTransactionMessages is null");
            mTransactionMessages = new ArrayList<>();
        }


        //init RecyclerView
        transactionMessagesRecyclerView = (RecyclerView) findViewById(R.id.show_transaction_messages_recycler_view);
        transactionMessagesRecyclerView.setHasFixedSize(true);
        transactionMessagesRecyclerViewLayoutManager = new LinearLayoutManager(this);
        transactionMessagesRecyclerViewAdapter = new TransactionMessagesAdapter(this, datedTransactionMessageHashMap);
        transactionMessagesRecyclerView.setLayoutManager(transactionMessagesRecyclerViewLayoutManager);
        transactionMessagesRecyclerView.setAdapter(transactionMessagesRecyclerViewAdapter);
        
    }

    private void initDatedTransactionMessageHashMap() {
        if(mTransactionMessages!=null){
            for(TransactionMessage transaction:mTransactionMessages){
                addTransactionMessageToHashMap(transaction);
            }
        } else {
            Log.d(TAG, "mTransactionMessages is null or empty!");
        }
    }

    private void addTransactionMessageToHashMap(TransactionMessage transactionMessage) {
        Date dateOfTransactionMessage = transactionMessage.getDate();
        if(datedTransactionMessageHashMap==null){
            datedTransactionMessageHashMap = new HashMap<>();
        }
        if(dateOfTransactionMessage!=null){
            ArrayList<TransactionMessage> curDateTransactionMessages = null;
            if(datedTransactionMessageHashMap.containsKey(dateOfTransactionMessage)){
                curDateTransactionMessages = datedTransactionMessageHashMap.get(dateOfTransactionMessage);
            }
            if (curDateTransactionMessages == null) {
                curDateTransactionMessages = new ArrayList<>();
            }
            curDateTransactionMessages.add(transactionMessage);
            datedTransactionMessageHashMap.put(dateOfTransactionMessage, curDateTransactionMessages);

        } else {
            Log.e(TAG,"Date of " + transactionMessage.toString() + " is null!");
        }
    }
    
}
