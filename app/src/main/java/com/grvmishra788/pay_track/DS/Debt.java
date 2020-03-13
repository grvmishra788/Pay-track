package com.grvmishra788.pay_track.DS;

import com.grvmishra788.pay_track.GlobalConstants;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.NonNull;

public class Debt implements Serializable {
    private long amount;
    private Date date;
    private String description;
    private GlobalConstants.DebtType type;
    private String account;
    private String person;

    public Debt(long amount, Date date, String description, GlobalConstants.DebtType type, String account, String person) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.type = type;
        this.account = account;
        this.person = person;
    }


    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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

    public GlobalConstants.DebtType getType() {
        return type;
    }

    public void setType(GlobalConstants.DebtType type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    @NonNull
    @Override
    public String toString() {
        return "Debt -" +
                " amount : " + amount +
                " date : " + date.toString() +
                " description : " + description +
                " type : " + ((type== GlobalConstants.DebtType.RECEIVE)?1:0) +
                " account : " + account +
                " person : " + person;
    }
}
