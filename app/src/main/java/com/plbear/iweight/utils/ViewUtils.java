package com.plbear.iweight.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.plbear.iweight.R;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ViewUtils {
//    public static void showChangeDialog(final Activity context) {
////        ArrayList<Data> list = mAdapter.getSelectData();
////        final Data data = list.get(0);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        final View layout = context.getLayoutInflater().inflate(R.layout.dialog_main_input_weight, null);
//        builder.setView(layout);
//
//        final AlertDialog dialog = builder.create();
//        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
//        final EditText editText = layout.findViewById(R.id.dialog_input_weight);
//        editText.setText("");
//        editText.setSelection(editText.getText().length());
//
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    float weight = Float.parseFloat(editText.getText().toString());
//                    if (weight < 5 || weight > 200) {
//                        Utils.showToast("您输入的值太不合理了");
//                        return;
//                    }
//                    data.setWeight(weight);
//                    DataManager.getInstance().update(data);
//                    dialog.dismiss();
//                    mAdapter.exitEditMode();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Utils.showToast("请检查输入的值");
//                }
//            }
//        });
//
//        Button btnCancel = layout.findViewById(R.id.dialog_cacnel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//
//        editText.requestFocus();
//        editText.setFocusable(true);
//        editText.setFocusableInTouchMode(true);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (editText != null) {
//                    InputMethodManager input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    input.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
//                }
//            }
//        }, 200);
}
