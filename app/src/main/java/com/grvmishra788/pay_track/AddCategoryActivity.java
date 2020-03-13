package com.grvmishra788.pay_track;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;

public class AddCategoryActivity extends AppCompatActivity {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddCategoryActivity.class.getName();

    private TextView tv_submit;
    private EditText et_categoryName, et_parent, et_associatedAccount, et_description;
    private ImageButton ib_parent, ib_associatedAccount;

    private String categoryName, parent, associatedAccountNickName, description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        setTitle(R.string.title_add_category);

        initViews();
        initParentSelection();
        initAccountSelection();
        initSubmitBtn();

        Log.i(TAG, "onCreate() ends!");
    }

    private void initAccountSelection() {
        ib_associatedAccount = findViewById(R.id.ib_select_account);
        ib_associatedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectParentIntent = new Intent(getBaseContext(), SelectAccountActivity.class);
                startActivityForResult(selectParentIntent, REQ_CODE_SELECT_ACCOUNT);
            }
        });
    }

    private void initParentSelection() {
        ib_parent = findViewById(R.id.ib_select_parent);
        ib_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectParentIntent = new Intent(getBaseContext(), CategoryActivity.class);
                selectParentIntent.putExtra(GlobalConstants.IS_SELECT_ACCOUNT_INTENT, true);
                startActivityForResult(selectParentIntent, REQ_CODE_SELECT_PARENT_CATEGORY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_SELECT_PARENT_CATEGORY){
                String categoryParent = (String) data.getStringExtra(SELECTED_CATEGORY_NAME);
                et_parent.setText(categoryParent);
            } else if (requestCode == REQ_CODE_SELECT_ACCOUNT){
                String accountNickName = (String) data.getStringExtra(SELECTED_ACCOUNT_NAME);
                et_associatedAccount.setText(accountNickName);
            } else {
                Log.e(TAG, "Wrong request code - " + requestCode);
            }
        } else {
            Log.e(TAG, "Wrong result code - " + resultCode);
        }

    }

    private void initSubmitBtn() {
        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parseInput()) {
                    Intent resultIntent = new Intent();
                    if(!InputValidationUtilities.isValidString(parent)){
                        if(entryPresentInDB(CATEGORIES_TABLE, CATEGORIES_TABLE_COL_CATEGORY_NAME, categoryName)){
                            Toast.makeText(getBaseContext(),getString(R.string.error_account_entry_with_same_nickname), Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"Entry with  name - " + categoryName + " already present in category table");
                        } else {
                            //create category & set result in case parent is empty
                            Category category = new Category(categoryName, associatedAccountNickName, description);
                            resultIntent.putExtra(GlobalConstants.CATEGORY_OBJECT, category);
                        }
                    } else {
                        if(entryPresentInDB(SUB_CATEGORIES_TABLE, SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME, categoryName)){
                            Toast.makeText(getBaseContext(),getString(R.string.error_account_entry_with_same_nickname), Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"Entry with name - " + categoryName + " already present in sub_category table");
                        } else {
                            // else, create sub category & set result
                            SubCategory subCategory = new SubCategory(categoryName, associatedAccountNickName, description, parent);
                            resultIntent.putExtra(GlobalConstants.SUB_CATEGORY_OBJECT, subCategory);
                        }
                    }
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Log.d(TAG,"Input not parsed properly - Some field entry is wrong");
                }
            }
        });

    }

    private boolean entryPresentInDB(String tableName, String colName, String value) {
        DbHelper dbHelper = new DbHelper(this);
        return dbHelper.entryPresentInDB(tableName, colName, value);
    }

    private boolean parseInput() {
        categoryName = String.valueOf(et_categoryName.getText()).trim();
        parent = String.valueOf(et_parent.getText()).trim();
        associatedAccountNickName = String.valueOf(et_associatedAccount.getText()).trim();
        description = String.valueOf(et_description.getText()).trim();
        if(InputValidationUtilities.isValidString(categoryName)){
            if(InputValidationUtilities.isValidString(associatedAccountNickName)){
                return true;
            } else {
                Log.d(TAG,"Associated Account is null!");
                return false;
            }
        } else {
            Log.d(TAG,"category Name is null!");
            return false;
        }
    }

    private void initViews() {
        et_categoryName = findViewById(R.id.et_category_name);
        et_parent = findViewById(R.id.et_select_parent);
        et_associatedAccount = findViewById(R.id.et_select_account);
        et_description = findViewById(R.id.et_description);

        ib_parent = findViewById(R.id.ib_select_parent);
        ib_associatedAccount = findViewById(R.id.ib_select_account);

        //set onFocusChangeListener for editTexts
        et_categoryName.setOnFocusChangeListener(onFocusChangeListener);
        et_description.setOnFocusChangeListener(onFocusChangeListener);
    }

    private EditText.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            Log.d(TAG,"onFocusChange() - viewID: " + view.getId() +" hasFocus:" + hasFocus);
            if (hasFocus) {
                //make hint disappear on edit view focus
                ((EditText) view).setHint("");
            } else {
                String hint = "";
                switch (view.getId()) {
                    case R.id.et_category_name:
                        hint = getString(R.string.category_name);
                        break;
                    case R.id.et_description:
                        hint = getString(R.string.description);
                        break;
                    default:
                        hint = "";
                }
                ((EditText) view).setHint(hint);
            }
        }
    };


}
