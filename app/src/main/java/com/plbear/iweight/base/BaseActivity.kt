package com.plbear.iweight.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.plbear.iweight.utils.MyLog

/**
 * Created by yanyongjun on 2018/1/28.
 */
abstract class BaseActivity : AppCompatActivity() {
    abstract fun getLayout(): Int
    abstract fun afterLayout()
    protected val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        MyLog.d(TAG, "onCreate")
        AppManager.getAppManager().addToStack(this)
        afterLayout()
    }


    override fun onDestroy() {
        super.onDestroy()
        AppManager.getAppManager().removeFromStack(this)
        MyLog.d(TAG, "onDestroy")
    }

    override fun onStart() {
        super.onStart()
        MyLog.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        MyLog.d(TAG, "onResume")
    }

    protected fun exitAll() {
        AppManager.getAppManager().finshAllActivity()
    }

    override fun finish() {
        AppManager.getAppManager().removeFromStack(this)
        super.finish()
    }
}