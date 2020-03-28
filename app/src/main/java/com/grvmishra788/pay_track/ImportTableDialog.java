package com.grvmishra788.pay_track;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        switch (tableName){
            case DatabaseConstants.ACCOUNTS_TABLE:
                tableString = "Accounts";
                break;
            case DatabaseConstants.DEBTS_TABLE:
                tableString = "Debts";
                break;
            case DatabaseConstants.CATEGORIES_TABLE:
                tableString = "Categories";
                break;
            case DatabaseConstants.TRANSACTIONS_TABLE:
                tableString = "Transactions";
                break;
        }
        LayoutInflater inflater = ((AppCompatActivity)mContext).getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_import_table_dialog, null);

        final LinearLayout ll_guidelines = alertLayout.findViewById(R.id.ll_guidelines);
        final TextView tv_guideline = alertLayout.findViewById(R.id.tv_guideline);
        final ImageButton ib_guideline = alertLayout.findViewById(R.id.ib_guideline);
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

        setTitle("Import " + tableString+ " : ")
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
                        listener.OnSelectedFile(fileName);
                        Log.i(TAG, "Finally selected: " + fileName);
                    }
                });

    }

    public DialogListener getListener() {
        return listener;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }
}
