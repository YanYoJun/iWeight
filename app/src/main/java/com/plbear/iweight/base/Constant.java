package com.plbear.iweight.base;

import android.net.Uri;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public class Constant {
    public static String SP_NAME = "iweight";
    public static String PROVIDER_AUTHORITY = "com.plbear.iweight.provider";
    public final static int PROVIDER_ITEM = 1;
    public final static int PROVIDER_ITEMS_WITHOUT_NOTIFY = 2;
    public final static String BUGLY_ID = "934c6ebddb";
    public final static String APP_ID="5b90e3c3";
    public final static String AD_ID_Main="21B1454F6FF7074469402472FEA5E1C5";

    public static Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/items");
    public static Uri CONTENT_URI_WITHOUT_NOTIRY = Uri.parse("content://" + PROVIDER_AUTHORITY + "/items_without_notify");

    public static final String PREFERENCE_KEY_SET_TARGET_WEIGHT = "set_target_weight";
    public static final String PREFERENCE_KEY_ONLY_ONCE_EVERYDAY = "only_once_everyday";
    public static final String PREFERENCE_KEY_EXPORT_IMPORT = "export_import_switch";
    public static final String PREFERENCE_KEY_UNIT = "value_unit";

    public static final String PRE_KEY_LOGIN_NAME = "login_name";
    public static final String PRE_KEY_LOGIN_PASSWD = "login_passwd";
    public static final String PRE_KEY_LOGIN_STATUS = "login_status";
    public static final String PRE_KEY_USER_ID = "login_id";

}