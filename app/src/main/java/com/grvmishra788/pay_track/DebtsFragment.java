package com.grvmishra788.pay_track;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.Debt;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_DEBT;

public class DebtsFragment extends Fragment {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + DebtsFragment.class.getName();

    private FloatingActionButton addDebtButton;

    //Debts list
    private ArrayList<Debt> mDebts;

    //recyclerView variables
    private RecyclerView debtsRecyclerView;
    private DebtsAdapter debtsRecyclerViewAdapter;
    private RecyclerView.LayoutManager debtsRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_debts_fragment, container, false);

        //init db
        payTrackDBHelper = new DbHelper(getContext());

        //init transasctions list
//        mDebts = payTrackDBHelper.getAllDebts();

        if(mDebts == null){
            Log.d(TAG, "mDebts is null");
            mDebts = new ArrayList<>();
        }



        //init RecyclerView
        debtsRecyclerView = (RecyclerView) view.findViewById(R.id.show_debt_recycler_view);
        debtsRecyclerView.setHasFixedSize(true);
        debtsRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        debtsRecyclerViewAdapter = new DebtsAdapter(getContext(), mDebts);
        debtsRecyclerView.setLayoutManager(debtsRecyclerViewLayoutManager);
        debtsRecyclerView.setAdapter(debtsRecyclerViewAdapter);


        addDebtButton = view.findViewById(R.id.addItemFAB);
        addDebtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addDebtButton::onClick()");
                Intent addDebtIntent = new Intent(getActivity(), AddDebtActivity.class);
                startActivityForResult(addDebtIntent, REQ_CODE_ADD_DEBT);
            }
        });
        
        return view;
    }
}
