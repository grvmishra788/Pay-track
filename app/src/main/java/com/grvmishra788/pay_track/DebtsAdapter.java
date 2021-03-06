package com.grvmishra788.pay_track;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Debt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.grvmishra788.pay_track.GlobalConstants.DEFAULT_FORMAT_DAY_AND_DATE;

public class DebtsAdapter extends RecyclerView.Adapter<DebtsAdapter.DebtsViewHolder> {
    //constants
    private static final String TAG = "Pay-Track: " + DebtsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    //Variable for accessing Debts List in DebtsAdapter
    private ArrayList<Debt> mDebts;

    //Variable for onItemclickListener
    private OnItemClickListener onItemClickListener;

    //Variable to store selectedItem positions when launching Contextual action mode
    private TreeSet<Integer> selectedItems = new TreeSet<>();

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull DebtsViewHolder holder, int position) {
        String amountValue = "", desc = "";

        Debt debt = mDebts.get(position);
        Log.d(TAG, "onBindViewHolder() :: " + position + " :: " + debt.toString());
        //set amount
        if(debt.getType()== GlobalConstants.DebtType.PAY){
            holder.title_person.setText(mContext.getString(R.string.lender));
            amountValue = Utilities.getAmountWithRupeeSymbol(debt.getAmount());
            holder.amount.setTextColor(GREEN);
        } else {
            holder.title_person.setText(mContext.getString(R.string.borrower));
            amountValue = Utilities.getAmountWithRupeeSymbol(debt.getAmount());
            holder.amount.setTextColor(RED);
        }
        holder.amount.setText(amountValue);

        //set date
        SimpleDateFormat sdf=new SimpleDateFormat(PreferenceUtils.getDefaultDateFormat(mContext));
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

        if (selectedItems.contains(position)) {
            //if item is selected then,set foreground color of root view.
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccentTransparent)));
        } else {
            //else remove selected item color.
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        }
    }

    @Override
    public int getItemCount() {
        if(mDebts==null)
            return 0;
        else
            return mDebts.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TreeSet<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(TreeSet<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public class DebtsViewHolder extends RecyclerView.ViewHolder {

        //constants
        private final String S_TAG = "Pay-Track: " + DebtsViewHolder.class.getName(); //constant Class TAG

        private TextView amount, person, description, date, account, title_person;
        private LinearLayout ll_description;

        public DebtsViewHolder(@NonNull View itemView) {
            super(itemView);
            //perform necessary ops if current item is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
            //perform necessary ops if current item is long clicked
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemLongClick(position);
                    }
                    return true;
                }
            });

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
