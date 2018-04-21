package com.plbear.iweight.model.main.view

import android.app.Activity
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.plbear.iweight.utils.Utils

/**
 * Created by yanyongjun on 2018/normal_4/normal_6.
 */
class KeyboardBuilder(activity: Activity, keyboardView: KeyboardView, keyRes: Int, edittext: EditText, listener: OnStatusChanged) {
    private var mActivity = activity
    private var mKeyboardView = keyboardView
    private var mRes = keyRes
    private var mEditText = edittext
    private var mStatusListener = listener
    private var mListener = object : KeyboardView.OnKeyboardActionListener {
        override fun swipeRight() {
        }

        override fun onPress(primaryCode: Int) {
        }

        override fun onRelease(primaryCode: Int) {
        }

        override fun swipeLeft() {
        }

        override fun swipeUp() {
        }

        override fun swipeDown() {
        }

        override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
            var editable = mEditText.getText();
            var start = mEditText.getSelectionStart();
            when (primaryCode) {
                Keyboard.KEYCODE_DELETE ->
                    if (editable != null && editable.length > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                Keyboard.KEYCODE_CANCEL -> {
                    hideKeyboard()
                    mStatusListener.onChanged(STATUS_CANCEL)
                }
                Keyboard.KEYCODE_DONE -> {
                    hideKeyboard()
                    if (TextUtils.isEmpty(editable)) {
                        mStatusListener.onChanged(STATUS_CANCEL)
                    } else {
                        mStatusListener.onChanged(STATUS_SUBMIT)
                    }
                }
                else -> {
                    if (check(editable.toString(), primaryCode.toChar())) {
                        editable.insert(start, primaryCode.toChar().toString());
                    }
                }
            }
        }

        override fun onText(text: CharSequence?) {
        }
    }

    companion object {
        val TAG = "KeyboardBuilder"

        val STATUS_CANCEL = 1
        val STATUS_SUBMIT = 2
    }

    init {
        var keyboard = Keyboard(mActivity, mRes)
        mKeyboardView.keyboard = keyboard
        mKeyboardView.isPreviewEnabled = false
        mKeyboardView.setOnKeyboardActionListener(mListener)
    }


    fun hideKeyboard() {
        mKeyboardView.visibility = View.GONE
        mKeyboardView.isEnabled = false

    }

    fun showKeyboard(v: View?) {
        mKeyboardView.visibility = View.VISIBLE
        mKeyboardView.isEnabled = true
        if (v != null) {
            v?.isFocusable = false
            (mActivity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(v.windowToken, 0)
        }
        mKeyboardView.requestFocus()

        mKeyboardView.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    hideKeyboard()
                    mStatusListener.onChanged(STATUS_CANCEL)
                }
            }
        })
    }

    private var mFailsTimes = 0
    private fun check(str: String, c: Char): Boolean {
        if (TextUtils.isEmpty(str) && c == '.') {
            return false
        }
        if (TextUtils.isEmpty(str) && c == '0') {
            return true
        }
        if (str.length > 4) {
            mFailsTimes++
            if (mFailsTimes > 6) {
                Utils.showToast("够了够了，够精确了")
                mFailsTimes = 0
            }
            return false
        }
        if (!Utils.checkWeightValueFat((str + c.toString()).toFloat())) {
            mFailsTimes++
            if (mFailsTimes > 6) {
                Utils.showToast("你能不能长点心~~~")
                mFailsTimes = 0
            }
            return false
        }
        return true
    }

    interface OnStatusChanged {
        fun onChanged(int: Int)
    }
}