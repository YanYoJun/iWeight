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
    public final static int PROVIDER_POS = 3;

    public static Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/items");
    public static Uri CONTENT_URI_WITHOUT_NOTIRY = Uri.parse("content://" + PROVIDER_AUTHORITY + "/items_without_notify");
}