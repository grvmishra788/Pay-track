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
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SHOW_CATEGORY;

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
                    Intent addCategoryIntent = new Intent(getBaseContext(), AddCategoryActivity.class);
                    startActivityForResult(addCategoryIntent, REQ_CODE_ADD_CATEGORY);
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
            if(requestCode==REQ_CODE_ADD_CATEGORY){
                Log.i(TAG, "Processing...");
                if(data.hasExtra(GlobalConstants.CATEGORY_OBJECT)){
                    Category category = (Category) data.getSerializableExtra(GlobalConstants.CATEGORY_OBJECT);
                    mCategories.add(category);
                    if(payTrackDBHelper.insertDataToCategoriesTable(category)){
                        Log.d(TAG,"Account inserted to db - " + category.toString());
                    } else {
                        Log.e(TAG,"Couldn't insert account to db - " + category.toString());
                    }
                } else if (data.hasExtra(GlobalConstants.SUB_CATEGORY_OBJECT)){
                    SubCategory subCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.SUB_CATEGORY_OBJECT);
                    addSubCategoryToItsParent(subCategory);
                    if(payTrackDBHelper.insertDataToSubCategoriesTable(subCategory)){
                        Log.d(TAG,"Account inserted to db - " + subCategory.toString());
                    } else {
                        Log.e(TAG,"Couldn't insert account to db - " + subCategory.toString());
                    }

                }
                categoriesRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }

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
}
