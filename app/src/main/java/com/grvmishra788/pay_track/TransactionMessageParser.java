package com.grvmishra788.pay_track;

import android.content.Context;

import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.BankAccount;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.DigitalAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.grvmishra788.pay_track.GlobalConstants.SMS_ACCOUNT_NO_REGEX;
import static com.grvmishra788.pay_track.GlobalConstants.SMS_AMOUNT_REGEX;

public class TransactionMessageParser {

    public static boolean hasAmount(String msg){
        Pattern pattern = Pattern.compile(SMS_AMOUNT_REGEX);
        Matcher matcher = pattern.matcher(msg);
        return matcher.find();
    }

    public static String getAmountStr(String msg) {
        String amt = "";
        List<String> allMatches = new ArrayList<String>();
        //match with amount regex
        Matcher m = Pattern.compile(SMS_AMOUNT_REGEX)
                .matcher(msg);
        while (m.find()) {
            allMatches.add(m.group());
        }
        if(allMatches.size()>0){
            //if 1 or more matches, get the first match as AMT
            amt = allMatches.get(0);
            //replace all alphabets  to get rid of Rs/INR etc
            amt = amt.replace("Rs.", ""); // replacement for Rs.
            amt = amt.replaceAll("[^\\d.]", ""); // regex replacement for non-decimal character
        }
        return amt;
    }

    public static GlobalConstants.TransactionType getTransactionType(String msg) {
        GlobalConstants.TransactionType type = GlobalConstants.TransactionType.DEBIT; // keep type as debit by default
        if (msg.toLowerCase().contains("credit") && !msg.toLowerCase().contains("debit")){
            //if the msg contains string credit but doesn't contain string debit, set type as CREDIT
            type = GlobalConstants.TransactionType.CREDIT;
        }
        return type;
    }

    public static String getAccountNickName(Context context, String sender, String msg) {

        //init vars
        String matchedAccount = "";
        ArrayList<CashAccount> accounts = new DbHelper(context).getAllAccounts();
        ArrayList<CashAccount> matches = new ArrayList<>();

        //match based on bank/service name
        for (CashAccount account: accounts) {
            if(account instanceof BankAccount){
                String bankName = ((BankAccount) account).getBankName().toLowerCase();
                if(sender.toLowerCase().contains(bankName) || msg.toLowerCase().contains(bankName)){
                    matches.add(account);
                }
            } else if (account instanceof DigitalAccount) {
                String serviceName = ((DigitalAccount) account).getServiceName().toLowerCase();
                if(sender.toLowerCase().contains(serviceName) || msg.toLowerCase().contains(serviceName)){
                    matches.add(account);
                }
            }
        }

        if(matches.size()==1) {
            matchedAccount = matches.get(0).getNickName();
        } else {

            //get account no from msg
            List<String> allMatches = new ArrayList<String>();
            Matcher m = Pattern.compile(SMS_ACCOUNT_NO_REGEX)
                    .matcher(msg);
            while (m.find()) {
                allMatches.add(m.group());
            }

            //match account no
            if(allMatches.size()>0){
                //if 1 or more matches, get the first match as account no
                String accountNo = allMatches.get(0);
                accountNo = accountNo.substring(accountNo.length()-3);  //get Last 3 digits of account number
                for (CashAccount account:accounts){
                    if(account instanceof BankAccount && ((BankAccount) account).getAccountNumber().contains(accountNo)){
                        matchedAccount = account.getNickName();
                        break;
                    }
                }
            }
        }
        return matchedAccount;
    }

}
