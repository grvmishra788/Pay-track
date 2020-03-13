package com.grvmishra788.pay_track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;


import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.BULLET_SYMBOL;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_PARENT_CATEGORY;

public class AddCategoryActivity extends AppCompatActivity {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddCategoryActivity.class.getName();

    private TextView tv_submit;
    private EditText et_categoryName, et_parent, et_account, et_description;
    private ImageButton ib_parent, ib_account, ib_cancelParent, ib_cancelAccount;

    private String categoryName, parent, accountNickName, description;

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
        ib_account.setOnClickListener(addAccountClickListener);
        et_account.setOnClickListener(addAccountClickListener);

        ib_cancelAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear account field as well
                et_account.setText("");
                et_account.setHint(getString(R.string.default_account));
                ib_cancelAccount.setVisibility(View.INVISIBLE);
            }
        });

    }

    private View.OnClickListener addAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent selectParentIntent = new Intent(AddCategoryActivity.this, SelectAccountActivity.class);
            startActivityForResult(selectParentIntent, REQ_CODE_SELECT_ACCOUNT);
        }
    };

    private void initParentSelection() {
        ib_parent.setOnClickListener(addParentClickListener);
        et_parent.setOnClickListener(addParentClickListener);

        ib_cancelParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_parent.setText("");
                et_parent.setHint(getString(R.string.default_parent));
                ib_cancelParent.setVisibility(View.INVISIBLE);
            }
        });
    }

    private View.OnClickListener addParentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent selectParentIntent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
            selectParentIntent.putExtra(GlobalConstants.CATEGORY_INTENT_TYPE, SELECT_PARENT_CATEGORY);
            startActivityForResult(selectParentIntent, REQ_CODE_SELECT_PARENT_CATEGORY);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_SELECT_PARENT_CATEGORY){
                String categoryParent = data.getStringExtra(SELECTED_CATEGORY_NAME);
                String account = data.getStringExtra(SELECTED_CATEGORY_ACCOUNT_NAME);
                et_parent.setText(categoryParent);
                ib_cancelParent.setVisibility(View.VISIBLE);
                if(!InputValidationUtilities.isValidString(String.valueOf(et_account.getText()).trim())){
                    et_account.setText(account);
                    ib_cancelAccount.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == REQ_CODE_SELECT_ACCOUNT){
                String accountNickName = data.getStringExtra(SELECTED_ACCOUNT_NAME);
                et_account.setText(accountNickName);
                ib_cancelAccount.setVisibility(View.VISIBLE);
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
                    if(!InputValidationUtilities.isValidString(parent)){
                        if(Utilities.entryPresentInDB(AddCategoryActivity.this, CATEGORIES_TABLE, CATEGORIES_TABLE_COL_CATEGORY_NAME, categoryName)){
                            String error = "\n\n" + BULLET_SYMBOL + " " +  getString(R.string.category_name) + " : " + categoryName;
                            Utilities.showDuplicateFieldErrorDialog(AddCategoryActivity.this, error);
                            Log.d(TAG,"Entry with  name - " + categoryName + " already present in category table");
                        } else {
                            Intent resultIntent = new Intent();
                            //create category & set result in case parent is empty
                            Category category = new Category(categoryName, accountNickName, description);
                            resultIntent.putExtra(GlobalConstants.CATEGORY_OBJECT, category);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    } else {
                        if(Utilities.entryPresentInDB(AddCategoryActivity.this, SUB_CATEGORIES_TABLE, SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME, categoryName)){
                            String error = "\n\n" + BULLET_SYMBOL + " " +  getString(R.string.category_name) + " : " + categoryName;
                            error += "\n" + BULLET_SYMBOL + " " + getString(R.string.parent) + " : " + parent;
                            Utilities.showDuplicateFieldErrorDialog(AddCategoryActivity.this, error);
                            Log.d(TAG,"Entry with name - " + categoryName + " already present in sub_category table");
                        } else {
                            Intent resultIntent = new Intent();
                            // else, create sub category & set result
                            SubCategory subCategory = new SubCategory(categoryName, accountNickName, description, parent);
                            resultIntent.putExtra(GlobalConstants.SUB_CATEGORY_OBJECT, subCategory);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }

                } else {
                    Log.d(TAG,"Input not parsed properly - Some field entry is wrong");
                }
            }
        });

    }

    private boolean parseInput() {
        categoryName = String.valueOf(et_categoryName.getText()).trim();
        parent = String.valueOf(et_parent.getText()).trim();
        accountNickName = String.valueOf(et_account.getText()).trim();
        description = String.valueOf(et_description.getText()).trim();
        HashMap<String, Boolean> validInputs = new HashMap<>();

        validInputs.put(getString(R.string.category_name), InputValidationUtilities.isValidString(categoryName));
        validInputs.put(getString(R.string.account), InputValidationUtilities.isValidString(accountNickName));

        String emptyFields = Utilities.checkHashMapForFalseValues(validInputs);

        if(InputValidationUtilities.isValidString(emptyFields)){
            Utilities.showEmptyFieldsErrorDialog(this, emptyFields);
            return false;
        } else {
            return true;
        }
    }

    private void initViews() {
        et_categoryName = findViewById(R.id.et_category_name);
        et_parent = findViewById(R.id.et_select_parent);
        et_account = findViewById(R.id.et_select_account);
        et_description = findViewById(R.id.et_description);

        ib_parent = findViewById(R.id.ib_select_parent);
        ib_cancelParent = findViewById(R.id.ib_cancel_parent);

        ib_account = findViewById(R.id.ib_select_account);
        ib_cancelAccount = findViewById(R.id.ib_cancel_account);

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
