package com.grvmishra788.pay_track;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.DS.Transaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_TRANSACTION;

public class TransactionsFragment extends Fragment {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + TransactionsFragment.class.getName();

    private FloatingActionButton addTransactionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() starts...");
        View view = inflater.inflate(R.layout.layout_transactions_fragment, container, false);
        addTransactionButton = view.findViewById(R.id.addItemFAB);
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addTransactionButton::onClick()");
                Intent addTransactionIntent = new Intent(getActivity(), AddTransactionActivity.class);
                startActivityForResult(addTransactionIntent, REQ_CODE_ADD_TRANSACTION);
            }
        });
        Log.i(TAG, "onCreateView() ends!");
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_ADD_TRANSACTION){
                Log.i(TAG, "Processing...");
                Transaction transaction =  (Transaction) data.getSerializableExtra(GlobalConstants.TRANSACTION_OBJECT);
                Log.i(TAG, "Added transaction - " + transaction.toString());
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }
    }

}
