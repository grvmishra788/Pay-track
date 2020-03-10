package com.grvmishra788.pay_track.DS;

import com.grvmishra788.pay_track.GlobalConstants;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private long amount;
    private String category;
    private Date date;
    private String description;
    private GlobalConstants.TransactionType type;
    private String account;

    public Transaction(long amount, String category, Date date, String description, GlobalConstants.TransactionType type, String account) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.type = type;
        this.account = account;
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
}
