package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.SubCategory;
import com.grvmishra788.pay_track.DS.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class AnalyzeSubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //constants
    private static final String TAG = "Pay-Track: " + AnalyzeSubCategoryAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;
    //Variable for accessing Categories List in  CategoriesAdapter
    HashMap<SubCategory, ArrayList<Transaction>> mFilterSubCategoryTransactionHashMap;
    private SortedList<SubCategory> subCategories;

    public void setFilterSubCategoryTransactionHashMap( HashMap<SubCategory, ArrayList<Transaction>> filterSubCategoryTransactionHashMap) {
        this.mFilterSubCategoryTransactionHashMap = filterSubCategoryTransactionHashMap;
    }

    public AnalyzeSubCategoryAdapter(Context mContext, HashMap<SubCategory, ArrayList<Transaction>> filterSubCategoryTransactionHashMap) {
        this.mContext = mContext;
        this.mFilterSubCategoryTransactionHashMap = filterSubCategoryTransactionHashMap;
        createSortedSubCategoriesList();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_subcategory_summary, parent, false);
        SubCategoryViewHolder subCategoryViewHolder = new SubCategoryViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return subCategoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SubCategory category = subCategories.get(position);
        Double[] expenseOverview = getExpensesOverview(category);

        ((SubCategoryViewHolder) holder).tv_title.setText(category.getSubCategoryName());
        ((SubCategoryViewHolder) holder).tv_income.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[0]));
        ((SubCategoryViewHolder) holder).tv_expenses.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[1]));
        if(expenseOverview[2]<0){
            ((SubCategoryViewHolder) holder).tv_total.setText(" - " + Utilities.getAmountWithRupeeSymbol(-expenseOverview[2]));
        } else {
            ((SubCategoryViewHolder) holder).tv_total.setText(" + " + Utilities.getAmountWithRupeeSymbol(expenseOverview[2]));
        }
    }

    @Override
    public int getItemCount() {
        if(subCategories!=null)
            return subCategories.size();
        else
            return 0;
    }

    public void refreshSubCategoriesList() {
        createSortedSubCategoriesList();
        notifyDataSetChanged();
    }

    private void createSortedSubCategoriesList() {
        List<SubCategory> subCategoriesList = new ArrayList<SubCategory>(mFilterSubCategoryTransactionHashMap.keySet()); // <== Set to List
        if(subCategories ==null)
            subCategories = new SortedList<SubCategory>(SubCategory.class, new SortedList.Callback<SubCategory>() {
                @Override
                public int compare(SubCategory o1, SubCategory o2) {
                    return o1.getSubCategoryName().toLowerCase().compareTo(o2.getSubCategoryName().toLowerCase());
                }

                @Override
                public void onChanged(int position, int count) {

                }

                @Override
                public boolean areContentsTheSame(SubCategory oldItem, SubCategory newItem) {
                    return false;
                }

                @Override
                public boolean areItemsTheSame(SubCategory item1, SubCategory item2) {
                    return false;
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
        subCategories.clear();
        subCategories.beginBatchedUpdates();
        for (int i = 0; i < subCategoriesList.size(); i++) {
            subCategories.add(subCategoriesList.get(i));
        }
        subCategories.endBatchedUpdates();
        Log.d(TAG,"All months - " + subCategories);
    }

    private Double[] getExpensesOverview(SubCategory subCategory) {
        //amount[0] -> stores income
        //amount[1] -> stores expenses
        //amount[2] -> stores total
        Double[] amount = new Double[]{(double)0,(double)0,(double)0};
        ArrayList<Transaction> transactions = mFilterSubCategoryTransactionHashMap.get(subCategory);
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

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_income, tv_expenses, tv_total, tv_title;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_income = itemView.findViewById(R.id.tv_show_income);
            tv_expenses = itemView.findViewById(R.id.tv_show_expenses);
            tv_total = itemView.findViewById(R.id.tv_show_total);
        }
    }
}
