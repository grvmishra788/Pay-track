package com.grvmishra788.pay_track;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.BankAccount;
import com.grvmishra788.pay_track.DS.CashAccount;
import com.grvmishra788.pay_track.DS.DigitalAccount;

import java.util.ArrayList;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>{

    //constants
    private static final String TAG = "Pay-Track: " + AccountsAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    //Variable for accessing Accounts List in AccountsAdapter
    private ArrayList<CashAccount> mAccounts;

    //Variable for onItemclickListener
    private OnItemClickListener mOnItemClickListener;

    //Variable to store selectedItem positions when launching Contextual action mode
    private TreeSet<Integer> selectedItems = new TreeSet<>();

    //Constructor: binds CashAccount object data to AccountsAdapter
    public AccountsAdapter(Context mContext, ArrayList<CashAccount> mAccounts) {
        Log.d(TAG, TAG + ": Constructor starts");
        this.mContext = mContext;
        this.mAccounts = mAccounts;
        Log.d(TAG, TAG + ": Constructor ends");
    }


    @NonNull
    @Override
    public AccountsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_account, parent, false);
        AccountsViewHolder accountsViewHolder = new AccountsViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return accountsViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull AccountsViewHolder accountsViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder() :: " + position + "-th account.");
        CashAccount account = mAccounts.get(position);
        Double balance = account.getAccountBalance();
        if(account instanceof BankAccount){
            //hide unwanted views
            accountsViewHolder.ll_serviceName.setVisibility(View.GONE);
            //unhide wanted views
            accountsViewHolder.ll_accountNumber.setVisibility(View.VISIBLE);
            accountsViewHolder.ll_bankName.setVisibility(View.VISIBLE);
            accountsViewHolder.ll_email.setVisibility(View.VISIBLE);
            accountsViewHolder.ll_mobile.setVisibility(View.VISIBLE);
            //set texts
            accountsViewHolder.tv_nickName.setText(account.getNickName());
            accountsViewHolder.tv_bankName.setText(((BankAccount) account).getBankName());
            accountsViewHolder.tv_accountNumber.setText(((BankAccount) account).getAccountNumber());
            accountsViewHolder.tv_email.setText(((BankAccount) account).getEmail());
            accountsViewHolder.tv_mobile.setText(((BankAccount) account).getMobileNumber());
            accountsViewHolder.tv_balance.setText(Utilities.getAmountWithRupeeSymbol(balance));
        } else if (account instanceof DigitalAccount){
            //hide unwanted views
            accountsViewHolder.ll_bankName.setVisibility(View.GONE);
            accountsViewHolder.ll_accountNumber.setVisibility(View.GONE);
            //unhide wanted views
            accountsViewHolder.ll_serviceName.setVisibility(View.VISIBLE);
            accountsViewHolder.ll_email.setVisibility(View.VISIBLE);
            accountsViewHolder.ll_mobile.setVisibility(View.VISIBLE);
            //set texts
            accountsViewHolder.tv_nickName.setText(account.getNickName());
            accountsViewHolder.tv_serviceName.setText(((DigitalAccount) account).getServiceName());
            accountsViewHolder.tv_email.setText(((DigitalAccount) account).getEmail());
            accountsViewHolder.tv_mobile.setText(((DigitalAccount) account).getMobileNumber());
            accountsViewHolder.tv_balance.setText(Utilities.getAmountWithRupeeSymbol(balance));
        } else {
            //hide unwanted views
            accountsViewHolder.ll_accountNumber.setVisibility(View.GONE);
            accountsViewHolder.ll_bankName.setVisibility(View.GONE);
            accountsViewHolder.ll_serviceName.setVisibility(View.GONE);
            accountsViewHolder.ll_email.setVisibility(View.GONE);
            accountsViewHolder.ll_mobile.setVisibility(View.GONE);
            //set texts
            accountsViewHolder.tv_nickName.setText(account.getNickName());
            accountsViewHolder.tv_balance.setText(Utilities.getAmountWithRupeeSymbol(balance));
        }

        if (selectedItems.contains(position)) {
            //if item is selected then,set foreground color of FrameLayout.
            accountsViewHolder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccentTransparent)));
        } else {
            //else remove selected item color.
            accountsViewHolder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        }

    }

    @Override
    public int getItemCount() {
        if(mAccounts!=null)
           return mAccounts.size();
        else
            return 0;
    }

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //method to update selected items
    public void setSelectedItems(TreeSet<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public class AccountsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_nickName, tv_bankName, tv_serviceName, tv_accountNumber, tv_email, tv_mobile, tv_balance;
        private LinearLayout ll_nickName, ll_bankName, ll_serviceName, ll_accountNumber, ll_email, ll_mobile, ll_balance;

        public AccountsViewHolder(@NonNull View itemView) {
            super(itemView);
            //perform necessary ops if current item is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mOnItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
            //perform necessary ops if current item is long clicked
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (mOnItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onItemLongClick(position);
                    }
                    return true;
                }
            });

            tv_nickName = itemView.findViewById(R.id.tv_show_nick_name);
            tv_bankName = itemView.findViewById(R.id.tv_show_bank_name);
            tv_serviceName = itemView.findViewById(R.id.tv_show_service_name);
            tv_accountNumber = itemView.findViewById(R.id.tv_show_account_number);
            tv_email = itemView.findViewById(R.id.tv_show_email);
            tv_mobile = itemView.findViewById(R.id.tv_show_mobile);
            tv_balance = itemView.findViewById(R.id.tv_show_balance);

            ll_nickName = itemView.findViewById(R.id.ll_show_nick_name);
            ll_bankName = itemView.findViewById(R.id.ll_show_bank_name);
            ll_serviceName = itemView.findViewById(R.id.ll_show_service_name);
            ll_accountNumber = itemView.findViewById(R.id.ll_show_account_number);
            ll_email = itemView.findViewById(R.id.ll_show_email);
            ll_mobile = itemView.findViewById(R.id.ll_show_mobile);
            ll_balance = itemView.findViewById(R.id.ll_show_balance);

            //set onClickListener for item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mOnItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });

        }
    }


}
