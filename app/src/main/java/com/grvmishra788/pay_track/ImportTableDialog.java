package com.grvmishra788.pay_track;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grvmishra788.pay_track.BackEnd.DatabaseConstants;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static com.grvmishra788.pay_track.GlobalConstants.FILE_EXTENSION_CSV;

public class ImportTableDialog extends AlertDialog.Builder {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + ImportTableDialog.class.getName();

    private DialogListener listener;
    private String tableName;
    private String tableString = "";

    public ImportTableDialog(final Context mContext, String tableName) {
        super(mContext);
        Log.i(TAG,"ImportTableDialog() - " + tableName);
        this.tableName = tableName;

        //init views
        LayoutInflater inflater = ((AppCompatActivity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_import_table_dialog, null);
        final LinearLayout ll_guidelines = alertLayout.findViewById(R.id.ll_guidelines);
        final TextView tv_guideline = alertLayout.findViewById(R.id.tv_guideline);
        final ImageButton ib_guideline = alertLayout.findViewById(R.id.ib_guideline);

        switch (tableName){
            case DatabaseConstants.ACCOUNTS_TABLE:
                tableString = "Accounts";
                ll_guidelines.addView(getGuildLineMainTV(mContext, R.string.import_rule_account_heading));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_nickName));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_type));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_bankName));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_serviceName));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_accountNo));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_email));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_mobile));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_account_balance));
                break;
            case DatabaseConstants.DEBTS_TABLE:
                tableString = "Debts";
                break;
            case DatabaseConstants.CATEGORIES_TABLE:
                tableString = "Categories";
                ll_guidelines.addView(getGuildLineMainTV(mContext, R.string.import_rule_category_heading));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_category_categoryName));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_category_accountNickName));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_category_description));
                break;
            case DatabaseConstants.SUB_CATEGORIES_TABLE:
                tableString = "Sub-Categories";
                ll_guidelines.addView(getGuildLineMainTV(mContext, R.string.import_rule_sub_category_heading));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_sub_category_categoryName));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_sub_category_accountNickName));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_sub_category_description));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_sub_category_parent));
                break;
            case DatabaseConstants.TRANSACTIONS_TABLE:
                tableString = "Transactions";
                ll_guidelines.addView(getGuildLineMainTV(mContext, R.string.import_rule_transaction_heading));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_transaction_amount));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_transaction_description));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_transaction_category));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_transaction_subCategory));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_transaction_type));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_transaction_date));
                ll_guidelines.addView(getGuildLineSubTV(mContext, R.string.import_rule_transaction_account));
                break;
        }

        ib_guideline.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                String str = (String) tv_guideline.getText();
                if(str.equals(mContext.getString(R.string.show_guidelines))){
                    tv_guideline.setText(mContext.getString(R.string.hide_guidelines));
                    ll_guidelines.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ib_guideline.setImageDrawable(mContext.getDrawable(R.drawable.ic_arrow_up));
                    }
                } else {
                    tv_guideline.setText(mContext.getString(R.string.show_guidelines));
                    ll_guidelines.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ib_guideline.setImageDrawable(mContext.getDrawable(R.drawable.ic_arrow_down));
                    }
                }
            }
        });

        final EditText loc = alertLayout.findViewById(R.id.et_location);
        final ImageButton ib_loc = alertLayout.findViewById(R.id.ib_select_location);
        ib_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFileDialog ofd = new OpenFileDialog(mContext);
                ofd.setOpenDialogListener(new DialogListener() {
                    @Override
                    public void OnSelectedFile(String fileName) {
                        loc.setText(fileName);
                    }
                });
                ofd.setFilter(FILE_EXTENSION_CSV);
                ofd.show();
            }
        });

        setTitle("Import " + tableString + " : ")
                .setView(alertLayout)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = String.valueOf(loc.getText());
                        if(InputValidationUtilities.isValidString(fileName)){
                            listener.OnSelectedFile(fileName);
                            Log.i(TAG, "Finally selected: " + fileName);
                        } else {
                            Toast.makeText(mContext, "No " + tableString + " spreadsheet Selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public DialogListener getListener() {
        return listener;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    private TextView getGuildLineMainTV(Context context, int resID){
        /*
        <TextView
			android:layout_width = "match_parent"
			android:layout_height = "wrap_content"
			android:layout_marginLeft = "@dimen/size_8_dp"
			android:layout_marginTop = "@dimen/size_10_dp"
			android:layout_marginRight = "@dimen/size_8_dp" />
         */

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginSideInDp = Utilities.getPxFromDip(context, 8);
        int marginTopInDp = Utilities.getPxFromDip(context, 10);
        params.setMargins(marginSideInDp, marginTopInDp, marginSideInDp, 0);
        tv.setLayoutParams(params);
        tv.setText(context.getText(resID));
        return tv;
    }

    private TextView getGuildLineSubTV(Context context, int resID){
        /*
        <TextView
			android:layout_width = "match_parent"
			android:layout_height = "wrap_content"
			android:layout_marginLeft = "@dimen/size_10_dp"
			android:layout_marginTop = "@dimen/size_4_dp"
			android:layout_marginRight = "@dimen/size_10_dp" />
         */

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginSideInDp = Utilities.getPxFromDip(context, 4);
        int marginTopInDp = Utilities.getPxFromDip(context, 10);
        params.setMargins(marginSideInDp, marginTopInDp, marginSideInDp, 0);
        tv.setLayoutParams(params);
        tv.setText(context.getText(resID));
        return tv;
    }
}
