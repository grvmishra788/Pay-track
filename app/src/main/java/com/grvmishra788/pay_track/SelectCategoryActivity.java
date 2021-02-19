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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import static com.grvmishra788.pay_track.GlobalConstants.CATEGORY_INTENT_TYPE;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_ACCOUNT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECTED_SUB_CATEGORY_PARENT_NAME;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SHOW_CATEGORY;

public class SelectCategoryActivity extends AppCompatActivity {

    //constant Class TAG
    private static final String TAG = "Pay-Track: " + SelectCategoryActivity.class.getName();

    private FloatingActionButton addCategoryButton;

    //boolean to store activity type
    private int categoryActivityType;

    //Categories list
    private SortedList<Category> mCategories;

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
        if (startingIntent.hasExtra(CATEGORY_INTENT_TYPE)) {
            categoryActivityType = startingIntent.getIntExtra(CATEGORY_INTENT_TYPE, SHOW_CATEGORY);
        } else {
            categoryActivityType = SHOW_CATEGORY;
        }

        setTitleAsPerActivityType();

        //init db
        payTrackDBHelper = new DbHelper(this);

        //init categories list
        initCategoriesList();

        //init RecyclerView
        categoriesRecyclerView = (RecyclerView) findViewById(R.id.show_category_recycler_view);
        categoriesRecyclerView.setHasFixedSize(true);    //hasFixedSize=true increases app performance as Recyclerview is not going to change in size
        categoriesRecyclerViewLayoutManager = new LinearLayoutManager(this);
        categoriesRecyclerViewAdapter = new CategoriesAdapter(this, mCategories, categoryActivityType);
        //TODO : set observer to check if data is empty
//        categoriesRecyclerViewAdapter.registerAdapterDataObserver(observer); //register data observer for recyclerView
        categoriesRecyclerView.setLayoutManager(categoriesRecyclerViewLayoutManager);
        categoriesRecyclerView.setAdapter(categoriesRecyclerViewAdapter);

        //hide FAB
        addCategoryButton = findViewById(R.id.addItemFAB);
        addCategoryButton.setVisibility(View.GONE);

        if (categoryActivityType == SELECT_CATEGORY) {
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

        } else if (categoryActivityType == SELECT_PARENT_CATEGORY) {
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
        }
        Log.i(TAG, "onCreate() ends!");
    }

    private void initCategoriesList() {
        mCategories = payTrackDBHelper.getAllCategories();
        if (mCategories == null) {
            mCategories = new SortedList<Category>(Category.class, new SortedList.Callback<Category>() {
                @Override
                public int compare(Category o1, Category o2) {
                    return o1.getCategoryName().toLowerCase().compareTo(o2.getCategoryName().toLowerCase());
                }

                @Override
                public void onChanged(int position, int count) {

                }

                @Override
                public boolean areContentsTheSame(Category oldItem, Category newItem) {
                    return false;
                }

                @Override
                public boolean areItemsTheSame(Category item1, Category item2) {
                    return false;
                }

                @Override
                public void onInserted(int position, int count) {

                }

                @Override
                public void onRemoved(int position, int count) {

                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {

                }
            });
        }
    }

    private void setTitleAsPerActivityType() {
        if (categoryActivityType == SELECT_PARENT_CATEGORY) {
            setTitle(R.string.title_select_parent_categories);
        } else {
            setTitle(R.string.title_select_categories);
        }
    }

}
