package com.plbear.iweight.Utils;

import android.net.Uri;

/**
 * Created by yanyongjun on 2017/5/19.
 */

public class Constant {
    public static final String SP_NAME = "iweight";

    public static final String PROVIDER_AUTHORITY = "com.plbear.iweight.provider";
    public static final int PROVIDER_ITEM = 1;
    public static final int PROVIDER_ITEMS_WITHOUT_NOTIFY = 2;
    public static final int PROVIDER_POS = 3;

    public static final Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_AUTHORITY+"/items");
    public static final Uri CONTENT_URI_WITHOUT_NOTIRY = Uri.parse("content://"+PROVIDER_AUTHORITY+"/items_without_notify");

}
