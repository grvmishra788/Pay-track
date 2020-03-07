package com.grvmishra788.pay_track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.DS.CashAccount;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_ACCOUNT;

public class AccountsFragment extends Fragment {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AccountsFragment.class.getName();

    private FloatingActionButton addAccountButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() starts...");
        View view = inflater.inflate(R.layout.layout_accounts_fragment, container, false);
        addAccountButton = view.findViewById(R.id.addItemFAB);
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addAccountButton::onClick()");
                Intent addAccountIntent = new Intent(getActivity(), AddAccountActivity.class);
                startActivityForResult(addAccountIntent, REQ_CODE_ADD_ACCOUNT);
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
            if(requestCode==REQ_CODE_ADD_ACCOUNT){
                Log.i(TAG, "Processing...");
                CashAccount account =  (CashAccount) data.getSerializableExtra(GlobalConstants.ACCOUNT_OBJECT);
                Log.i(TAG, "Added account - " + account.toString());
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }
    }
}
