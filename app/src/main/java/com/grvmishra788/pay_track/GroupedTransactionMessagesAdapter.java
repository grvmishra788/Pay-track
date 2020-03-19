package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.TransactionMessage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupedTransactionMessagesAdapter extends RecyclerView.Adapter<GroupedTransactionMessagesAdapter.GroupedTransactionMessagesHolder>  {
    //constants
    private static final String TAG = "Pay-Track: " + GroupedTransactionMessagesAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context context;

    private ArrayList<TransactionMessage> mGroupedTransactionMessages;


    public GroupedTransactionMessagesAdapter(Context context, ArrayList<TransactionMessage> mGroupedTransactionMessages) {
        this.context = context;
        this.mGroupedTransactionMessages = mGroupedTransactionMessages;
    }

    @NonNull
    @Override
    public GroupedTransactionMessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sub_item_transaction_message, parent, false);
        GroupedTransactionMessagesHolder transactionMessagesViewHolder = new GroupedTransactionMessagesHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return transactionMessagesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupedTransactionMessagesHolder holder, int position) {
        TransactionMessage transactionMessage = mGroupedTransactionMessages.get(position);
        holder.sender.setText(transactionMessage.getSrc());
        holder.msg.setText(transactionMessage.getBody());
    }

    @Override
    public int getItemCount() {
        return mGroupedTransactionMessages.size();
    }

    public ArrayList<TransactionMessage> getmGroupedTransactionMessages() {
        return mGroupedTransactionMessages;
    }

    public void setmGroupedTransactionMessages(ArrayList<TransactionMessage> mGroupedTransactionMessages) {
        this.mGroupedTransactionMessages = mGroupedTransactionMessages;
        notifyDataSetChanged();
    }

    public class GroupedTransactionMessagesHolder extends RecyclerView.ViewHolder{
        //constants
        private final String S_TAG = "Pay-Track: " + GroupedTransactionMessagesAdapter.GroupedTransactionMessagesHolder.class.getName(); //constant Class TAG

        private TextView sender, msg;

        public GroupedTransactionMessagesHolder(@NonNull View itemView) {
            super(itemView);
            Log.i(S_TAG, S_TAG + ": Constructor starts");
            sender = itemView.findViewById(R.id.tv_show_sender);
            msg = itemView.findViewById(R.id.tv_show_msg);
            Log.i(S_TAG, S_TAG + ": Constructor ends");
        }
    }
}
