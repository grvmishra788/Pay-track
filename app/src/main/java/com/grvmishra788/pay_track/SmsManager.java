package com.grvmishra788.pay_track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.TransactionMessage;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.grvmishra788.pay_track.GlobalConstants.SMS_AMOUNT_REGEX;

public class SmsManager extends BroadcastReceiver {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + SmsManager.class.getName();

    private static DbHelper payTrackDBHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        payTrackDBHelper = new DbHelper(context);

        String strMessage = "";

        if ( extras != null )
        {
            Object[] smsExtras = (Object[]) extras.get( "pdus" );

            for ( int i = 0; i < smsExtras.length; i++ )
            {
                SmsMessage smsMsg = SmsMessage.createFromPdu((byte[])smsExtras[i]);

                String strMsgBody = smsMsg.getMessageBody().toString();
                String strMsgSrc = smsMsg.getOriginatingAddress();

                if(TransactionMessageParser.hasAmount(strMsgBody)){
                    // set Today as default date
                    //create date object
                    Calendar calendar = Calendar.getInstance();
                    //set time part of date as 0
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR, 0);
                    Date date = calendar.getTime();

                    UUID id = UUID.randomUUID();

                    TransactionMessage transactionMessage = new TransactionMessage(id, strMsgSrc, strMsgBody, date);

                    if(payTrackDBHelper.insertDataToTransactionMessagesTable(transactionMessage)){
                        Toast.makeText(context, "Added Transaction Message to db", Toast.LENGTH_SHORT).show();;
                    } else {
                        Toast.makeText(context, "Couldn't Add Transaction Message to db", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String msg = strMsgBody + " - doesn't have amount";
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, msg);
                }

            }

        }

    }
}
