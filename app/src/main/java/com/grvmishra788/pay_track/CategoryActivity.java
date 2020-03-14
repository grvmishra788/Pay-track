package com.grvmishra788.pay_track;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.grvmishra788.pay_track.GlobalConstants.CATEGORY_INTENT_TYPE;
import static com.grvmishra788.pay_track.GlobalConstants.CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.NEW_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.NEW_SUB_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.OLD_SUB_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.POSITION_ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_EDIT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_PARENT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SHOW_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_ITEM_TO_EDIT;

public class CategoryActivity extends AppCompatActivity {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + CategoryActivity.class.getName();

    private FloatingActionButton addCategoryButton;

    //boolean to store activity type
    private int categoryActivityType;

    //Categories list
    private ArrayList<Category> mCategories;

    //recyclerView variables
    private RecyclerView categoriesRecyclerView;
    private CategoriesAdapter categoriesRecyclerViewAdapter;
    private RecyclerView.LayoutManager categoriesRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;

    @SuppressLint("RestrictedApi")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //get categoryActivityType from Starting intent
        Intent startingIntent = getIntent();
        if(startingIntent.hasExtra(CATEGORY_INTENT_TYPE)){
            categoryActivityType = startingIntent.getIntExtra(CATEGORY_INTENT_TYPE, SHOW_CATEGORY);
        } else {
            categoryActivityType = SHOW_CATEGORY;
        }

        setTitleAsPerActivityType();

        //init db
        payTrackDBHelper = new DbHelper(this);

        //init accounts list
        mCategories = payTrackDBHelper.getAllCategories();
        if(mCategories == null){
            mCategories = new ArrayList<>();
        }

        //init RecyclerView
        categoriesRecyclerView = (RecyclerView) findViewById(R.id.show_category_recycler_view);
        categoriesRecyclerView.setHasFixedSize(true);    //hasFixedSize=true increases app performance as Recyclerview is not going to change in size
        categoriesRecyclerViewLayoutManager = new LinearLayoutManager(this);
        categoriesRecyclerViewAdapter = new CategoriesAdapter(this, mCategories, categoryActivityType);
        //TODO : set observer to check if data is empty
//        categoriesRecyclerViewAdapter.registerAdapterDataObserver(observer); //register data observer for recyclerView
        categoriesRecyclerView.setLayoutManager(categoriesRecyclerViewLayoutManager);
        categoriesRecyclerView.setAdapter(categoriesRecyclerViewAdapter);

        //init FAB
        addCategoryButton = findViewById(R.id.addItemFAB);
        if(categoryActivityType==SELECT_CATEGORY){
            addCategoryButton.setVisibility(View.GONE);
            categoriesRecyclerViewAdapter.setmOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(SELECTED_CATEGORY_NAME, mCategories.get(position).getCategoryName());
                    resultIntent.putExtra(SELECTED_CATEGORY_ACCOUNT_NAME, mCategories.get(position).getAccountNickName());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                @Override
                public void onItemLongClick(int position) {

                }
            });

            categoriesRecyclerViewAdapter.setSubCategoryClickListener(new OnSubCategoryClickListener() {
                @Override
                public void onItemClick(int position, SubCategory subCategory) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(SELECTED_SUB_CATEGORY_NAME, subCategory.getSubCategoryName());
                    resultIntent.putExtra(SELECTED_SUB_CATEGORY_PARENT_NAME, subCategory.getParent());
                    resultIntent.putExtra(SELECTED_SUB_CATEGORY_ACCOUNT_NAME, subCategory.getAccountNickName());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                @Override
                public void onItemLongClick(int position, SubCategory subCategory) {

                }
            });

        } else if(categoryActivityType==SELECT_PARENT_CATEGORY){
            addCategoryButton.setVisibility(View.GONE);
            categoriesRecyclerViewAdapter.setmOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(SELECTED_CATEGORY_NAME, mCategories.get(position).getCategoryName());
                    resultIntent.putExtra(SELECTED_CATEGORY_ACCOUNT_NAME, mCategories.get(position).getAccountNickName());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                @Override
                public void onItemLongClick(int position) {

                }
            });
        } else {

            addCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "addCategoryButton::onClick()");
                    Intent addCategoryIntent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                    startActivityForResult(addCategoryIntent, REQ_CODE_ADD_CATEGORY);
                }
            });

            categoriesRecyclerViewAdapter.setmOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    Intent editActivityIntent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                    editActivityIntent.putExtra(ITEM_TO_EDIT, mCategories.get(position));
                    editActivityIntent.putExtra(POSITION_ITEM_TO_EDIT, position);
                    startActivityForResult(editActivityIntent, REQ_CODE_EDIT_CATEGORY);

                }
                @Override
                public void onItemLongClick(int position) {

                }
            });

            categoriesRecyclerViewAdapter.setSubCategoryClickListener(new OnSubCategoryClickListener() {
                @Override
                public void onItemClick(int position, SubCategory subCategory) {
                    Intent editActivityIntent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                    editActivityIntent.putExtra(SUB_ITEM_TO_EDIT, subCategory);
                    startActivityForResult(editActivityIntent, REQ_CODE_EDIT_CATEGORY);
                }
                @Override
                public void onItemLongClick(int position, SubCategory subCategory) {

                }
            });
        }
        Log.i(TAG, "onCreate() ends!");
    }

    private void setTitleAsPerActivityType() {
        if(categoryActivityType==SELECT_PARENT_CATEGORY){
            setTitle(R.string.title_select_parent_categories);
        } else if (categoryActivityType==SELECT_CATEGORY){
            setTitle(R.string.title_select_categories);
        } else {
            setTitle(R.string.title_categories);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if(resultCode==RESULT_OK){
            if(requestCode==REQ_CODE_ADD_CATEGORY){     //Add category activity result

                Log.i(TAG, "Processing add category...");
                if(data.hasExtra(GlobalConstants.CATEGORY_OBJECT)){     //received category object

                    Category category = (Category) data.getSerializableExtra(GlobalConstants.CATEGORY_OBJECT);
                    mCategories.add(category);
                    if(payTrackDBHelper.insertDataToCategoriesTable(category)){
                        Log.d(TAG,"Category inserted to db - " + category.toString());
                    } else {
                        Log.e(TAG,"Couldn't insert category to db - " + category.toString());
                    }

                } else if (data.hasExtra(GlobalConstants.SUB_CATEGORY_OBJECT)){     //received sub-category object

                    SubCategory subCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.SUB_CATEGORY_OBJECT);
                    addSubCategoryToItsParent(subCategory);
                    if(payTrackDBHelper.insertDataToSubCategoriesTable(subCategory)){
                        Log.d(TAG,"Sub-Category inserted to db - " + subCategory.toString());
                    } else {
                        Log.e(TAG,"Couldn't insert sub-category to db - " + subCategory.toString());
                    }

                }
                categoriesRecyclerViewAdapter.notifyDataSetChanged();

            }
            else if(requestCode==REQ_CODE_EDIT_CATEGORY){       //edit category activity result

                if(data.hasExtra(POSITION_ITEM_TO_EDIT)){       //Category sent initially

                    int position = data.getIntExtra(POSITION_ITEM_TO_EDIT, -1);

                    if(data.hasExtra(CATEGORY_OBJECT)){         //category to category edit

                        Category oldCategory = mCategories.get(position);
                        Category newCategory = (Category) data.getSerializableExtra(GlobalConstants.CATEGORY_OBJECT);
                        if (payTrackDBHelper.updateDataInCategoriesTable(oldCategory, newCategory)) {
                            Log.d(TAG, "Category updated in db - FROM : " + oldCategory.toString() + " TO : " + newCategory.toString());
                        } else {
                            Log.e(TAG, "Couldn't update category to db - " + oldCategory.toString());
                        }
                        updateCategoryOnCascade(position, oldCategory, newCategory);

                    } else if (data.hasExtra(SUB_CATEGORY_OBJECT)){     //category to sub-category edit

                        //remove category
                        Category category = mCategories.get(position);
                        if (payTrackDBHelper.deleteDataInCategoriesTable(category)) {
                            Log.d(TAG, "Category deleted from db : " + category.toString() );
                        } else {
                            Log.e(TAG, "Couldn't delete category from db : " + category.toString());
                        }
                        mCategories.remove(position);
                        //add sub-category
                        SubCategory subCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.SUB_CATEGORY_OBJECT);
                        addSubCategoryToItsParent(subCategory);
                        if(payTrackDBHelper.insertDataToSubCategoriesTable(subCategory)){
                            Log.d(TAG,"Sub-Category inserted to db - " + subCategory.toString());
                        } else {
                            Log.e(TAG,"Couldn't insert sub-category to db - " + subCategory.toString());
                        }

                    }

                } else {   //sub-category sent initially

                    if(data.hasExtra(OLD_SUB_CATEGORY_OBJECT) && data.hasExtra(NEW_SUB_CATEGORY_OBJECT)){   //sub-category to sub-category

                        SubCategory oldSubCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.OLD_SUB_CATEGORY_OBJECT);
                        SubCategory newSubCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.NEW_SUB_CATEGORY_OBJECT);
                        if (payTrackDBHelper.updateDataInSubCategoriesTable(oldSubCategory, newSubCategory)) {
                            Log.d(TAG, "SubCategory updated in db - FROM : " + oldSubCategory.toString() + " TO : " + newSubCategory.toString());
                        } else {
                            Log.e(TAG, "Couldn't update sub-category to db - " + oldSubCategory.toString());
                        }
                        removeSubCategoryFromItsParent(oldSubCategory);
                        addSubCategoryToItsParent(newSubCategory);

                    } else if(data.hasExtra(OLD_SUB_CATEGORY_OBJECT) && data.hasExtra(NEW_CATEGORY_OBJECT)){      //sub-category to category edit

                        SubCategory oldSubCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.OLD_SUB_CATEGORY_OBJECT);
                        Category newCategory = (Category) data.getSerializableExtra(NEW_CATEGORY_OBJECT);
                        if (payTrackDBHelper.deleteDataInSubCategoriesTable(oldSubCategory)) {
                            Log.d(TAG, "SubCategory deleted from db : " + oldSubCategory.toString() );
                        } else {
                            Log.e(TAG, "Couldn't delete sub-category from db : " + oldSubCategory.toString());
                        }
                        removeSubCategoryFromItsParent(oldSubCategory);
                        mCategories.add(newCategory);
                        if(payTrackDBHelper.insertDataToCategoriesTable(newCategory)){
                            Log.d(TAG,"Category inserted to db - " + newCategory.toString());
                        } else {
                            Log.e(TAG,"Couldn't insert category to db - " + newCategory.toString());
                        }
                        categoriesRecyclerViewAdapter.notifyDataSetChanged();

                    }

                }

            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }

    }

    private void updateCategoryOnCascade(int position, Category oldCategory, Category newCategory) {
        String newCategoryName = newCategory.getCategoryName();
        ArrayList<SubCategory> subCategories = oldCategory.getSubCategories();
        if(subCategories!=null){
            for (SubCategory subCategory: subCategories) {
                subCategory.setParent(newCategoryName);
            }
            newCategory.setSubCategories(subCategories);
        }
        mCategories.set(position, newCategory);
        categoriesRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addSubCategoryToItsParent(SubCategory subCategory) {
        String parent = subCategory.getParent();
        for(Category category:mCategories){
            if(category.getCategoryName().equals(parent)){
                category.addSubCategory(subCategory);
                break;
            }
        }
        categoriesRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void removeSubCategoryFromItsParent(SubCategory subCategory) {
        String parent = subCategory.getParent();
        for(Category category:mCategories){
            if(category.getCategoryName().equals(parent)){
                category.removeSubCategory(subCategory);
                break;
            }
        }
        categoriesRecyclerViewAdapter.notifyDataSetChanged();
    }
}
