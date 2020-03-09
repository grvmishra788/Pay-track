package com.grvmishra788.pay_track;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.CashAccount;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;

public class SelectAccountActivity extends AppCompatActivity {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + SelectAccountActivity.class.getName();

    //Accounts list
    private ArrayList<CashAccount> mAccounts;

    private FloatingActionButton addAccountButton;

    //recyclerView variables
    private RecyclerView accountsRecyclerView;
    private AccountsAdapter accountsRecyclerViewAdapter;
    private RecyclerView.LayoutManager accountsRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_accounts_fragment);

        //set activity title
        setTitle(R.string.title_select_account);

        //init db
        payTrackDBHelper = new DbHelper(this);

        //init accounts list
        mAccounts = payTrackDBHelper.getAllAccounts();
        if(mAccounts == null){
            mAccounts = new ArrayList<>();
        }

        //hide FAB
        addAccountButton = findViewById(R.id.addItemFAB);
        addAccountButton.setVisibility(View.GONE);

        //init RecyclerView
        accountsRecyclerView = (RecyclerView) findViewById(R.id.show_account_recycler_view);
        accountsRecyclerView.setHasFixedSize(true);
        accountsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        accountsRecyclerViewAdapter = new AccountsAdapter(this, mAccounts);
        accountsRecyclerView.setLayoutManager(accountsRecyclerViewLayoutManager);
        accountsRecyclerView.setAdapter(accountsRecyclerViewAdapter);

        accountsRecyclerViewAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(SELECTED_ACCOUNT_NAME, mAccounts.get(position).getNickName());
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });


    }
}
