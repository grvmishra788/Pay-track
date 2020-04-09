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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //constants
    private static final String TAG = "Pay-Track: " + TransactionsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    private HashMap<Date, ArrayList<Transaction>> allDatedTransactionHashMap, datedTransactionHashMap;
    private SortedList<Date> dates;

    //Variable for onItemClickListener
    private OnItemClickListener onItemClickListener;
    private OnTransactionClickListener onTransactionClickListener;

    //Variable to select dates when launching Contextual action mode
    private TreeSet<Integer> selectedItems = new TreeSet<>();

    //Variable to store transactions when launching Contextual action mode
    private TreeSet<Transaction> selectedTransactions = new TreeSet<>();

    //Variable to store selected month
    private String selectedMonthString;

    //Variable to represent two types of recyclerview items
    private static int TYPE_SUMMARY = 1;
    private static int TYPE_ITEM = 2;

    //Constructor: binds Transaction object data to TransactionsAdapter
    public TransactionsAdapter(Context mContext, HashMap<Date, ArrayList<Transaction>> allDatedTransactionHashMap) {
        Log.i(TAG, TAG + ": Constructor starts");
        this.mContext = mContext;
        this.allDatedTransactionHashMap = allDatedTransactionHashMap;
        //set default selected month
        Date date = Utilities.getTodayDateWithDefaultTime();
        SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_MONTH_AND_YEAR);
        this.selectedMonthString = sdf.format(date);;
        //init datedHM
        initSelectedMonthDatedTransactionsHM();
        Log.i(TAG, TAG + ": Constructor ends");
    }

    public void initSelectedMonthDatedTransactionsHM() {
        SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_MONTH_AND_YEAR);

        if(datedTransactionHashMap==null){
            datedTransactionHashMap = new HashMap<>();
        }
        datedTransactionHashMap.clear();

        if(allDatedTransactionHashMap!=null && allDatedTransactionHashMap.size()!=0) {
            //add dummy item for position zero
            datedTransactionHashMap.put(Utilities.getRandomDateFromFuture(), null);
        }

        Iterator it = allDatedTransactionHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Date, ArrayList<Transaction>> pair = (Map.Entry)it.next();
            Date date = pair.getKey();
            String dateStr = sdf.format(date);
            if(dateStr.equals(selectedMonthString)){
                datedTransactionHashMap.put(pair.getKey(), pair.getValue());
            }
        }

        createSortedDatesList();

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_SUMMARY;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view;
        if(viewType==TYPE_SUMMARY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_transaction_summary, parent, false);
            SummaryViewHolder summaryViewHolder = new SummaryViewHolder(view);
            Log.i(TAG, "onCreateViewHolder() ends!");
            return summaryViewHolder;
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_transaction, parent, false);
            TransactionsViewHolder transactionsViewHolder = new TransactionsViewHolder(view);
            Log.i(TAG, "onCreateViewHolder() ends!");
            return transactionsViewHolder;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position)==TYPE_SUMMARY){

            Double[] expenseOverview = getExpensesOverview();
            ((SummaryViewHolder) holder).tv_income.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[0]));
            ((SummaryViewHolder) holder).tv_expenses.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[1]));
            if(expenseOverview[2]<0){
                ((SummaryViewHolder) holder).tv_total.setText(" - " + Utilities.getAmountWithRupeeSymbol(-expenseOverview[2]));
            } else {
                ((SummaryViewHolder) holder).tv_total.setText(" + " + Utilities.getAmountWithRupeeSymbol(expenseOverview[2]));
            }

        } else {
            Log.d(TAG, "onBindViewHolder() :: " + position );

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
                    ((TransactionsAdapter.TransactionsViewHolder)holder).setmGroupedTransactions(curDateTransactions);

                    SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_DAY_AND_DATE);
                    String dateString = sdf.format(date);
                    ((TransactionsAdapter.TransactionsViewHolder)holder).tv_date.setText(dateString);

                    //set total ampunt for curDateTransaction
                    String amountValue = "";
                    Double groupTransactionAmount = getGroupedTransactionAmount(curDateTransactions);
                    if(groupTransactionAmount>=new Double(0)){
                        amountValue = " + " + Utilities.getAmountWithRupeeSymbol(groupTransactionAmount);
                    } else {
                        amountValue = " - " + Utilities.getAmountWithRupeeSymbol(-groupTransactionAmount);
                    }
                    ((TransactionsAdapter.TransactionsViewHolder)holder).tv_group_amt.setText(amountValue);

                    //update groupedTransactionRecyclerViewAdapter
                    ((TransactionsAdapter.TransactionsViewHolder)holder).groupedTransactionRecyclerViewAdapter.setmGroupedTransactions(curDateTransactions);
                    ((TransactionsAdapter.TransactionsViewHolder)holder).groupedTransactionRecyclerViewAdapter.setSelectedTransactions(selectedTransactions);

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
    }

    private Double[] getExpensesOverview() {
        //amount[0] -> stores income
        //amount[1] -> stores expenses
        //amount[2] -> stores total
        Iterator it = datedTransactionHashMap.entrySet().iterator();
        Double[] amount = new Double[]{(double)0,(double)0,(double)0};
        while (it.hasNext()) {
            Map.Entry<Date, ArrayList<Transaction>> pair = (Map.Entry) it.next();
            ArrayList<Transaction> transactions = pair.getValue();
            if(transactions!=null){
                for (Transaction transaction : transactions) {
                    if (transaction.getType() == GlobalConstants.TransactionType.CREDIT) {
                        amount[0] += transaction.getAmount();
                    } else {
                        amount[1] += transaction.getAmount();
                    }
                }
            }
        }
        amount[2] = amount[0] - amount[1];
        //round all values to 2 decimal places
        for(int i=0;i<3;i++){
            amount[i] = Math.round(amount[i] * 100.0) / 100.0;
        }
        return amount;
    }

    public SortedList<Date> getDates() {
        return dates;
    }

    private void createSortedDatesList() {
        List<Date> datesList = new ArrayList<Date>(datedTransactionHashMap.keySet()); // <== Set to List
        if(dates==null)
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
        dates.clear();
        dates.beginBatchedUpdates();
        for (int i = 0; i < datesList.size(); i++) {
            dates.add(datesList.get(i));
        }
        dates.endBatchedUpdates();
        Log.d(TAG,"All dates - " + dates);
    }

    private Double getGroupedTransactionAmount(ArrayList<Transaction> curDateTransactions) {
        Double groupTransactionAmt = new Double(0);

        for (Transaction transaction:curDateTransactions){
            Double transactionAmt = transaction.getAmount();
            if(transaction.getType()== GlobalConstants.TransactionType.CREDIT){
                groupTransactionAmt += transactionAmt;
            } else {
                groupTransactionAmt -= transactionAmt;
            }
        }

        //return group amount after rounding 2 decimal places
        return Math.round(groupTransactionAmt * 100.0) / 100.0;
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

    public String getSelectedMonthString() {
        return selectedMonthString;
    }

    public void setSelectedMonthString(String selectedMonthString) {
        this.selectedMonthString = selectedMonthString;
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

    public class SummaryViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_income, tv_expenses, tv_total;
        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_income = itemView.findViewById(R.id.tv_show_income);
            tv_expenses = itemView.findViewById(R.id.tv_show_expenses);
            tv_total = itemView.findViewById(R.id.tv_show_total);
        }
    }

}
