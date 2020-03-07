package com.grvmishra788.pay_track.DS;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class CashAccount implements Serializable {
    private String nickName;
    private long accountBalance;

    public CashAccount(String nickName, long accountBalance){
        this.nickName = nickName;
        this.accountBalance = accountBalance;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
    }

    @NonNull
    @Override
    public String toString() {
        return "Cash Account -" +
                " nickName : " + nickName +
                " accountBalance : "+accountBalance;
    }
}
