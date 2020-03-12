package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {

    //constants
    private static final String TAG = "Pay-Track: " + TransactionsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    private HashMap<String, ArrayList<Transaction>> datedTransactionHashMap;
    private List<String> dates;

    //Constructor: binds Transaction object data to TransactionsAdapter
    public TransactionsAdapter(Context mContext, HashMap<String, ArrayList<Transaction>> datedTransactionHashMap) {
        Log.i(TAG, TAG + ": Constructor starts");
        this.mContext = mContext;
        this.datedTransactionHashMap = datedTransactionHashMap;
        Log.i(TAG, TAG + ": Constructor ends");
    }


    @NonNull
    @Override
    public TransactionsAdapter.TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_transaction, parent, false);
        TransactionsViewHolder transactionsViewHolder = new TransactionsViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return transactionsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.TransactionsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() :: " + position );
        if(position==0){
            dates = new ArrayList<String>(datedTransactionHashMap.keySet()); // <== Set to List
            Log.d(TAG,"All dates - " + dates);
        }

        String dateString = dates.get(position);

        //convert date to string & display in text view
        if(dateString!=null){
            ArrayList<Transaction> curDateTransactions = datedTransactionHashMap.get(dateString);
            Log.d(TAG,"Date for position - " + position + " is - " + dateString);
            holder.tv_date.setText(dateString);
            //update grouped Transactions
            holder.setmGroupedTransactions(curDateTransactions);
        } else {
            Log.e(TAG,"Date for position - " + position + " is null !");
        }
    }

    @Override
    public int getItemCount() {
        if(datedTransactionHashMap!=null)
            return datedTransactionHashMap.size();
        else
            return 0;
    }

    public class TransactionsViewHolder extends RecyclerView.ViewHolder {

        //constants
        private final String S_TAG = "Pay-Track: " + TransactionsViewHolder.class.getName(); //constant Class TAG

        private TextView tv_date;
        
        private RecyclerView groupedTransactionsRecyclerView;
        private GroupedTransactionsAdapter groupedTransactionRecyclerViewAdapter;
        private RecyclerView.LayoutManager groupedTransactionsRecyclerViewLayoutManager;

        private ArrayList<Transaction> mGroupedTransactions;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i(S_TAG, S_TAG + ": Constructor starts");
            tv_date = itemView.findViewById(R.id.tv_show_date);
            groupedTransactionsRecyclerView = itemView.findViewById(R.id.recyclerview_item_transaction);
            initItemTransactionRecyclerView();
            Log.i(S_TAG, S_TAG + ": Constructor ends");
        }

        private void initItemTransactionRecyclerView() {
            Log.i(TAG,"initItemTransactionRecyclerView()");
            if(mGroupedTransactions ==null){
                mGroupedTransactions = new ArrayList<>();
            }

            groupedTransactionsRecyclerView.setHasFixedSize(true);
            groupedTransactionsRecyclerViewLayoutManager = new LinearLayoutManager(mContext);
            groupedTransactionRecyclerViewAdapter = new GroupedTransactionsAdapter(mContext, mGroupedTransactions);
            groupedTransactionsRecyclerView.setLayoutManager(groupedTransactionsRecyclerViewLayoutManager);
            groupedTransactionsRecyclerView.setAdapter(groupedTransactionRecyclerViewAdapter);
        }

        public ArrayList<Transaction> getmGroupedTransactions() {
            return mGroupedTransactions;
        }

        public void setmGroupedTransactions(ArrayList<Transaction> mGroupedTransactions) {
            Log.i(TAG,"setmGroupedTransactions()");
            this.mGroupedTransactions = mGroupedTransactions;
            groupedTransactionRecyclerViewAdapter.setmDatedTransactions(mGroupedTransactions);
            groupedTransactionRecyclerViewAdapter.notifyDataSetChanged();
        }

    }
}
