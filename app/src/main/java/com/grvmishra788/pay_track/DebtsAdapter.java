package com.grvmishra788.pay_track;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Debt;
import com.grvmishra788.pay_track.DS.Debt;
import com.grvmishra788.pay_track.DS.Debt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class DebtsAdapter extends RecyclerView.Adapter<DebtsAdapter.DebtsViewHolder> {
    //constants
    private static final String TAG = "Pay-Track: " + DebtsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    //Variable for accessing Debts List in DebtsAdapter
    private ArrayList<Debt> mDebts;

    public DebtsAdapter(Context context, ArrayList<Debt> mDebts) {
        Log.d(TAG, TAG + ": Constructor starts");
        this.mContext = context;
        this.mDebts = mDebts;
        Log.d(TAG, TAG + ": Constructor ends");
    }

    @NonNull
    @Override
    public DebtsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_debt, parent, false);
        DebtsViewHolder debtsViewHolder = new DebtsViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return debtsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DebtsViewHolder holder, int position) {
        String amountValue = "", desc = "";

        Debt debt = mDebts.get(position);
        Log.d(TAG, "onBindViewHolder() :: " + position + " :: " + debt.toString());
        //set amount
        if(debt.getType()== GlobalConstants.DebtType.PAY){
            holder.title_person.setText(mContext.getString(R.string.lender));
            amountValue = " + " + String.valueOf(debt.getAmount());
            holder.amount.setTextColor(GREEN);
        } else {
            holder.title_person.setText(mContext.getString(R.string.borrower));
            amountValue = " - " + String.valueOf(debt.getAmount());
            holder.amount.setTextColor(RED);
        }
        holder.amount.setText(amountValue);

        //set date
        SimpleDateFormat sdf=new SimpleDateFormat(GlobalConstants.DATE_FORMAT_DAY_AND_DATE);
        String dateString = sdf.format(debt.getDate());
        holder.date.setText(dateString);

        //set person
        holder.person.setText(debt.getPerson());

        desc = debt.getDescription();
        if(InputValidationUtilities.isValidString(desc)){
            holder.ll_description.setVisibility(View.VISIBLE);
            holder.description.setText(desc);
        } else {
            holder.ll_description.setVisibility(View.GONE);
        }

        holder.account.setText(debt.getAccount());
    }

    @Override
    public int getItemCount() {
        if(mDebts==null)
            return 0;
        else
            return mDebts.size();
    }

    public class DebtsViewHolder extends RecyclerView.ViewHolder {

        //constants
        private final String S_TAG = "Pay-Track: " + DebtsViewHolder.class.getName(); //constant Class TAG

        private TextView amount, person, description, date, account, title_person;
        private LinearLayout ll_description;

        public DebtsViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i(S_TAG, S_TAG + ": Constructor starts");
            amount = itemView.findViewById(R.id.tv_show_amount);
            person = itemView.findViewById(R.id.tv_show_person);
            title_person = itemView.findViewById(R.id.tv_title_person);
            description = itemView.findViewById(R.id.tv_show_description);
            date = itemView.findViewById(R.id.tv_show_date);
            account = itemView.findViewById(R.id.tv_show_account);

            ll_description = itemView.findViewById(R.id.ll_show_description);
            Log.i(S_TAG, S_TAG + ": Constructor ends");
        }
    }
}
