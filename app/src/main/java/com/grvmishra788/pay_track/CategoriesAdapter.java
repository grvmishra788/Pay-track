package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    //constants
    private static final String TAG = "Pay-Track: " + CategoriesAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    //Variable for accessing Categories List in  CategoriesAdapter
    private ArrayList<Category> mCategories;

    //Variable for onItemclickListener
    private OnItemClickListener mOnItemClickListener;

    //Constructor: binds Category object data to CategoriesAdapter
    public CategoriesAdapter(Context context, ArrayList<Category> categories) {
        Log.d(TAG, TAG + ": Constructor starts");
        this.mContext = context;
        this.mCategories = categories;
        Log.d(TAG, TAG + ": Constructor ends");
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_category, parent, false);
        CategoriesViewHolder categoriesViewHolder = new CategoriesViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return categoriesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder categoriesViewHolder, int position) {
        Category category = mCategories.get(position);
        ArrayList<SubCategory> subCategories = category.getSubCategories();
        categoriesViewHolder.setSubCategories(subCategories);

        categoriesViewHolder.tv_categoryName.setText(category.getCategoryName());
        categoriesViewHolder.tv_defaultAccount.setText(category.getAssociatedAccountNickName());

        String desc = category.getDescription();
        categoriesViewHolder.tv_description.setText(desc);
        if(InputValidationUtilities.isValidString(desc)){
            categoriesViewHolder.ll_show_description.setVisibility(View.VISIBLE);
        } else {
            categoriesViewHolder.ll_show_description.setVisibility(View.GONE);
        }

        //update subcategories
        categoriesViewHolder.subCategoryRecyclerViewAdapter.setSubCategories(subCategories);

    }

    @Override
    public int getItemCount() {
        if(mCategories==null)
            return 0;
        else
            return mCategories.size();
    }

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_categoryName, tv_defaultAccount, tv_description;

        //Variables to store linear layout associated with category description
        private LinearLayout ll_show_description;

        private ArrayList<SubCategory> mSubCategories;

        private RecyclerView subCategoryRecyclerView;
        private SubCategoriesAdapter subCategoryRecyclerViewAdapter;
        private RecyclerView.LayoutManager subCategoryRecyclerViewLayoutManager;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mOnItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });

            tv_categoryName = itemView.findViewById(R.id.tv_show_category_name);
            tv_defaultAccount = itemView.findViewById(R.id.tv_show_default_account);
            tv_description = itemView.findViewById(R.id.tv_show_description);

            //init description linear layouts
            ll_show_description = itemView.findViewById(R.id.ll_show_description);

            //init sub category RecyclerView
            subCategoryRecyclerView = itemView.findViewById(R.id.sub_items_recyclerview);
            initSubCategoryRecyclerView();
        }

        public void setSubCategories(ArrayList<SubCategory> subCategories) {
            this.mSubCategories = subCategories;
            subCategoryRecyclerViewAdapter.notifyDataSetChanged();
        }

        public ArrayList<SubCategory> getSubCategories() {
            return mSubCategories;
        }

        private void initSubCategoryRecyclerView(){
            if(mSubCategories==null){
                mSubCategories = new ArrayList<>();
            }

            subCategoryRecyclerView.setHasFixedSize(true);
            subCategoryRecyclerViewLayoutManager = new LinearLayoutManager(mContext);
            subCategoryRecyclerViewAdapter = new SubCategoriesAdapter(mContext, mSubCategories);
            subCategoryRecyclerView.setLayoutManager(subCategoryRecyclerViewLayoutManager);
            subCategoryRecyclerView.setAdapter(subCategoryRecyclerViewAdapter);
        }
    }

}
