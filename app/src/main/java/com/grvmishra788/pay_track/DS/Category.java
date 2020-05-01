package com.grvmishra788.pay_track.DS;

import android.util.Log;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;

public class Category implements Serializable {
    private String categoryName;
    private String description;
    private String accountNickName;
    private SortedList<SubCategory> subCategories;

    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.description = null;
        this.accountNickName = null;
        this.subCategories = null;
    }

    public Category(String categoryName, String accountNickName, String description) {
        this.categoryName = categoryName;
        this.accountNickName = accountNickName;
        this.description = description;
        this.subCategories = null;
    }

    public Category(String categoryName, String accountNickName, String description, SortedList<SubCategory> subCategories) {
        this.categoryName = categoryName;
        this.accountNickName = accountNickName;
        this.description = description;
        this.subCategories = subCategories;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNickName() {
        return accountNickName;
    }

    public void setAccountNickName(String accountNickName) {
        this.accountNickName = accountNickName;
    }

    public void addSubCategory(SubCategory subCategory) {
        if (subCategories == null) {
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
        }
        subCategories.add(subCategory);
    }

    public void removeSubCategory(SubCategory subCategory) {
        if (subCategories == null) {
            return;
        }
        int pos = getSubCategoryIndex(subCategory);
        subCategories.removeItemAt(pos);
    }

    public SortedList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(SortedList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @NonNull
    @Override
    public String toString() {
        String str = "Category -" +
                " categoryName : " + categoryName +
                " description : " + description +
                " accountNickName : " + accountNickName +
                " Sub-Categories - ";

        if (subCategories != null) {
            for (int i=0; i<subCategories.size(); i++) {
                SubCategory subCategory = subCategories.get(i);
                str += ((subCategory.getSubCategoryName()) + ", ");
            }
        }
        return str;
    }

    private int getSubCategoryIndex(SubCategory subCategory) {
        for (int i = 0; i < subCategories.size(); i++) {
            SubCategory s = subCategories.get(i);
            if (s.getSubCategoryName().equals(subCategory.getSubCategoryName())) {
                return i;
            }
        }
        return -1;
    }
}
