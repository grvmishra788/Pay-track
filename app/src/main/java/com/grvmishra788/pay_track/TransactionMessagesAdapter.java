package com.grvmishra788.pay_track;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.TransactionMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

class TransactionMessagesAdapter extends RecyclerView.Adapter<TransactionMessagesAdapter.TransactionMessagesViewHolder>  {
    //constants
    private static final String TAG = "Pay-Track: " + TransactionMessagesAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    private HashMap<Date, ArrayList<TransactionMessage>> datedTransactionMessagesHashMap;
    private List<Date> dates;

    TransactionMessagesAdapter(Context mContext, HashMap<Date, ArrayList<TransactionMessage>> datedTransactionMessagesHashMap) {
        this.mContext = mContext;
        this.datedTransactionMessagesHashMap = datedTransactionMessagesHashMap;
    }

    @NonNull
    @Override
    public TransactionMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_transaction_message, parent, false);
        TransactionMessagesViewHolder transactionMessagesViewHolder = new TransactionMessagesViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return transactionMessagesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionMessagesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() :: " + position );
        if(position==0){
            dates = new ArrayList<Date>(datedTransactionMessagesHashMap.keySet()); // <== Set to List
            Log.d(TAG,"All dates - " + dates);
        }

        Date date = dates.get(position);

        //convert date to string & display in text view
        if(date!=null){
            Log.d(TAG,"Date for position - " + position + " is - " + date);
            ArrayList<TransactionMessage> curDateTransactionMessages = datedTransactionMessagesHashMap.get(date);
            if (curDateTransactionMessages == null || curDateTransactionMessages.size()==0) {
                holder.itemView.setVisibility(View.GONE);
            } else {

                holder.itemView.setVisibility(View.VISIBLE);
                //update grouped Transactions
                holder.setmGroupedTransactionMessages(curDateTransactionMessages);

                SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_DAY_AND_DATE);
                String dateString = sdf.format(date);
                holder.tv_date.setText(dateString);

                //update groupedTransactionRecyclerViewAdapter
                holder.groupedTransactionMessageRecyclerViewAdapter.setmGroupedTransactionMessages(curDateTransactionMessages);

            }
        } else {
            Log.e(TAG,"Date for position - " + position + " is null !");
        }


    }

    @Override
    public int getItemCount() {
        if(datedTransactionMessagesHashMap!=null)
            return datedTransactionMessagesHashMap.size();
        else
            return 0;
    }

    public class TransactionMessagesViewHolder extends RecyclerView.ViewHolder {

        //constants
        private final String S_TAG = "Pay-Track: " + TransactionMessagesViewHolder.class.getName(); //constant Class TAG

        private TextView tv_date, tv_group_amt;

        private ArrayList<TransactionMessage> mGroupedTransactionMessages;

        private RecyclerView groupedTransactionMessagesRecyclerView;
        private GroupedTransactionMessagesAdapter groupedTransactionMessageRecyclerViewAdapter;
        private RecyclerView.LayoutManager groupedTransactionMessagesRecyclerViewLayoutManager;

        public TransactionMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i(S_TAG, S_TAG + ": Constructor starts");
            tv_date = itemView.findViewById(R.id.tv_show_date);
            tv_group_amt = itemView.findViewById(R.id.tv_show_group_amt);
            groupedTransactionMessagesRecyclerView = itemView.findViewById(R.id.recyclerview_item_transaction_message);
            initItemTransactionMessageRecyclerView();
            Log.i(S_TAG, S_TAG + ": Constructor ends");
        }

        private void initItemTransactionMessageRecyclerView() {
            Log.i(TAG,"initItemTransactionMessageRecyclerView()");
            if(mGroupedTransactionMessages ==null){
                mGroupedTransactionMessages = new ArrayList<>();
            }

            groupedTransactionMessagesRecyclerView.setHasFixedSize(true);
            groupedTransactionMessagesRecyclerViewLayoutManager = new LinearLayoutManager(mContext);
            groupedTransactionMessageRecyclerViewAdapter = new GroupedTransactionMessagesAdapter(mContext, mGroupedTransactionMessages);
//            if (onTransactionMessageClickListener != null)
//                groupedTransactionMessageRecyclerViewAdapter.setOnTransactionMessageClickListener(onTransactionMessageClickListener);
            groupedTransactionMessagesRecyclerView.setLayoutManager(groupedTransactionMessagesRecyclerViewLayoutManager);
            groupedTransactionMessagesRecyclerView.setAdapter(groupedTransactionMessageRecyclerViewAdapter);
        }

        public ArrayList<TransactionMessage> getmGroupedTransactionMessages() {
            return mGroupedTransactionMessages;
        }

        public void setmGroupedTransactionMessages(ArrayList<TransactionMessage> mGroupedTransactionMessages) {
            Log.i(TAG,"setmGroupedTransactionMessages()");
            this.mGroupedTransactionMessages = mGroupedTransactionMessages;
        }

    }
}
