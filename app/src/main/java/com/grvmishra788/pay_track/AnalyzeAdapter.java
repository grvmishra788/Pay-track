package com.grvmishra788.pay_track;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class AnalyzeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //constants
    private static final String TAG = "Pay-Track: " + AnalyzeAdapter.class.getName(); //constant Class TAG

    public void setFilterTransactionHashMap(HashMap<Date, ArrayList<Transaction>> mFilterTransactionHashMap) {
        this.mFilterTransactionHashMap = mFilterTransactionHashMap;
    }

    private HashMap<Date, ArrayList<Transaction>> mFilterTransactionHashMap;
    private SortedList<Date> months;
    private Context mContext;

    public AnalyzeAdapter(Context context, HashMap<Date, ArrayList<Transaction>> filterTransactionHashMap){
        mContext = context;
        mFilterTransactionHashMap = filterTransactionHashMap;
        refreshMonthsList();
    }

    public void refreshMonthsList() {
        createSortedMonthsList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_transaction_summary, parent, false);
        MonthlyViewHolder monthlyViewHolder = new MonthlyViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return monthlyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Date date = getMonthAt(position);
        Double[] expenseOverview = getExpensesOverview(date);

        SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_MONTH_AND_YEAR);
        String dateString = sdf.format(date);
        ((MonthlyViewHolder) holder).tv_title.setText(dateString);
        ((MonthlyViewHolder) holder).tv_income.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[0]));
        ((MonthlyViewHolder) holder).tv_expenses.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[1]));
        if(expenseOverview[2]<0){
            ((MonthlyViewHolder) holder).tv_total.setText(" - " + Utilities.getAmountWithRupeeSymbol(-expenseOverview[2]));
        } else {
            ((MonthlyViewHolder) holder).tv_total.setText(" + " + Utilities.getAmountWithRupeeSymbol(expenseOverview[2]));
        }
    }

    @Override
    public int getItemCount() {
        if(mFilterTransactionHashMap!=null)
            return mFilterTransactionHashMap.size();
        else
            return 0;
    }

    public class MonthlyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_income, tv_expenses, tv_total, tv_title;
        public MonthlyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_income = itemView.findViewById(R.id.tv_show_income);
            tv_expenses = itemView.findViewById(R.id.tv_show_expenses);
            tv_total = itemView.findViewById(R.id.tv_show_total);
        }
    }

    private Double[] getExpensesOverview(Date date) {
        //amount[0] -> stores income
        //amount[1] -> stores expenses
        //amount[2] -> stores total
        Double[] amount = new Double[]{(double)0,(double)0,(double)0};
        ArrayList<Transaction> transactions = mFilterTransactionHashMap.get(date);
        if(transactions!=null){
            for (Transaction transaction : transactions) {
                if (transaction.getType() == GlobalConstants.TransactionType.CREDIT) {
                    amount[0] += transaction.getAmount();
                } else {
                    amount[1] += transaction.getAmount();
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

    // use LinkedHashMap if you want to read values from the hashmap in the same order as you put them into it
    private Date getMonthAt(int index) {
        if(index>=mFilterTransactionHashMap.size()){
            Log.e(TAG,"Wrong index: index - "+index+" > size - "+mFilterTransactionHashMap.size());
            return null;
        }
        return months.get(index);
    }

    private void createSortedMonthsList() {
        List<Date> datesList = new ArrayList<Date>(mFilterTransactionHashMap.keySet()); // <== Set to List
        if(months ==null)
            months = new SortedList<Date>(Date.class, new SortedList.Callback<Date>() {
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
        months.clear();
        months.beginBatchedUpdates();
        for (int i = 0; i < datesList.size(); i++) {
            months.add(datesList.get(i));
        }
        months.endBatchedUpdates();
        Log.d(TAG,"All months - " + months);
    }
}
