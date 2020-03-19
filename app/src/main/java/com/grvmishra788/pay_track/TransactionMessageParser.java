package com.grvmishra788.pay_track;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.grvmishra788.pay_track.GlobalConstants.SMS_AMOUNT_REGEX;

public class TransactionMessageParser {

    public static boolean hasAmount(String msg){
        Pattern pattern = Pattern.compile(SMS_AMOUNT_REGEX);
        Matcher matcher = pattern.matcher(msg);
        return matcher.find();
    }

}
