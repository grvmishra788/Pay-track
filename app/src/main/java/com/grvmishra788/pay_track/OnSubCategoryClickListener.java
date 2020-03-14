package com.grvmishra788.pay_track;

import com.grvmishra788.pay_track.DS.SubCategory;

public interface OnSubCategoryClickListener {
    void onItemClick(int position, SubCategory subCategory);
    void onItemLongClick(int position, SubCategory subCategory);
}
