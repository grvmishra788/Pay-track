package com.grvmishra788.pay_track.DS;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Category implements Serializable {
    private String categoryName;
    private String description;
    private String accountNickName;
    private ArrayList<SubCategory> subCategories;

    public Category(String categoryName){
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

    public void addSubCategory(SubCategory subCategory){
        if(subCategories==null){
            subCategories=new ArrayList<>();
        }
        subCategories.add(subCategory);
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

        if(subCategories!=null){
            for(SubCategory subCategory:subCategories){
                str += ((subCategory.getSubCategoryName()) + ", ");
            }
        }
        return str;
    }
}
