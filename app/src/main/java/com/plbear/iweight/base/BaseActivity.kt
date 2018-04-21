package com.plbear.iweight.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.plbear.iweight.utils.LogInfo

/**
 * Created by yanyongjun on 2018/normal_1/28.
 */
abstract class BaseActivity : AppCompatActivity() {
    abstract fun getLayout(): Int
    abstract fun afterLayout()
    protected val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var layout = getLayout()
        if (layout != 0) {
            setContentView(getLayout())
        }
        loginfo("onCreate")
        AppManager.getAppManager().addToStack(this)
        afterLayout()
    }


    override fun onDestroy() {
        super.onDestroy()
        AppManager.getAppManager().removeFromStack(this)
        loginfo("onDestroy")
    }

    override fun onStart() {
        super.onStart()
        loginfo("onStart")
    }

    override fun onResume() {
        super.onResume()
        loginfo("onresume")
    }

    protected fun exitAll() {
        AppManager.getAppManager().finshAllActivity()
    }

    override fun finish() {
        AppManager.getAppManager().removeFromStack(this)
        super.finish()
    }

    fun loginfo(info: String) {
        LogInfo.i(TAG, info)
    }

    fun logerror(error: String) {
        LogInfo.e(TAG, error)
    }
}