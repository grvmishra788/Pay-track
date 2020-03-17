package com.grvmishra788.pay_track;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {

    //constants
    private static final String TAG = "Pay-Track: " + TransactionsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    private HashMap<Date, ArrayList<Transaction>> datedTransactionHashMap;
    private List<Date> dates;

    //Variable for onItemClickListener
    private OnItemClickListener onItemClickListener;
    private OnTransactionClickListener onTransactionClickListener;

    //Variable to select dates when launching Contextual action mode
    private TreeSet<Integer> selectedItems = new TreeSet<>();

    //Variable to store transactions when launching Contextual action mode
    private TreeSet<Transaction> selectedTransactions = new TreeSet<>();

    //Constructor: binds Transaction object data to TransactionsAdapter
    public TransactionsAdapter(Context mContext, HashMap<Date, ArrayList<Transaction>> datedTransactionHashMap) {
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

    public List<Date> getDates() {
        return dates;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.TransactionsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() :: " + position );
        if(position==0){
            dates = new ArrayList<Date>(datedTransactionHashMap.keySet()); // <== Set to List
            Log.d(TAG,"All dates - " + dates);
        }

        Date date = dates.get(position);

        //convert date to string & display in text view
        if(date!=null){
            Log.d(TAG,"Date for position - " + position + " is - " + date);
            ArrayList<Transaction> curDateTransactions = datedTransactionHashMap.get(date);
            if (curDateTransactions == null || curDateTransactions.size()==0) {
                holder.itemView.setVisibility(View.GONE);
            } else {

                holder.itemView.setVisibility(View.VISIBLE);
                //update grouped Transactions
                holder.setmGroupedTransactions(curDateTransactions);

                SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_DAY_AND_DATE);
                String dateString = sdf.format(date);
                holder.tv_date.setText(dateString);

                //set total ampunt for curDateTransaction
                String amountValue = "";
                Long groupTransactionAmount = getGroupedTransactionAmount(curDateTransactions);
                if(groupTransactionAmount>=new Long(0)){
                    amountValue = " + " + String.valueOf(groupTransactionAmount);
                    holder.tv_group_amt.setTextColor(GREEN);
                } else {
                    amountValue = " - " + String.valueOf(-groupTransactionAmount);
                    holder.tv_group_amt.setTextColor(RED);
                }
                holder.tv_group_amt.setText(amountValue);

                //update groupedTransactionRecyclerViewAdapter
                holder.groupedTransactionRecyclerViewAdapter.setmGroupedTransactions(curDateTransactions);
                holder.groupedTransactionRecyclerViewAdapter.setSelectedTransactions(selectedTransactions);

                if (selectedItems.contains(position)) {
                    //if item is selected then,set foreground color of FrameLayout.
                    holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccentTransparent)));
                } else {
                    //else remove selected item color.
                    holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
                }

            }
        } else {
            Log.e(TAG,"Date for position - " + position + " is null !");
        }
    }

    private Long getGroupedTransactionAmount(ArrayList<Transaction> curDateTransactions) {
        Long groupTransactionAmt = new Long(0);

        for (Transaction transaction:curDateTransactions){
            Long transactionAmt = transaction.getAmount();
            if(transaction.getType()== GlobalConstants.TransactionType.CREDIT){
                groupTransactionAmt += transactionAmt;
            } else {
                groupTransactionAmt -= transactionAmt;
            }
        }

        return groupTransactionAmt;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnTransactionClickListener(OnTransactionClickListener onTransactionClickListener) {
        this.onTransactionClickListener = onTransactionClickListener;
    }

    //method to update selected items
    public void setSelectedItems(TreeSet<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public void setSelectedTransactions(TreeSet<Transaction> selectedTransactions) {
        this.selectedTransactions = selectedTransactions;
        notifyDataSetChanged();
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

        private TextView tv_date, tv_group_amt;

        private ArrayList<Transaction> mGroupedTransactions;

        private RecyclerView groupedTransactionsRecyclerView;
        private GroupedTransactionsAdapter groupedTransactionRecyclerViewAdapter;
        private RecyclerView.LayoutManager groupedTransactionsRecyclerViewLayoutManager;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            //perform necessary ops if current item is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
            //perform necessary ops if current item is long clicked
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemLongClick(position);
                    }
                    return true;
                }
            });

            Log.i(S_TAG, S_TAG + ": Constructor starts");
            tv_date = itemView.findViewById(R.id.tv_show_date);
            tv_group_amt = itemView.findViewById(R.id.tv_show_group_amt);
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
            if (onTransactionClickListener != null)
                groupedTransactionRecyclerViewAdapter.setOnTransactionClickListener(onTransactionClickListener);
            groupedTransactionsRecyclerView.setLayoutManager(groupedTransactionsRecyclerViewLayoutManager);
            groupedTransactionsRecyclerView.setAdapter(groupedTransactionRecyclerViewAdapter);
        }

        public ArrayList<Transaction> getmGroupedTransactions() {
            return mGroupedTransactions;
        }

        public void setmGroupedTransactions(ArrayList<Transaction> mGroupedTransactions) {
            Log.i(TAG,"setmGroupedTransactions()");
            this.mGroupedTransactions = mGroupedTransactions;
        }

    }
}
