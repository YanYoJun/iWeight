package com.plbear.iweight.model.main.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.plbear.iweight.utils.Utils;

/**
 * Created by yanyongjun on 2018/6/27.
 */

public class KeyboardBuilder {
    private final static String TAG = "KeyboardBuilder";
    public final static int STATUS_SUBMIT = 2;
    public final static int STATUS_CANCEL = 1;

    private Activity mActivity;
    private KeyboardView mKeyboardView;
    private int mRes;
    private KeyboardBuilder.OnStatusChanged mStatusListener;
    private EditText mEditText;
    private KeyboardView.OnKeyboardActionListener mListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = mEditText.getText();
            int start = mEditText.getSelectionStart();
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                    break;
                case Keyboard.KEYCODE_CANCEL:
                    hideKeyboard();
                    mStatusListener.onChanged(STATUS_CANCEL);
                    break;
                case Keyboard.KEYCODE_DONE:
                    hideKeyboard();
                    if (TextUtils.isEmpty(editable)) {
                        mStatusListener.onChanged(STATUS_CANCEL);
                    } else {
                        mStatusListener.onChanged(STATUS_SUBMIT);
                    }
                    break;
                default:
                    if (check(editable.toString(), primaryCode)) {
                        editable.insert(start, ((char)primaryCode)+"");
                    }
                    break;
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    public KeyboardBuilder(Activity activity, View keyboardView, int keyRes, EditText editText, KeyboardBuilder.OnStatusChanged listener) {
        mActivity = activity;
        mKeyboardView = (KeyboardView) keyboardView;
        mRes = keyRes;
        mEditText = editText;
        mStatusListener = listener;

        mKeyboardView.setKeyboard(new Keyboard(mActivity, mRes));
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(mListener);
    }

    public void hideKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void showKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null) {
            v.setFocusable(false);
            InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        mKeyboardView.requestFocus();
        mKeyboardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                    mStatusListener.onChanged(STATUS_CANCEL);
                }
            }
        });
    }

    private int mFailsTimes = 0;

    private boolean check(String str, int c) {
        if (TextUtils.isEmpty(str) && c == '.') {
            return false;
        }
        if (TextUtils.isEmpty(str) && c == '0') {
            return true;
        }
        if (str.length() > 4) {
            mFailsTimes++;
            if (mFailsTimes > 6) {
                Utils.showToast("够了够了，够精确了");
                mFailsTimes = 0;
            }
            return false;
        }
        if (!Utils.checkWeightValueFat(Float.parseFloat(str + c))) {
            mFailsTimes++;
            if (mFailsTimes > 6) {
                Utils.showToast("你能不能长点心~~~");
                mFailsTimes = 0;
            }
            return false;
        }
        return true;
    }

    public interface OnStatusChanged {
        void onChanged(int temp);
    }

}
