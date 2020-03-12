package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Transaction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

class GroupedTransactionsAdapter extends RecyclerView.Adapter<GroupedTransactionsAdapter.GroupedTransactionsHolder> {
    //constants
    private static final String TAG = "Pay-Track: " + GroupedTransactionsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    ArrayList<Transaction> mDatedTransactions;

    public GroupedTransactionsAdapter(Context mContext, ArrayList<Transaction> transactions) {
        this.mContext = mContext;
        this.mDatedTransactions = transactions;
    }

    @NonNull
    @Override
    public GroupedTransactionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sub_item_transaction, parent, false);
        GroupedTransactionsHolder transactionsViewHolder = new GroupedTransactionsHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return transactionsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupedTransactionsHolder holder, int position) {
        String amountValue = "", desc = "";

        Transaction transaction = mDatedTransactions.get(position);
        Log.d(TAG, "onBindViewHolder() :: " + position + " :: " + transaction.toString());
        if(transaction.getType()== GlobalConstants.TransactionType.CREDIT){
            amountValue = " + " + String.valueOf(transaction.getAmount());
            holder.amount.setTextColor(GREEN);
        } else {
            amountValue = " - " + String.valueOf(transaction.getAmount());
            holder.amount.setTextColor(RED);
        }
        holder.amount.setText(amountValue);

        holder.category.setText(transaction.getCategory());

        desc = transaction.getDescription();
        if(InputValidationUtilities.isValidString(desc)){
            holder.ll_description.setVisibility(View.VISIBLE);
            holder.description.setText(desc);
        } else {
            holder.ll_description.setVisibility(View.GONE);
        }

        holder.account.setText(transaction.getAccount());

    }

    @Override
    public int getItemCount() {
        if(mDatedTransactions==null)
            return 0;
        else
            return mDatedTransactions.size();
    }

    public class GroupedTransactionsHolder extends RecyclerView.ViewHolder{
        //constants
        private final String S_TAG = "Pay-Track: " + GroupedTransactionsHolder.class.getName(); //constant Class TAG

        private TextView amount, category, description, account;
        private LinearLayout ll_description;

        public GroupedTransactionsHolder(@NonNull View itemView) {
            super(itemView);
            Log.i(S_TAG, S_TAG + ": Constructor starts");
            amount = itemView.findViewById(R.id.tv_show_amount);
            category = itemView.findViewById(R.id.tv_show_transactionCategory);
            description = itemView.findViewById(R.id.tv_show_description);
            account = itemView.findViewById(R.id.tv_show_account);
            ll_description = itemView.findViewById(R.id.ll_show_description);
            Log.i(S_TAG, S_TAG + ": Constructor ends");
        }
    }

    public ArrayList<Transaction> getmDatedTransactions() {
        return mDatedTransactions;
    }

    public void setmDatedTransactions(ArrayList<Transaction> mDatedTransactions) {
        this.mDatedTransactions = mDatedTransactions;
    }
}
