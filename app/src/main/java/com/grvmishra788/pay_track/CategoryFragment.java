package com.grvmishra788.pay_track;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grvmishra788.pay_track.BackEnd.DbHelper;
import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import static android.app.Activity.RESULT_OK;
import static com.grvmishra788.pay_track.GlobalConstants.CATEGORY_INTENT_TYPE;
import static com.grvmishra788.pay_track.GlobalConstants.CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.NEW_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.NEW_SUB_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.OLD_SUB_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.POSITION_ITEM_TO_EDIT;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_ADD_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.REQ_CODE_EDIT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SHOW_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_CATEGORY_OBJECT;
import static com.grvmishra788.pay_track.GlobalConstants.SUB_ITEM_TO_EDIT;

public class CategoryFragment extends Fragment {
    //constant Class TAG
    private static final String TAG = "Pay-Track: " + CategoryFragment.class.getName();

    private FloatingActionButton addCategoryButton;

    //Categories list
    private SortedList<Category> mCategories;

    //recyclerView variables
    private RecyclerView categoriesRecyclerView;
    private CategoriesAdapter categoriesRecyclerViewAdapter;
    private RecyclerView.LayoutManager categoriesRecyclerViewLayoutManager;

    private DbHelper payTrackDBHelper;

    // fields to help keep track of app’s state for Contextual Action Mode
    private boolean isMultiSelect = false;
    private TreeSet<Integer> selectedItems = new TreeSet<>();
    private ActionMode actionMode;

    //Variable to store subcategories when launching Contextual action mode
    private TreeSet<SubCategory> selectedSubCategories = new TreeSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() starts...");
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_category, container, false);

        //init db
        payTrackDBHelper = new DbHelper(getContext());

        //init categories list
        initCategoriesList();

        //init RecyclerView
        categoriesRecyclerView = (RecyclerView) view.findViewById(R.id.show_category_recycler_view);
        categoriesRecyclerView.setHasFixedSize(true);    //hasFixedSize=true increases app performance as Recyclerview is not going to change in size
        categoriesRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        categoriesRecyclerViewAdapter = new CategoriesAdapter(getContext(), mCategories, SHOW_CATEGORY);
        //TODO : set observer to check if data is empty
//        categoriesRecyclerViewAdapter.registerAdapterDataObserver(observer); //register data observer for recyclerView
        categoriesRecyclerView.setLayoutManager(categoriesRecyclerViewLayoutManager);
        categoriesRecyclerView.setAdapter(categoriesRecyclerViewAdapter);

        //init FAB
        addCategoryButton = view.findViewById(R.id.addItemFAB);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "addCategoryButton::onClick()");
                Intent addCategoryIntent = new Intent(getActivity(), AddCategoryActivity.class);
                startActivityForResult(addCategoryIntent, REQ_CODE_ADD_CATEGORY);
            }
        });

        categoriesRecyclerViewAdapter.setmOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                if (isMultiSelect) {
                    //if multiple selection is enabled then select item on single click
                    selectMultiple(position);
                } else {
                    Intent editActivityIntent = new Intent(getActivity(), AddCategoryActivity.class);
                    editActivityIntent.putExtra(ITEM_TO_EDIT, mCategories.get(position));
                    editActivityIntent.putExtra(POSITION_ITEM_TO_EDIT, position);
                    startActivityForResult(editActivityIntent, REQ_CODE_EDIT_CATEGORY);
                }
            }

            @Override
            public void onItemLongClick(int position) {
                Log.d(TAG, "onItemLongClick called at position - " + position);
                if (!isMultiSelect) {
                    //init select items and isMultiSelect on long click
                    selectedItems = new TreeSet<>();
                    selectedSubCategories = new TreeSet<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        //show ActionMode on long click
                        actionMode = ((AppCompatActivity)getContext()).startSupportActionMode(actionModeCallbacks);
                    }
                    selectMultiple(position);
                }
            }
        });

        categoriesRecyclerViewAdapter.setSubCategoryClickListener(new OnSubCategoryClickListener() {
            @Override
            public void onItemClick(int position, SubCategory subCategory) {
                if (!isMultiSelect) {
                    Intent editActivityIntent = new Intent(getContext(), AddCategoryActivity.class);
                    editActivityIntent.putExtra(SUB_ITEM_TO_EDIT, subCategory);
                    startActivityForResult(editActivityIntent, REQ_CODE_EDIT_CATEGORY);
                } else {
                    selectMultiple(subCategory);
                }
            }

            @Override
            public void onItemLongClick(int position, SubCategory subCategory) {
                Log.d(TAG, "onItemLongClick called at position - " + position);
                if (!isMultiSelect) {
                    //init select items and isMultiSelect on long click
                    selectedItems = new TreeSet<>();
                    selectedSubCategories = new TreeSet<>();
                    isMultiSelect = true;
                    if (actionMode == null) {
                        //show ActionMode on long click
                        actionMode = ((AppCompatActivity)getContext()).startSupportActionMode(actionModeCallbacks);
                    }
                }
                selectMultiple(subCategory);
            }
        });

        return view;
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

    //function to multi-select Category once contextual action mode is launched
    private void selectMultiple(int position) {
        Log.d(TAG, "selectMultiple() called at position - " + String.valueOf(position));
        if (actionMode != null) {
            if (selectedItems.contains(position))
                selectedItems.remove(position);
            else {
                selectedItems.add(position);
                Category category = mCategories.get(position);
                ArrayList<SubCategory> subCategories = category.getSubCategories();
                if (subCategories != null) {
                    for (int i=0; i<subCategories.size(); i++) {
                        SubCategory subCategory = subCategories.get(i);
                        if (selectedSubCategories.contains(subCategory)) {
                            selectedSubCategories.remove(subCategory);
                        }
                    }
                }
            }
            if (selectedSubCategories.size() + selectedItems.size() > 0) {
                actionMode.setTitle(String.valueOf(selectedSubCategories.size() + selectedItems.size())); //show selected item count on action mode.
            } else {
                actionMode.setTitle(""); //remove item count from action mode.
                actionMode.finish(); //hide action mode.
            }
            categoriesRecyclerViewAdapter.setSelectedItems(selectedItems);

        }
    }

    //function to multi-select SubCategory contextual action mode is launched
    private void selectMultiple(SubCategory subCategory) {
        Log.d(TAG, "selectMultiple() called at sub-category - " + subCategory.toString());
        if (actionMode != null) {
            int parentPos = getPositionFromCategoryName(subCategory.getParent());
            if (!selectedItems.contains(parentPos)) {

                if (selectedSubCategories.contains(subCategory))
                    selectedSubCategories.remove(subCategory);
                else
                    selectedSubCategories.add(subCategory);

                if (selectedSubCategories.size() + selectedItems.size() > 0) {
                    actionMode.setTitle(String.valueOf(selectedSubCategories.size() + selectedItems.size())); //show selected item count on action mode.
                } else {
                    actionMode.setTitle(""); //remove item count from action mode.
                    actionMode.finish(); //hide action mode.
                }
                categoriesRecyclerViewAdapter.setSelectedSubCategories(selectedSubCategories);

            }
        }
    }

    private int getPositionFromCategoryName(String name) {
        int pos = -1;
        for (int i = 0; i < mCategories.size(); i++) {
            Category category = mCategories.get(i);
            if (category.getCategoryName() == name) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    // ActionMode.Callback for contextual action mode
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isMultiSelect = true;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_mode_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

            //create AlertDialog to check if user actually wants to delete Items
            final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Delete Items");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Positive button clicked on Delete alert dialog!!");
                    //delete todos from end to start so as to avoid accidental damage to todolist
                    Iterator<Integer> iterator = selectedItems.descendingIterator();
                    while (iterator.hasNext()) {
                        int pos = iterator.next();
                        deleteCategory(pos);
                        categoriesRecyclerViewAdapter.notifyItemRemoved(pos);
                    }

                    Iterator<SubCategory> subCategoryIterator = selectedSubCategories.descendingIterator();
                    while (subCategoryIterator.hasNext()) {
                        SubCategory subCategory = subCategoryIterator.next();
                        deleteSubCategory(subCategory);
                    }
                    mode.finish();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Negative button clicked on Delete alert dialog!!");
                    dialogInterface.cancel();
                }
            });

            //show dialog
            alert.show();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            categoriesRecyclerViewAdapter.setSelectedItems(new TreeSet<Integer>());
            categoriesRecyclerViewAdapter.setSelectedSubCategories(new TreeSet<SubCategory>());
            isMultiSelect = false;
            selectedItems.clear();
            selectedSubCategories.clear();
            actionMode = null;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() starts...");
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_ADD_CATEGORY) {     //Add category activity result

                Log.i(TAG, "Processing add category...");
                if (data.hasExtra(GlobalConstants.CATEGORY_OBJECT)) {     //received category object

                    Category category = (Category) data.getSerializableExtra(GlobalConstants.CATEGORY_OBJECT);
                    addCategory(category);

                } else if (data.hasExtra(GlobalConstants.SUB_CATEGORY_OBJECT)) {     //received sub-category object

                    SubCategory subCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.SUB_CATEGORY_OBJECT);
                    addSubCategory(subCategory);
                }

            } else if (requestCode == REQ_CODE_EDIT_CATEGORY) {       //edit category activity result

                if (data.hasExtra(POSITION_ITEM_TO_EDIT)) {       //Category sent initially

                    int position = data.getIntExtra(POSITION_ITEM_TO_EDIT, -1);

                    if (data.hasExtra(CATEGORY_OBJECT)) {         //category to category edit

                        Category oldCategory = mCategories.get(position);
                        Category newCategory = (Category) data.getSerializableExtra(GlobalConstants.CATEGORY_OBJECT);
                        updateCategory(position, oldCategory, newCategory);

                    } else if (data.hasExtra(SUB_CATEGORY_OBJECT)) {     //category to sub-category edit

                        SubCategory subCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.SUB_CATEGORY_OBJECT);
                        deleteCategory(position);
                        addSubCategory(subCategory);

                    }

                } else {   //sub-category sent initially

                    if (data.hasExtra(OLD_SUB_CATEGORY_OBJECT) && data.hasExtra(NEW_SUB_CATEGORY_OBJECT)) {   //sub-category to sub-category

                        SubCategory oldSubCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.OLD_SUB_CATEGORY_OBJECT);
                        SubCategory newSubCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.NEW_SUB_CATEGORY_OBJECT);
                        updateSubCategory(oldSubCategory, newSubCategory);

                    } else if (data.hasExtra(OLD_SUB_CATEGORY_OBJECT) && data.hasExtra(NEW_CATEGORY_OBJECT)) {      //sub-category to category edit

                        SubCategory oldSubCategory = (SubCategory) data.getSerializableExtra(GlobalConstants.OLD_SUB_CATEGORY_OBJECT);
                        Category newCategory = (Category) data.getSerializableExtra(NEW_CATEGORY_OBJECT);
                        deleteSubCategory(oldSubCategory);
                        addCategory(newCategory);

                    }
                }
            } else {
                Log.i(TAG, "Wrong request code");
            }
        } else {
            Log.i(TAG, "Wrong result code - " + resultCode);
        }

    }


    private void addCategory(Category newCategory) {
        if (payTrackDBHelper.insertDataToCategoriesTable(newCategory)) {
            mCategories.add(newCategory);
            categoriesRecyclerViewAdapter.notifyDataSetChanged();
            Log.d(TAG, "Category inserted - " + newCategory.toString());
        } else {
            Log.e(TAG, "Couldn't insert category - " + newCategory.toString());
        }

    }

    private void addSubCategory(SubCategory subCategory) {
        if (payTrackDBHelper.insertDataToSubCategoriesTable(subCategory)) {
            addSubCategoryToItsParent(subCategory);
            Log.d(TAG, "Sub-Category inserted - " + subCategory.toString());
        } else {
            Log.e(TAG, "Couldn't insert sub-category - " + subCategory.toString());
        }
    }

    private void updateCategory(int position, Category oldCategory, Category newCategory) {
        if (payTrackDBHelper.updateDataInCategoriesTable(oldCategory, newCategory)) {
            String newCategoryName = newCategory.getCategoryName();
            ArrayList<SubCategory> subCategories = oldCategory.getSubCategories();
            if (subCategories != null) {
                for (int i=0; i<subCategories.size(); i++) {
                    SubCategory subCategory = subCategories.get(i);
                    subCategory.setParent(newCategoryName);
                }
                newCategory.setSubCategories(subCategories);
            }
            mCategories.updateItemAt(position, newCategory);
            categoriesRecyclerViewAdapter.notifyDataSetChanged();
            Log.d(TAG, "Category updated - FROM : " + oldCategory.toString() + " TO : " + newCategory.toString());
        } else {
            Log.e(TAG, "Couldn't update category - " + oldCategory.toString());
        }

    }

    private void updateSubCategory(SubCategory oldSubCategory, SubCategory newSubCategory) {
        if (payTrackDBHelper.updateDataInSubCategoriesTable(oldSubCategory, newSubCategory)) {
            removeSubCategoryFromItsParent(oldSubCategory);
            addSubCategoryToItsParent(newSubCategory);
            Log.d(TAG, "SubCategory updated - FROM : " + oldSubCategory.toString() + " TO : " + newSubCategory.toString());
        } else {
            Log.e(TAG, "Couldn't update sub-category - " + oldSubCategory.toString());
        }
    }

    private void addSubCategoryToItsParent(SubCategory subCategory) {
        String parent = subCategory.getParent();
        for (int i=0; i<mCategories.size(); i++) {
            Category category = mCategories.get(i);
            if (category.getCategoryName().equals(parent)) {
                category.addSubCategory(subCategory);
                break;
            }
        }
        categoriesRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void removeSubCategoryFromItsParent(SubCategory subCategory) {
        String parent = subCategory.getParent();
        for (int i=0; i<mCategories.size(); i++) {
            Category category = mCategories.get(i);
            if (category.getCategoryName().equals(parent)) {
                category.removeSubCategory(subCategory);
                break;
            }
        }
        categoriesRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void deleteCategory(int position) {
        //remove category
        Category category = mCategories.get(position);
        if (payTrackDBHelper.deleteDataInCategoriesTable(category)) {
            mCategories.removeItemAt(position);
            Log.d(TAG, "Category deleted : " + category.toString());
        } else {
            Log.e(TAG, "Couldn't delete category : " + category.toString());
        }
    }

    private void deleteSubCategory(SubCategory subCategory) {
        if (payTrackDBHelper.deleteDataInSubCategoriesTable(subCategory)) {
            removeSubCategoryFromItsParent(subCategory);
            categoriesRecyclerViewAdapter.notifyDataSetChanged();
            Log.d(TAG, "SubCategory deleted : " + subCategory.toString());
        } else {
            Log.e(TAG, "Couldn't delete sub-category : " + subCategory.toString());
        }
    }

}
