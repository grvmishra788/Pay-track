package com.grvmishra788.pay_track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;


import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE;
import static com.grvmishra788.pay_track.BackEnd.DatabaseConstants.SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.BULLET_SYMBOL;
import static com.grvmishra788.pay_track.GlobalConstants.ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.POSITION_ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_ACCOUNT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_ITEM_TO_EDIT;

public class AddCategoryActivity extends AppCompatActivity {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + AddCategoryActivity.class.getName();

    private TextView tv_submit;
    private EditText et_categoryName, et_parent, et_account, et_description;
    private ImageButton ib_parent, ib_account, ib_cancelParent, ib_cancelAccount;

    private String categoryName, parent, accountNickName, description;

    private Category categoryToEdit;
    private int positionCategoryToEdit;

    private SubCategory subCategoryToEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        initViews();
        initParentSelection();
        initAccountSelection();
        initSubmitBtn();

        Intent activityStartingIntent = getIntent();
        if(activityStartingIntent.hasExtra(ITEM_TO_EDIT)){
            setTitle(R.string.title_edit_category);
            categoryToEdit = (Category) activityStartingIntent.getSerializableExtra(ITEM_TO_EDIT);
            positionCategoryToEdit = activityStartingIntent.getIntExtra(POSITION_ITEM_TO_EDIT, -1);
            et_categoryName.setText(categoryToEdit.getCategoryName());
            et_account.setText(categoryToEdit.getAccountNickName());
            et_description.setText(categoryToEdit.getDescription());

            ib_cancelAccount.setVisibility(View.VISIBLE);
        } else if(activityStartingIntent.hasExtra(SUB_ITEM_TO_EDIT)){
            setTitle(R.string.title_edit_category);
            subCategoryToEdit = (SubCategory) activityStartingIntent.getSerializableExtra(SUB_ITEM_TO_EDIT);

            et_categoryName.setText(subCategoryToEdit.getSubCategoryName());
            et_parent.setText(subCategoryToEdit.getParent());
            et_account.setText(subCategoryToEdit.getAccountNickName());
            et_description.setText(subCategoryToEdit.getDescription());

            ib_cancelParent.setVisibility(View.VISIBLE);
            ib_cancelAccount.setVisibility(View.VISIBLE);
        }
        else {
            setTitle(R.string.title_add_category);
        }

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
                if (parseInput()) { //input parsed properly

                    if (!InputValidationUtilities.isValidString(parent)) {    //Dealing with Category

                        if (categoryToEdit == null && subCategoryToEdit == null) {   // Add activity

                            boolean isPresentInDb = Utilities.entryPresentInDB(AddCategoryActivity.this, CATEGORIES_TABLE, CATEGORIES_TABLE_COL_CATEGORY_NAME, categoryName);
                            if (isPresentInDb) {      //duplicate entry

                                //show alert
                                String error = "\n\n" + BULLET_SYMBOL + " " + getString(R.string.category_name) + " : " + categoryName;
                                Utilities.showDuplicateFieldErrorDialog(AddCategoryActivity.this, error);
                                Log.d(TAG, "Entry with  name - " + categoryName + " already present in category table");

                            } else {    //not duplicate

                                // complete add category activity
                                Intent resultIntent = new Intent();
                                Category category = new Category(categoryName, accountNickName, description);
                                resultIntent.putExtra(GlobalConstants.CATEGORY_OBJECT, category);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            }

                        } else {    //Edit Activity

                            if (categoryToEdit != null) {   //Editing category to category

                                Intent resultIntent = new Intent();
                                categoryToEdit.setCategoryName(categoryName);
                                categoryToEdit.setAccountNickName(accountNickName);
                                categoryToEdit.setDescription(description);
                                resultIntent.putExtra(POSITION_ITEM_TO_EDIT, positionCategoryToEdit);
                                resultIntent.putExtra(GlobalConstants.CATEGORY_OBJECT, categoryToEdit);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            } else {    //Editing Sub-Category to category

                                Intent resultIntent = new Intent();
                                Category category = new Category(categoryName, accountNickName, description);
                                resultIntent.putExtra(GlobalConstants.OLD_SUB_CATEGORY_OBJECT, subCategoryToEdit);
                                resultIntent.putExtra(GlobalConstants.NEW_CATEGORY_OBJECT, category);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            }

                        }
                    } else {    //Dealing with sub-category

                        if (categoryToEdit == null && subCategoryToEdit == null) {   // Add activity

                            boolean isPresentInDb = Utilities.entryPresentInDB(AddCategoryActivity.this, SUB_CATEGORIES_TABLE, SUB_CATEGORIES_TABLE_COL_CATEGORY_NAME, categoryName);
                            if (isPresentInDb) {      //duplicate entry

                                //show error
                                String error = "\n" + BULLET_SYMBOL + " " + getString(R.string.category_name) + " : " + categoryName;
                                Utilities.showDuplicateFieldErrorDialog(AddCategoryActivity.this, error);
                                Log.d(TAG, "Entry with name - " + categoryName + " already present in sub_category table");

                            } else {    //not duplicate

                                //complete add sub-category activity
                                Intent resultIntent = new Intent();
                                SubCategory subCategory = new SubCategory(categoryName, accountNickName, description, parent);
                                resultIntent.putExtra(GlobalConstants.SUB_CATEGORY_OBJECT, subCategory);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            }
                        } else {    //Edit Activity

                            if(categoryToEdit!=null){   //Editing Category to sub-category

                                //category to sub-category edit
                                ArrayList<SubCategory> subCategories = categoryToEdit.getSubCategories();
                                if(subCategories==null){    //original category doesn't have sub-category

                                    Intent resultIntent = new Intent();
                                    SubCategory subCategory = new SubCategory(categoryName, accountNickName, description, parent);
                                    resultIntent.putExtra(POSITION_ITEM_TO_EDIT, positionCategoryToEdit);   //with position one can get old category object
                                    resultIntent.putExtra(GlobalConstants.SUB_CATEGORY_OBJECT, subCategory);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();

                                } else {    //original category has sub-category

                                    Utilities.showSimpleErrorDialog(AddCategoryActivity.this, getString(R.string.error_cast_category_to_sub_category));

                                }

                            } else {        ////Editing sub-category to sub-category

                                Intent resultIntent = new Intent();
                                SubCategory newSubCategory =  subCategoryToEdit.copy();
                                newSubCategory.setSubCategoryName(categoryName);
                                newSubCategory.setParent(parent);
                                newSubCategory.setAccountNickName(accountNickName);
                                newSubCategory.setDescription(description);
                                resultIntent.putExtra(GlobalConstants.OLD_SUB_CATEGORY_OBJECT, subCategoryToEdit);
                                resultIntent.putExtra(GlobalConstants.NEW_SUB_CATEGORY_OBJECT, newSubCategory);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            }

                        }
                    }
                } else {    //input not parsed properly
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
            if(!InputValidationUtilities.isValidCategory(categoryName)){
                Utilities.showErrorFromKey(AddCategoryActivity.this, getString(R.string.category_name));
                return false;
            } else if(!InputValidationUtilities.isCategoryDiffFromParent(categoryName, parent)) {
                String msg = getString(R.string.category_name) + " can not be same as " + getString(R.string.parent);
                Utilities.showSimpleErrorDialog(AddCategoryActivity.this, msg);
                return false;
            } else {
                return true;
            }
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
