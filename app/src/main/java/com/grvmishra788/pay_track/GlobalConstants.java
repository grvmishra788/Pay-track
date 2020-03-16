package com.grvmishra788.pay_track;

public abstract class GlobalConstants {

    //Number of Tabs in bottom tab toolbar
    public static final int NUMBER_OF_TABS = 3;

    public static final String BULLET_SYMBOL = "\u2022";  // •

    //Category Activity Types
    public static final int SHOW_CATEGORY= 101;
    public static final int SELECT_CATEGORY = 102;
    public static final int SELECT_PARENT_CATEGORY = 103;

    //Intent request codes
    public static final int REQ_CODE_ADD_ACCOUNT = 1001;
    public static final int REQ_CODE_ADD_CATEGORY = 1002;
    public static final int REQ_CODE_ADD_TRANSACTION = 1003;
    public static final int REQ_CODE_ADD_DEBT = 1004;

    public static final int REQ_CODE_EDIT_ACCOUNT = 1101;
    public static final int REQ_CODE_EDIT_CATEGORY = 1102;

    public static final int REQ_CODE_SELECT_ACCOUNT = 10001;
    public static final int REQ_CODE_SELECT_PARENT_CATEGORY = 10002;
    public static final int REQ_CODE_SELECT_CATEGORY = 10005;

    //Intent params
    public static final String CATEGORY_INTENT_TYPE = "com.grvmishra788.pay_track.category_intent_type";

    public static final String SELECTED_ACCOUNT_NAME = "com.grvmishra788.pay_track.selected_account_name";
    public static final String SELECTED_CATEGORY_NAME = "com.grvmishra788.pay_track.selected_category_name";

    public static final String SELECTED_SUB_CATEGORY_PARENT_NAME = "com.grvmishra788.pay_track.selected_sub_category_parent_name";
    public static final String SELECTED_SUB_CATEGORY_ACCOUNT_NAME = "com.grvmishra788.pay_track.selected_sub_category_account_name";
    public static final String SELECTED_SUB_CATEGORY_NAME = "com.grvmishra788.pay_track.selected_sub_category_name";

    public static final String SELECTED_CATEGORY_ACCOUNT_NAME = "com.grvmishra788.pay_track.selected_category_account_name";

    public static final String ACCOUNT_OBJECT = "com.grvmishra788.pay_track.object_account";
    public static final String CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_category";
    public static final String NEW_CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_new_category";
    public static final String SUB_CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_sub_category";
    public static final String OLD_SUB_CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_old_sub_category";
    public static final String NEW_SUB_CATEGORY_OBJECT = "com.grvmishra788.pay_track.object_new_sub_category";
    public static final String TRANSACTION_OBJECT = "com.grvmishra788.pay_track.object_transaction";
    public static final String DEBT_OBJECT = "com.grvmishra788.pay_track.object_debt";

    public static final String ITEM_TO_EDIT = "com.grvmishra788.pay_track.item_to_edit";
    public static final String POSITION_ITEM_TO_EDIT = "com.grvmishra788.pay_track.position_item_to_edit";

    public static final String SUB_ITEM_TO_EDIT = "com.grvmishra788.pay_track.sub_item_to_edit";

    //Date format string to show Day and Date
    public static final String DATE_FORMAT_DAY_AND_DATE = "EEE - MMM dd, yyyy";

    //enums
    public static enum TransactionType{
        DEBIT, CREDIT
    }

    public static enum DebtType{
        PAY, RECEIVE
    }

}
