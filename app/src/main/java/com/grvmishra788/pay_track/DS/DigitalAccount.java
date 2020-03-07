package com.grvmishra788.pay_track.DS;

import androidx.annotation.NonNull;

public class DigitalAccount extends CashAccount {

    private String serviceName;
    private String email;
    private String mobileNumber;

    public DigitalAccount(String nickName, long accountBalance, String serviceName, String email, String mobileNumber) {
        super(nickName, accountBalance);
        this.serviceName = serviceName;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
        return "Digital Account -" +
                " nickName : " + super.getNickName() +
                " accountBalance : " + super.getAccountBalance() +
                " serviceName : " + serviceName +
                " email : " + email +
                " mobileNumber : " + mobileNumber;
    }
}
