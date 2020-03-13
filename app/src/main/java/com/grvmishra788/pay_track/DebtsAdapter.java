package com.grvmishra788.pay_track;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.grvmishra788.pay_track.DS.Debt;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DebtsAdapter extends RecyclerView.Adapter<DebtsAdapter.DebtsViewHolder> {
    public DebtsAdapter(Context context, ArrayList<Debt> mDebts) {
    }

    @NonNull
    @Override
    public DebtsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DebtsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DebtsViewHolder extends RecyclerView.ViewHolder {
        public DebtsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
