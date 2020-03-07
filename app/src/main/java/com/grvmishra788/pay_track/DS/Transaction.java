package com.grvmishra788.pay_track.DS;

import com.grvmishra788.pay_track.GlobalConstants;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private long amount;
    private TransactionCategory transactionCategory;
    private Date date;
    private String description;
    private GlobalConstants.TransactionType type;
    private CashAccount account;

    public Transaction(long amount, TransactionCategory transactionCategory, Date date, String description, GlobalConstants.TransactionType type, CashAccount account) {
        this.amount = amount;
        this.transactionCategory = transactionCategory;
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

    public TransactionCategory getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(TransactionCategory transactionCategory) {
        this.transactionCategory = transactionCategory;
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

    public CashAccount getAccount() {
        return account;
    }

    public void setAccount(CashAccount account) {
        this.account = account;
    }
}
