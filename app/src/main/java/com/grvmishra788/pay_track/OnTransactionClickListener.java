package com.grvmishra788.pay_track;

import com.grvmishra788.pay_track.DS.Transaction;

interface OnTransactionClickListener {
    void onItemClick(int position, Transaction transaction);
    void onItemLongClick(int position, Transaction transaction);
}
