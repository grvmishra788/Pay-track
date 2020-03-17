package com.grvmishra788.pay_track;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Transaction;

import java.util.ArrayList;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

class GroupedTransactionsAdapter extends RecyclerView.Adapter<GroupedTransactionsAdapter.GroupedTransactionsHolder> {
    //constants
    private static final String TAG = "Pay-Track: " + GroupedTransactionsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    private ArrayList<Transaction> mGroupedTransactions;

    //Variable for onItemclickListener
    private OnTransactionClickListener onTransactionClickListener;

    //Variable to store subcategories when launching Contextual action mode
    private TreeSet<Transaction> selectedTransactions = new TreeSet<>();

    public GroupedTransactionsAdapter(Context mContext, ArrayList<Transaction> transactions) {
        this.mContext = mContext;
        this.mGroupedTransactions = transactions;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull GroupedTransactionsHolder holder, int position) {
        String amountValue = "", desc = "";

        Transaction transaction = mGroupedTransactions.get(position);
        Log.d(TAG, "onBindViewHolder() :: " + position + " :: " + transaction.toString());
        if(transaction.getType()== GlobalConstants.TransactionType.CREDIT){
            amountValue = " + " + String.valueOf(transaction.getAmount());
            holder.amount.setTextColor(GREEN);
        } else {
            amountValue = " - " + String.valueOf(transaction.getAmount());
            holder.amount.setTextColor(RED);
        }
        holder.amount.setText(amountValue);

        String categoryVal = transaction.getCategory();
        String subCategory = transaction.getSubCategory();
        if(InputValidationUtilities.isValidString(subCategory)) {
            categoryVal+=("/"+subCategory);
        }
        holder.category.setText(categoryVal);

        desc = transaction.getDescription();
        if(InputValidationUtilities.isValidString(desc)){
            holder.ll_description.setVisibility(View.VISIBLE);
            holder.description.setText(desc);
        } else {
            holder.ll_description.setVisibility(View.GONE);
        }

        holder.account.setText(transaction.getAccount());

        if (selectedTransactions.contains(transaction)) {
            //if item is selected then,set foreground color of FrameLayout.
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccentTransparent)));
        } else {
            //else remove selected item color.
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        }


    }

    @Override
    public int getItemCount() {
        if(mGroupedTransactions ==null)
            return 0;
        else
            return mGroupedTransactions.size();
    }

    public ArrayList<Transaction> getmGroupedTransactions() {
        return mGroupedTransactions;
    }

    public void setmGroupedTransactions(ArrayList<Transaction> mGroupedTransactions) {
        this.mGroupedTransactions = mGroupedTransactions;
    }

    public void setOnTransactionClickListener(OnTransactionClickListener onTransactionClickListener) {
        this.onTransactionClickListener = onTransactionClickListener;
    }

    public void setSelectedTransactions(TreeSet<Transaction> selectedTransactions) {
        this.selectedTransactions = selectedTransactions;
        notifyDataSetChanged();
    }

    public class GroupedTransactionsHolder extends RecyclerView.ViewHolder{
        //constants
        private final String S_TAG = "Pay-Track: " + GroupedTransactionsHolder.class.getName(); //constant Class TAG

        private TextView amount, category, description, account;
        private LinearLayout ll_description;

        public GroupedTransactionsHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onTransactionClickListener != null && position != RecyclerView.NO_POSITION) {
                        onTransactionClickListener.onItemClick(position, mGroupedTransactions.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (onTransactionClickListener != null && position != RecyclerView.NO_POSITION) {
                        onTransactionClickListener.onItemLongClick(position, mGroupedTransactions.get(position));
                    }
                    return true;
                }
            });

            Log.i(S_TAG, S_TAG + ": Constructor starts");
            amount = itemView.findViewById(R.id.tv_show_amount);
            category = itemView.findViewById(R.id.tv_show_transactionCategory);
            description = itemView.findViewById(R.id.tv_show_description);
            account = itemView.findViewById(R.id.tv_show_account);
            ll_description = itemView.findViewById(R.id.ll_show_description);
            Log.i(S_TAG, S_TAG + ": Constructor ends");
        }
    }

}
