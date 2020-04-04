package com.grvmishra788.pay_track;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.Transaction;
import com.grvmishra788.pay_track.DS.TransactionMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_TRANSACTION_VIA_MESSAGE;
import static com.grvmishra788.pay_track.GlobalConstants.TRANSACTION_MESSAGE_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.TRANSACTION_OBJECT;

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

    // fields to help keep track of app’s state for Contextual Action Mode
    private boolean isMultiSelect = false;
    private TreeSet<Integer> selectedItems = new TreeSet<>();
    private ActionMode actionMode;

    //Variable to store transactionMessages when launching Contextual action mode
    private TreeSet<TransactionMessage> selectedTransactionMessages = new TreeSet<>();

    private ArrayList<Transaction> newTransactions;
    private ArrayList<TransactionMessage> deletedMessages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_messages);
        setTitle(R.string.title_transaction_message);

        //init newTransactions & deletedMessages
        newTransactions = new ArrayList<>();
        deletedMessages = new ArrayList<>();

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

        transactionMessagesRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {

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
                    selectedTransactionMessages = new TreeSet<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        //show ActionMode on long click
                        actionMode = startSupportActionMode(actionModeCallbacks);
                    }
                    selectMultiple(position);
                }
            }
        });

        transactionMessagesRecyclerViewAdapter.setOnTransactionMessageClickListener(new OnTransactionMessageClickListener() {
            @Override
            public void onItemClick(int position, TransactionMessage transactionMessage) {
                if (!isMultiSelect) {
                    Intent editActivityIntent = new Intent(TransactionMessagesActivity.this, AddTransactionActivity.class);
                    editActivityIntent.putExtra(TRANSACTION_MESSAGE_OBJECT, transactionMessage);
                    startActivityForResult(editActivityIntent, REQ_CODE_ADD_TRANSACTION_VIA_MESSAGE);
                } else {
                    selectMultiple(transactionMessage);
                }
            }

            @Override
            public void onItemLongClick(int position, TransactionMessage transactionMessage) {
                Log.d(TAG, "onItemLongClick called at position - " + position);
                if (!isMultiSelect) {
                    //init select items and isMultiSelect on long click
                    selectedItems = new TreeSet<>();
                    selectedTransactionMessages = new TreeSet<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        //show ActionMode on long click
                        actionMode = startSupportActionMode(actionModeCallbacks);
                    }
                }
                selectMultiple(transactionMessage);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResultAndFinishActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        setResultAndFinishActivity();
    }

    private void setResultAndFinishActivity(){
        if(deletedMessages!=null){
            for(TransactionMessage transactionMessage: deletedMessages){
                if (payTrackDBHelper.deleteDataInTransactionMessagesTable(transactionMessage)) {
                    Log.d(TAG, "TransactionMessage deleted from db : " + transactionMessage.toString());
                } else {
                    Log.e(TAG, "Couldn't delete transactionMessage from db : " + transactionMessage.toString());
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra(TRANSACTION_OBJECT, newTransactions);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_ADD_TRANSACTION_VIA_MESSAGE){
                Log.i(TAG, "Processing add transaction via message...");
                Transaction transaction =  (Transaction) data.getSerializableExtra(GlobalConstants.TRANSACTION_OBJECT);
                newTransactions.add(transaction);
                TransactionMessage message = (TransactionMessage) data.getSerializableExtra(TRANSACTION_MESSAGE_OBJECT);
                if(message!=null){
                    if(deleteTransactionMessageByObject(message)){
                        deletedMessages.add(message);
                        removeTransactionMessageFromHashMap(message);
                        transactionMessagesRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Couldn't delete", Toast.LENGTH_LONG).show();
                    }
                }

                Log.i(TAG, "Received transaction - " + transaction.toString());
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }
    }

    private void initDatedTransactionMessageHashMap() {
        if(mTransactionMessages!=null){
            for(TransactionMessage transactionMessage:mTransactionMessages){
                addTransactionMessageToHashMap(transactionMessage);
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
    
    private void removeTransactionMessageFromHashMap(TransactionMessage transactionMessage) {
        Date dateOfTransactionMessage = transactionMessage.getDate();
        if(dateOfTransactionMessage!=null){
            ArrayList<TransactionMessage> curDateTransactionMessages = null;
            if(datedTransactionMessageHashMap.containsKey(dateOfTransactionMessage)){
                curDateTransactionMessages = datedTransactionMessageHashMap.get(dateOfTransactionMessage);
                for(int i=0; i<curDateTransactionMessages.size();i++){
                    if(curDateTransactionMessages.get(i).getId().equals(transactionMessage.getId())){
                        curDateTransactionMessages.remove(i);
                        break;
                    }
                }
                if(curDateTransactionMessages==null || curDateTransactionMessages.size()==0){
                    datedTransactionMessageHashMap.remove(dateOfTransactionMessage);
                }
            } else {
                Log.e(TAG,"Date of " + transactionMessage.toString() + " is not present in hash map!");
            }
        } else {
            Log.e(TAG,"Date of " + transactionMessage.toString() + " is null!");
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
            final AlertDialog.Builder alert = new AlertDialog.Builder(TransactionMessagesActivity.this);
            alert.setTitle("Delete Items");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Positive button clicked on Delete alert dialog!!");
                    //delete todos from end to start so as to avoid accidental damage to todolist
                    Iterator<Integer> iterator = selectedItems.descendingIterator();
                    while (iterator.hasNext()) {
                        int pos = iterator.next();
                        deleteGroupOfTransactionMessages(pos);
                    }

                    Iterator<TransactionMessage> transactionMessageIterator = selectedTransactionMessages.descendingIterator();
                    while (transactionMessageIterator.hasNext()) {
                        TransactionMessage transactionMessage = transactionMessageIterator.next();
                        deleteTransactionMessage(transactionMessage);
                    }

                    transactionMessagesRecyclerViewAdapter.notifyDataSetChanged();
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
            transactionMessagesRecyclerViewAdapter.setSelectedItems(new TreeSet<Integer>());
            transactionMessagesRecyclerViewAdapter.setSelectedTransactionMessages(new TreeSet<TransactionMessage>());
            isMultiSelect = false;
            selectedItems.clear();
            selectedTransactionMessages.clear();
            actionMode = null;
        }
    };

    //func to delete a single transactionMessage
    //make sure to call notifyDataSetChanged() after execution of this method
    private boolean deleteTransactionMessage(TransactionMessage transactionMessage) {
        if (payTrackDBHelper.deleteDataInTransactionMessagesTable(transactionMessage)) {
            Log.d(TAG, "TransactionMessage deleted from db : " + transactionMessage.toString());
            if(deleteTransactionMessageByObject(transactionMessage)){
                removeTransactionMessageFromHashMap(transactionMessage);
                transactionMessagesRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                Log.i(TAG, "Message already deleted from transaction messages");
            }
            return true;
        } else {
            Log.e(TAG, "Couldn't delete transactionMessage from db : " + transactionMessage.toString());
            return false;
        }
    }

    public boolean deleteTransactionMessageByObject(TransactionMessage message){
        for(int i=0; i<mTransactionMessages.size();i++){
            TransactionMessage transactionMessage = mTransactionMessages.get(i);
            if(transactionMessage.getId().equals(message.getId())){
                    mTransactionMessages.remove(i);
                    Log.d(TAG, "TransactionMessage deleted from Transaction message list : " + transactionMessage.toString());
                    return true;
            }
        }
        return false;
    }

    //func to delete multiple transactionMessages
    //make sure to call notifyDataSetChanged() after execution of this method
    private void deleteGroupOfTransactionMessages(int position) {
        //remove transactionMessage
        Date date = transactionMessagesRecyclerViewAdapter.getDates().get(position);
        ArrayList<TransactionMessage> transactionMessages = datedTransactionMessageHashMap.get(date);

        datedTransactionMessageHashMap.remove(date);
        //delete from mTransactionMessages & db
        for (int  i = mTransactionMessages.size()-1; i>=0; i--){
            TransactionMessage transactionMessage = mTransactionMessages.get(i);
            if(transactionMessage.getDate().equals(date)) {
                deleteTransactionMessage(transactionMessage);
            }
        }
    }

    //function to multi-select TransactionMessage once contextual action mode is launched
    private void selectMultiple(int position) {
        Log.d(TAG, "selectMultiple() called at position - " + String.valueOf(position));
        if (actionMode != null) {
            if (selectedItems.contains(position))
                selectedItems.remove(position);
            else {
                selectedItems.add(position);
                Date date = transactionMessagesRecyclerViewAdapter.getDates().get(position);
                ArrayList<TransactionMessage> transactionMessages = datedTransactionMessageHashMap.get(date);
                if (transactionMessages != null) {
                    for (TransactionMessage transactionMessage : transactionMessages) {
                        if (selectedTransactionMessages.contains(transactionMessage)) {
                            selectedTransactionMessages.remove(transactionMessage);
                        }
                    }
                }
            }
            if (selectedTransactionMessages.size() + selectedItems.size() > 0) {
                actionMode.setTitle(String.valueOf(selectedTransactionMessages.size() + selectedItems.size())); //show selected item count on action mode.
            } else {
                actionMode.setTitle(""); //remove item count from action mode.
                actionMode.finish(); //hide action mode.
            }
            transactionMessagesRecyclerViewAdapter.setSelectedItems(selectedItems);
        }
    }

    private void selectMultiple(TransactionMessage transactionMessage) {
        Log.d(TAG, "selectMultiple() called at transactionMessage - " + transactionMessage.toString());
        if (actionMode != null) {
            int parentPos = (transactionMessagesRecyclerViewAdapter.getDates().indexOf(transactionMessage.getDate()));
            if (!selectedItems.contains(parentPos)) {

                if (selectedTransactionMessages.contains(transactionMessage))
                    selectedTransactionMessages.remove(transactionMessage);
                else
                    selectedTransactionMessages.add(transactionMessage);

                if (selectedTransactionMessages.size() + selectedItems.size() > 0) {
                    actionMode.setTitle(String.valueOf(selectedTransactionMessages.size() + selectedItems.size())); //show selected item count on action mode.
                } else {
                    actionMode.setTitle(""); //remove item count from action mode.
                    actionMode.finish(); //hide action mode.
                }
                transactionMessagesRecyclerViewAdapter.setSelectedTransactionMessages(selectedTransactionMessages);

            }
        }
    }
    
}
