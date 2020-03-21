package com.grvmishra788.pay_track.DS;

import androidx.annotation.NonNull;

public class BankAccount extends CashAccount {

    private String accountNumber;
    private String bankName;
    private String email;
    private String mobileNumber;

    public BankAccount(String nickName, Double accountBalance, String accountNumber, String bankName, String email, String mobileNumber) {
        super(nickName, accountBalance);
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bank Account -" +
                " nickName : " + super.getNickName() +
                " accountBalance : " + super.getAccountBalance() +
                " accountNumber : " + accountNumber +
                " bankName : " + bankName +
                " email : " + email +
                " mobileNumber : " + mobileNumber;
    }

}
