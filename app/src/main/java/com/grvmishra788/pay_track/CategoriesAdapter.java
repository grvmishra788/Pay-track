package com.grvmishra788.pay_track;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grvmishra788.pay_track.DS.Category;
import com.grvmishra788.pay_track.DS.SubCategory;

import java.util.ArrayList;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import static com.grvmishra788.pay_track.GlobalConstants.SELECT_PARENT_CATEGORY;
import static com.grvmishra788.pay_track.GlobalConstants.SHOW_CATEGORY;

class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    //constants
    private static final String TAG = "Pay-Track: " + CategoriesAdapter.class.getName(); //constant Class TAG

    //Variable to store context from which Adapter has been called
    private Context mContext;

    //Variable for accessing Categories List in  CategoriesAdapter
    private SortedList<Category> mCategories;

    //Variable for onItemClickListener
    private OnItemClickListener mOnItemClickListener;
    private OnSubCategoryClickListener subCategoryClickListener;

    //Variable to store categoryActivityType
    private int categoryActivityType = SHOW_CATEGORY;

    //Variable to store selectedItem positions when launching Contextual action mode
    private TreeSet<Integer> selectedItems = new TreeSet<>();

    //Variable to store subcategories when launching Contextual action mode
    private TreeSet<SubCategory> selectedSubCategories = new TreeSet<>();

    //Constructor: binds Category object data to CategoriesAdapter
    public CategoriesAdapter(Context context, SortedList<Category> categories, int categoryActivityType) {
        Log.d(TAG, TAG + ": Constructor starts");
        this.mContext = context;
        this.mCategories = categories;
        this.categoryActivityType = categoryActivityType;
        Log.d(TAG, TAG + ": Constructor ends");
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder()...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_category, parent, false);
        CategoriesViewHolder categoriesViewHolder = new CategoriesViewHolder(view);
        Log.i(TAG, "onCreateViewHolder() ends!");
        return categoriesViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder categoriesViewHolder, int position) {
        Category category = mCategories.get(position);
        SortedList<SubCategory> subCategories = category.getSubCategories();
        if (subCategories == null || subCategories.size()==0 || categoryActivityType == SELECT_PARENT_CATEGORY) {
            categoriesViewHolder.horizontal_bar.setVisibility(View.GONE);
        } else {
            categoriesViewHolder.horizontal_bar.setVisibility(View.VISIBLE);
        }
        categoriesViewHolder.setSubCategories(subCategories);

        categoriesViewHolder.tv_categoryName.setText(category.getCategoryName());
        categoriesViewHolder.tv_defaultAccount.setText(category.getAccountNickName());

        String desc = category.getDescription();
        categoriesViewHolder.tv_description.setText(desc);
        if (InputValidationUtilities.isValidString(desc)) {
            categoriesViewHolder.ll_show_description.setVisibility(View.VISIBLE);
        } else {
            categoriesViewHolder.ll_show_description.setVisibility(View.GONE);
        }

        //update subcategories
        categoriesViewHolder.subCategoryRecyclerViewAdapter.setSubCategories(subCategories);
        categoriesViewHolder.subCategoryRecyclerViewAdapter.setSelectedSubCategories(selectedSubCategories);

        if (selectedItems.contains(position)) {
            //if item is selected then,set foreground color of FrameLayout.
            categoriesViewHolder.rootView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.colorAccentTransparent)));
        } else {
            //else remove selected item color.
            categoriesViewHolder.rootView.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent)));
        }

    }

    @Override
    public int getItemCount() {
        if (mCategories == null)
            return 0;
        else
            return mCategories.size();
    }

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setSubCategoryClickListener(OnSubCategoryClickListener subCategoryClickListener) {
        this.subCategoryClickListener = subCategoryClickListener;
    }

    //method to update selected items
    public void setSelectedItems(TreeSet<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public void setSelectedSubCategories(TreeSet<SubCategory> selectedSubCategories) {
        this.selectedSubCategories = selectedSubCategories;
        notifyDataSetChanged();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_categoryName, tv_defaultAccount, tv_description;

        //Variables to store linear layout associated with category description
        private LinearLayout ll_show_description, rootView;

        private SortedList<SubCategory> mSubCategories;

        private RecyclerView subCategoryRecyclerView;
        private SubCategoriesAdapter subCategoryRecyclerViewAdapter;
        private RecyclerView.LayoutManager subCategoryRecyclerViewLayoutManager;

        private View horizontal_bar;

        public CategoriesViewHolder(@NonNull View itemView) {
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

            rootView = itemView.findViewById(R.id.root_view);
            tv_categoryName = itemView.findViewById(R.id.tv_show_category_name);
            tv_defaultAccount = itemView.findViewById(R.id.tv_show_default_account);
            tv_description = itemView.findViewById(R.id.tv_show_description);

            //init horizontal bar
            horizontal_bar = itemView.findViewById(R.id.horizontal_bar);

            //init description linear layouts
            ll_show_description = itemView.findViewById(R.id.ll_show_description);

            //init sub category RecyclerView
            subCategoryRecyclerView = itemView.findViewById(R.id.sub_items_recyclerview);
            initSubCategoryRecyclerView();
            if (categoryActivityType == SELECT_PARENT_CATEGORY) {
                subCategoryRecyclerView.setVisibility(View.GONE);
            }
        }

        public void setSubCategories(SortedList<SubCategory> subCategories) {
            this.mSubCategories = subCategories;
        }

        public SortedList<SubCategory> getSubCategories() {
            return mSubCategories;
        }

        private void initSubCategoryRecyclerView() {
            if (mSubCategories == null) {
                mSubCategories = new SortedList<SubCategory>(SubCategory.class, new SortedList.Callback<SubCategory>() {
                    @Override
                    public int compare(SubCategory o1, SubCategory o2) {
                        return o1.getSubCategoryName().toLowerCase().compareTo(o2.getSubCategoryName().toLowerCase());
                    }

                    @Override
                    public void onChanged(int position, int count) {

                    }

                    @Override
                    public boolean areContentsTheSame(SubCategory oldItem, SubCategory newItem) {
                        return false;
                    }

                    @Override
                    public boolean areItemsTheSame(SubCategory item1, SubCategory item2) {
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

            subCategoryRecyclerView.setHasFixedSize(true);
            subCategoryRecyclerViewLayoutManager = new LinearLayoutManager(mContext);
            subCategoryRecyclerViewAdapter = new SubCategoriesAdapter(mContext, mSubCategories);
            if (subCategoryClickListener != null)
                subCategoryRecyclerViewAdapter.setOnSubCategoryClickListener(subCategoryClickListener);
            subCategoryRecyclerView.setLayoutManager(subCategoryRecyclerViewLayoutManager);
            subCategoryRecyclerView.setAdapter(subCategoryRecyclerViewAdapter);
        }
    }

}
