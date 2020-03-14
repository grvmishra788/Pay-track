package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    //Variable for onItemclickListener
    private OnSubCategoryClickListener onSubCategoryClickListener;


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
        subCategoriesViewHolder.tv_account.setText(subCategory.getAccountNickName());

        String desc = subCategory.getDescription();
        subCategoriesViewHolder.tv_description.setText(desc);
        if(InputValidationUtilities.isValidString(desc)){
            subCategoriesViewHolder.ll_show_description.setVisibility(View.VISIBLE);
        } else {
            subCategoriesViewHolder.ll_show_description.setVisibility(View.GONE);
        }


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

    public OnSubCategoryClickListener getOnSubCategoryClickListener() {
        return onSubCategoryClickListener;
    }

    public void setOnSubCategoryClickListener(OnSubCategoryClickListener onSubCategoryClickListener) {
        this.onSubCategoryClickListener = onSubCategoryClickListener;
    }

    public class SubCategoriesViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_subCategoryName, tv_account, tv_description;

        //Variables to store linear layout associated with category description
        private LinearLayout ll_show_description;


        public SubCategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onSubCategoryClickListener != null && position != RecyclerView.NO_POSITION) {
                        onSubCategoryClickListener.onItemClick(position, mSubCategories.get(position));
                    }
                }
            });

            tv_subCategoryName = itemView.findViewById(R.id.tv_show_sub_category_name);
            tv_account = itemView.findViewById(R.id.tv_show_default_account);
            tv_description = itemView.findViewById(R.id.tv_show_description);

            //init description linear layout
            ll_show_description = itemView.findViewById(R.id.ll_show_description);

        }
    }
}
