package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.SubCategory;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {
    //constants
    private static final String TAG = "Pay-Track: " + SubCategoriesAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    //Variable for accessing Categories List in  CategoriesAdapter
    private ArrayList<SubCategory> mSubCategories;

    public SubCategoriesAdapter(Context mContext, ArrayList<SubCategory> mSubCategories) {
        this.mContext = mContext;
        this.mSubCategories = mSubCategories;
    }

    @NonNull
    @Override
    public SubCategoriesAdapter.SubCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sub_item_category, parent, false);
        SubCategoriesViewHolder categoriesViewHolder = new SubCategoriesViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return categoriesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoriesViewHolder subCategoriesViewHolder, int position) {
        SubCategory subCategory = mSubCategories.get(position);
        subCategoriesViewHolder.tv_subCategoryName.setText(subCategory.getSubCategoryName());
        subCategoriesViewHolder.tv_defaultAccount.setText(subCategory.getAssociatedAccountNickName());
        subCategoriesViewHolder.tv_description.setText(subCategory.getDescription());

    }

    @Override
    public int getItemCount() {
        if(mSubCategories==null)
            return 0;
        else
            return mSubCategories.size();

    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.mSubCategories = subCategories;
    }

    public class SubCategoriesViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_subCategoryName, tv_defaultAccount, tv_description;

        public SubCategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_subCategoryName = itemView.findViewById(R.id.tv_show_sub_category_name);
            tv_defaultAccount = itemView.findViewById(R.id.tv_show_default_account);
            tv_description = itemView.findViewById(R.id.tv_show_description);

        }
    }
}
