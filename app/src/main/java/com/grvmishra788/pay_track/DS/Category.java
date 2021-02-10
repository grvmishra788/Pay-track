package com.grvmishra788.pay_track.DS;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.SortedList;

public class Category implements Serializable {
    private String categoryName;
    private String description;
    private String accountNickName;
    private ArrayList<SubCategory> subCategories;

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

    public Category(String categoryName, String accountNickName, String description, ArrayList<SubCategory> subCategories) {
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
            subCategories = new ArrayList<>();
        }
        subCategories.add(subCategory);
        Collections.sort(subCategories, new Comparator<SubCategory>() {
            @Override
            public int compare(SubCategory subCategory, SubCategory subCategory1) {
                return subCategory.getSubCategoryName().toLowerCase().compareTo(subCategory1.getSubCategoryName().toLowerCase());
            }
        });
    }

    public void removeSubCategory(SubCategory subCategory) {
        if (subCategories == null) {
            return;
        }
        int pos = getSubCategoryIndex(subCategory);
        subCategories.remove(pos);

        //sort subCategories
        Collections.sort(subCategories, new Comparator<SubCategory>() {
            @Override
            public int compare(SubCategory subCategory, SubCategory subCategory1) {
                return subCategory.getSubCategoryName().toLowerCase().compareTo(subCategory1.getSubCategoryName().toLowerCase());
            }
        });
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
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

    @Override
    public int hashCode() {
        return categoryName.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Category) {
            return categoryName.equals(((Category)obj).getCategoryName());
        } else {
            return false;
        }
    }
}
