package com.grvmishra788.pay_track.DS;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class SubCategory implements Serializable {

    private String subCategoryName;
    private String description;
    private String associatedAccountNickName;
    private String parent;


    public SubCategory(String subCategoryName, String parent) {
        this.subCategoryName = subCategoryName;
        this.parent = parent;
    }

    public SubCategory(String subCategoryName, String associatedAccountNickName, String description, String parent) {
        this.subCategoryName = subCategoryName;
        this.associatedAccountNickName = associatedAccountNickName;
        this.description = description;
        this.parent = parent;

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

    public String getAssociatedAccountNickName() {
        return associatedAccountNickName;
    }

    public void setAssociatedAccountNickName(String associatedAccountNickName) {
        this.associatedAccountNickName = associatedAccountNickName;
    }

    @NonNull
    @Override
    public String toString() {
        return  "SubCategory -" +
                " subCategoryName : " + subCategoryName +
                " description : " + description +
                " associatedAccountNickName : " + associatedAccountNickName +
                " parent : " + parent ;
    }
}
