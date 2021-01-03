package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;
import com.grvmishra788.pay_track.DS.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class AnalyzeCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    //constants
    private static final String TAG = "Pay-Track: " + AnalyzeCategoryAdapter.class.getName(); //constant Class TAG
    private Context mContext;
    private SortedList<Category> categories;
    private HashMap<Category, ArrayList<Transaction>> mFilterTransactionHashMap;
    private HashMap<SubCategory, ArrayList<Transaction>> mFilterSubCategoryTransactionHashMap;
    private HashMap<SubCategory, ArrayList<Transaction>> mFilterSubCategoryTransactionHashMapInParent;

    public void setFilterTransactionHashMap(HashMap<Category, ArrayList<Transaction>> mFilterTransactionHashMap) {
        this.mFilterTransactionHashMap = mFilterTransactionHashMap;
    }

    public void setFilterSubCategoryTransactionHashMap(HashMap<SubCategory, ArrayList<Transaction>> filterSubCategoryTransactionHashMap) {
        mFilterSubCategoryTransactionHashMap = filterSubCategoryTransactionHashMap;
    }

    public AnalyzeCategoryAdapter(Context context, HashMap<Category, ArrayList<Transaction>> filterTransactionHashMap, HashMap<SubCategory, ArrayList<Transaction>> filterSubCategoryTransactionHashMap){
        mContext = context;
        this.mFilterTransactionHashMap = filterTransactionHashMap;
        this.mFilterSubCategoryTransactionHashMap = filterSubCategoryTransactionHashMap;
        createSortedCategoriesList();
    }

    public void refreshCategoriesList() {
        createSortedCategoriesList();
        notifyDataSetChanged();
    }

    private void createSortedCategoriesList() {
        List<Category> categoriesList = new ArrayList<Category>(mFilterTransactionHashMap.keySet()); // <== Set to List
        if(categories ==null)
            categories = new SortedList<Category>(Category.class, new SortedList.Callback<Category>() {
                @Override
                public int compare(Category o1, Category o2) {
                    return o1.getCategoryName().toLowerCase().compareTo(o2.getCategoryName().toLowerCase());
                }

                @Override
                public void onChanged(int position, int count) {

                }

                @Override
                public boolean areContentsTheSame(Category oldItem, Category newItem) {
                    return false;
                }

                @Override
                public boolean areItemsTheSame(Category item1, Category item2) {
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
        categories.clear();
        categories.beginBatchedUpdates();
        for (int i = 0; i < categoriesList.size(); i++) {
            categories.add(categoriesList.get(i));
        }
        categories.endBatchedUpdates();
        Log.d(TAG,"All months - " + categories);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_category_summary, parent, false);
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Category category = categories.get(position);
        SortedList<SubCategory> subCategories = category.getSubCategories();
        if (subCategories == null || subCategories.size()==0 ) {
            ((CategoryViewHolder)holder).horizontal_bar.setVisibility(View.GONE);
        } else {
            ((CategoryViewHolder)holder).horizontal_bar.setVisibility(View.VISIBLE);
        }

        Double[] expenseOverview = getExpensesOverview(category);

        ((CategoryViewHolder) holder).tv_title.setText(category.getCategoryName());
        ((CategoryViewHolder) holder).tv_income.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[0]));
        ((CategoryViewHolder) holder).tv_expenses.setText(Utilities.getAmountWithRupeeSymbol(expenseOverview[1]));
        if(expenseOverview[2]<0){
            ((CategoryViewHolder) holder).tv_total.setText(" - " + Utilities.getAmountWithRupeeSymbol(-expenseOverview[2]));
        } else {
            ((CategoryViewHolder) holder).tv_total.setText(" + " + Utilities.getAmountWithRupeeSymbol(expenseOverview[2]));
        }

        filterSubCategoryHashMapInParent(category.getCategoryName());
        ((CategoryViewHolder)holder).setSubCategoryHashMap(mFilterSubCategoryTransactionHashMapInParent);
        ((CategoryViewHolder)holder).subCategoryRecyclerViewAdapter.refreshSubCategoriesList();
    }

    @Override
    public int getItemCount() {
        if(categories!=null)
            return categories.size();
        else
            return 0;
    }

    private Double[] getExpensesOverview(Category category) {
        //amount[0] -> stores income
        //amount[1] -> stores expenses
        //amount[2] -> stores total
        Double[] amount = new Double[]{(double)0,(double)0,(double)0};
        ArrayList<Transaction> transactions = mFilterTransactionHashMap.get(category);
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

    private void filterSubCategoryHashMapInParent(String parent){
        if(mFilterSubCategoryTransactionHashMapInParent==null){
            mFilterSubCategoryTransactionHashMapInParent = new HashMap<>();
        } else {
            mFilterSubCategoryTransactionHashMapInParent.clear();
        }

        if(mFilterSubCategoryTransactionHashMap!=null) {
            Iterator it = mFilterSubCategoryTransactionHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<SubCategory, ArrayList<Transaction>> pair = (Map.Entry) it.next();
                SubCategory subCategory = pair.getKey();
                if (subCategory.getParent().equals(parent)) {
                    mFilterSubCategoryTransactionHashMapInParent.put(pair.getKey(), pair.getValue());
                }
            }
        }

    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_income, tv_expenses, tv_total, tv_title;
        private View horizontal_bar;

        private HashMap<SubCategory, ArrayList<Transaction>> mFilterSubCategoryTransactionHashMap;
        private RecyclerView subCategoryRecyclerView;
        private AnalyzeSubCategoryAdapter subCategoryRecyclerViewAdapter;
        private RecyclerView.LayoutManager subCategoryRecyclerViewLayoutManager;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_income = itemView.findViewById(R.id.tv_show_income);
            tv_expenses = itemView.findViewById(R.id.tv_show_expenses);
            tv_total = itemView.findViewById(R.id.tv_show_total);
            //init horizontal bar
            horizontal_bar = itemView.findViewById(R.id.horizontal_bar);
            //init sub category RecyclerView
            subCategoryRecyclerView = itemView.findViewById(R.id.sub_items_recyclerview);
            initSubCategoryRecyclerView();
        }

        public void setSubCategoryHashMap(HashMap<SubCategory, ArrayList<Transaction>> filterSubCategoryTransactionHashMap) {
            this.mFilterSubCategoryTransactionHashMap = filterSubCategoryTransactionHashMap;
            subCategoryRecyclerViewAdapter.setFilterSubCategoryTransactionHashMap(filterSubCategoryTransactionHashMap);
            subCategoryRecyclerViewAdapter.refreshSubCategoriesList();
        }

        private void initSubCategoryRecyclerView() {
            if (mFilterSubCategoryTransactionHashMap == null) {
               mFilterSubCategoryTransactionHashMap = new HashMap<>();
            }

            subCategoryRecyclerView.setHasFixedSize(true);
            subCategoryRecyclerView.setNestedScrollingEnabled(false);
            subCategoryRecyclerViewLayoutManager = new LinearLayoutManager(mContext);
            subCategoryRecyclerViewAdapter = new AnalyzeSubCategoryAdapter(mContext, mFilterSubCategoryTransactionHashMap);
            subCategoryRecyclerView.setLayoutManager(subCategoryRecyclerViewLayoutManager);
            subCategoryRecyclerView.setAdapter(subCategoryRecyclerViewAdapter);
        }
    }

}
