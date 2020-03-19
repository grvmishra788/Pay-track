package com.grvmishra788.pay_track;

import com.grvmishra788.pay_track.DS.TransactionMessage;

public interface OnTransactionMessageClickListener {
    void onItemClick(int position, TransactionMessage transactionMessage);
    void onItemLongClick(int position, TransactionMessage transactionMessage);
}
