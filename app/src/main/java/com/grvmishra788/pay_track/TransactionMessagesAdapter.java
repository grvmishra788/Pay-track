package com.grvmishra788.pay_track;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
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
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import static com.grvmishra788.pay_track.GlobalConstants.DEFAULT_FORMAT_DAY_AND_DATE;

class TransactionMessagesAdapter extends RecyclerView.Adapter<TransactionMessagesAdapter.TransactionMessagesViewHolder>  {
    //constants
    private static final String TAG = "Pay-Track: " + TransactionMessagesAdapter.class.getName(); //constant Class TAG

    //Variables to store User Settings
    private SharedPreferences userPreferences;
    private String defaultDateFormat;

    //Variable to store context from which Adapter has been called
    private Context mContext;

    private HashMap<Date, ArrayList<TransactionMessage>> datedTransactionMessagesHashMap;
    private SortedList<Date> dates;

    //Variable for onItemClickListener
    private OnItemClickListener onItemClickListener;
    private OnTransactionMessageClickListener onTransactionMessageClickListener;

    //Variable to select dates when launching Contextual action mode
    private TreeSet<Integer> selectedItems = new TreeSet<>();

    //Variable to store transactions when launching Contextual action mode
    private TreeSet<TransactionMessage> selectedTransactionMessages = new TreeSet<>();

    public TransactionMessagesAdapter(Context mContext, HashMap<Date, ArrayList<TransactionMessage>> datedTransactionMessagesHashMap) {
        this.mContext = mContext;
        this.datedTransactionMessagesHashMap = datedTransactionMessagesHashMap;
        //--------------------init user settings----------------------//
        userPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (userPreferences != null) {
            defaultDateFormat = userPreferences.getString(mContext.getString(R.string.pref_key_date_format), "" );
        } else {
            defaultDateFormat = DEFAULT_FORMAT_DAY_AND_DATE;
        }


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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull TransactionMessagesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() :: " + position );
        if(position==0){
            createSortedDatesList();
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

                SimpleDateFormat sdf=new SimpleDateFormat(defaultDateFormat);
                String dateString = sdf.format(date);
                holder.tv_date.setText(dateString);

                //update groupedTransactionMessageRecyclerViewAdapter
                holder.groupedTransactionMessageRecyclerViewAdapter.setmGroupedTransactionMessages(curDateTransactionMessages);
                holder.groupedTransactionMessageRecyclerViewAdapter.setSelectedTransactionMessages(selectedTransactionMessages);

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

    @Override
    public int getItemCount() {
        if(datedTransactionMessagesHashMap!=null)
            return datedTransactionMessagesHashMap.size();
        else
            return 0;
    }

    private void createSortedDatesList() {
        List<Date> datesList = new ArrayList<Date>(datedTransactionMessagesHashMap.keySet()); // <== Set to List
        dates = new SortedList<Date>(Date.class, new SortedList.Callback<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1); // o2 compares to o1 for descending order
            }

            @Override
            public void onChanged(int position, int count) {
            }

            @Override
            public boolean areContentsTheSame(Date oldItem, Date newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Date item1, Date item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
            }

            @Override
            public void onRemoved(int position, int count) {
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
            }
        });
        dates.beginBatchedUpdates();
        for (int i = 0; i < datesList.size(); i++) {
            dates.add(datesList.get(i));
        }
        dates.endBatchedUpdates();
        Log.d(TAG,"All dates - " + dates);
    }

    public SortedList<Date> getDates() {
        return dates;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnTransactionMessageClickListener(OnTransactionMessageClickListener onTransactionMessageClickListener) {
        this.onTransactionMessageClickListener = onTransactionMessageClickListener;
    }

    //method to update selected items
    public void setSelectedItems(TreeSet<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public void setSelectedTransactionMessages(TreeSet<TransactionMessage> selectedTransactionMessages) {
        this.selectedTransactionMessages = selectedTransactionMessages;
        notifyDataSetChanged();
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
            if (onTransactionMessageClickListener != null)
                groupedTransactionMessageRecyclerViewAdapter.setOnTransactionMessageClickListener(onTransactionMessageClickListener);
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
