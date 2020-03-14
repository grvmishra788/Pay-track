package com.grvmishra788.pay_track.DS;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class SubCategory implements Serializable, Cloneable {

    private String subCategoryName;
    private String description;
    private String accountNickName;
    private String parent;


    public SubCategory(String subCategoryName, String parent) {
        this.subCategoryName = subCategoryName;
        this.parent = parent;
    }

    public SubCategory(String subCategoryName, String accountNickName, String description, String parent) {
        this.subCategoryName = subCategoryName;
        this.accountNickName = accountNickName;
        this.description = description;
        this.parent = parent;

    }

    public SubCategory(SubCategory subCategoryToEdit) {

        this.subCategoryName = subCategoryToEdit.getSubCategoryName();
        this.accountNickName = subCategoryToEdit.getAccountNickName();
        this.description = subCategoryToEdit.getDescription();
        this.parent = subCategoryToEdit.getParent();

    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
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

    @NonNull
    @Override
    public String toString() {
        return  "SubCategory -" +
                " subCategoryName : " + subCategoryName +
                " description : " + description +
                " accountNickName : " + accountNickName +
                " parent : " + parent ;
    }


    public SubCategory copy() {
        return new SubCategory(this);
    }
}
