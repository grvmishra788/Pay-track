package com.grvmishra788.pay_track.DS;

import com.grvmishra788.pay_track.GlobalConstants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;

public class Transaction implements Serializable, Comparable<Transaction> {

    private UUID id;
    private long amount;
    private String category;
    private String subCategory;
    private Date date;
    private String description;
    private GlobalConstants.TransactionType type;
    private String account;

    public Transaction(UUID id, long amount, String category, String subCategory, Date date, String description, GlobalConstants.TransactionType type, String account) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.subCategory = subCategory;
        this.date = date;
        this.description = description;
        this.type = type;
        this.account = account;
    }

    public Transaction(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.category = transaction.getCategory();
        this.subCategory = transaction.getSubCategory();
        this.date = transaction.getDate();
        this.description = transaction.getDescription();
        this.type = transaction.getType();
        this.account = transaction.getAccount();
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GlobalConstants.TransactionType getType() {
        return type;
    }

    public void setType(GlobalConstants.TransactionType type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_DAY_AND_DATE);
        String dateString = sdf.format(date);

        return "Transaction -" +
                " amount : " + amount +
                " category : " + category +
                ((subCategory!=null) ? (" sub-category : " + subCategory ) : "") +
                " date : " + dateString +
                " description : " + description +
                " type : " + ((type== GlobalConstants.TransactionType.CREDIT)?1:0) +
                " account : " + account ;
    }

    public Transaction copy() {
        return new Transaction(this);
    }

    @Override
    public int compareTo(Transaction transaction) {
        return this.id.compareTo(transaction.getId());
    }

}
