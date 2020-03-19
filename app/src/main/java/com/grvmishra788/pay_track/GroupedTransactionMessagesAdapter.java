package com.grvmishra788.pay_track;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.TransactionMessage;

import java.util.ArrayList;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class GroupedTransactionMessagesAdapter extends RecyclerView.Adapter<GroupedTransactionMessagesAdapter.GroupedTransactionMessagesHolder>  {
    //constants
    private static final String TAG = "Pay-Track: " + GroupedTransactionMessagesAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    private ArrayList<TransactionMessage> mGroupedTransactionMessages;

    //Variable for onItemclickListener
    private OnTransactionMessageClickListener onTransactionMessageClickListener;

    //Variable to store subcategories when launching Contextual action mode
    private TreeSet<TransactionMessage> selectedTransactionMessages = new TreeSet<>();


    public GroupedTransactionMessagesAdapter(Context context, ArrayList<TransactionMessage> mGroupedTransactionMessages) {
        this.mContext = context;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull GroupedTransactionMessagesHolder holder, int position) {
        TransactionMessage transactionMessage = mGroupedTransactionMessages.get(position);
        holder.sender.setText(transactionMessage.getSrc());
        holder.msg.setText(transactionMessage.getBody());

        if (selectedTransactionMessages.contains(transactionMessage)) {
            //if item is selected then,set foreground color of FrameLayout.
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccentTransparent)));
        } else {
            //else remove selected item color.
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        }

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

    public void setOnTransactionMessageClickListener(OnTransactionMessageClickListener onTransactionMessageClickListener) {
        this.onTransactionMessageClickListener = onTransactionMessageClickListener;
    }

    public void setSelectedTransactionMessages(TreeSet<TransactionMessage> selectedTransactionMessages) {
        this.selectedTransactionMessages = selectedTransactionMessages;
        notifyDataSetChanged();
    }

    public class GroupedTransactionMessagesHolder extends RecyclerView.ViewHolder{
        //constants
        private final String S_TAG = "Pay-Track: " + GroupedTransactionMessagesAdapter.GroupedTransactionMessagesHolder.class.getName(); //constant Class TAG

        private TextView sender, msg;

        public GroupedTransactionMessagesHolder(@NonNull View itemView) {
            super(itemView);
            Log.i(S_TAG, S_TAG + ": Constructor starts");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onTransactionMessageClickListener != null && position != RecyclerView.NO_POSITION) {
                        onTransactionMessageClickListener.onItemClick(position, mGroupedTransactionMessages.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (onTransactionMessageClickListener != null && position != RecyclerView.NO_POSITION) {
                        onTransactionMessageClickListener.onItemLongClick(position, mGroupedTransactionMessages.get(position));
                    }
                    return true;
                }
            });
            sender = itemView.findViewById(R.id.tv_show_sender);
            msg = itemView.findViewById(R.id.tv_show_msg);
            Log.i(S_TAG, S_TAG + ": Constructor ends");
        }
    }
}
