package com.plbear.iweight.base

import android.net.Uri

/**
 * Created by yanyongjun on 2018/1/28.
 */
class Constant {
    companion object {
        val SP_NAME = "iweight"

        var PROVIDER_AUTHORITY = "com.plbear.iweight.provider"
        var PROVIDER_ITEM = 1
        var PROVIDER_ITEMS_WITHOUT_NOTIFY = 2
        var PROVIDER_POS = 3

        var CONTENT_URI = Uri.parse("content://${PROVIDER_AUTHORITY}/items")
        var CONTENT_URI_WITHOUT_NOTIRY = Uri.parse("content://${PROVIDER_AUTHORITY}/items_without_notify")
    }
}