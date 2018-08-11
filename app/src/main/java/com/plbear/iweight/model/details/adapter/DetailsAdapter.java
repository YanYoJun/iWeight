package com.plbear.iweight.model.details.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yanyongjun on 2018/6/28.
 */

public class DetailsAdapter extends BaseAdapter {
    private final static String TAG = "DetailsAdapter";
    private BaseActivity mContext;
    private ArrayList<Data> mListData;
    private LayoutInflater mInflater;
    public boolean isEditMode = false;
    private DetailsAdapter.OnItemClick mCallback;
    private int mChangePos = -1;

    public interface OnItemClick {
        void itemClick(HashMap<Integer, Boolean> mSelectMap, int selectCount);

        void longItemClick(boolean editMode);
    }

    public void setChangePos(int pos) {
        mChangePos = pos;
    }

    public void notifyDataSetChanged() {
        DataManager db = DataManager.getInstance(mContext);
        ArrayList<Data> newList = db.queryAll();
        Utils.mergeWeightData(mListData, newList);
        Utils.sortDataBigToSmall(mListData);
        super.notifyDataSetChanged();
    }

    public DetailsAdapter(BaseActivity context, ArrayList<Data> list, DetailsAdapter.OnItemClick callback) {
        mContext = context;
        mListData = list;
        mCallback = callback;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        Data data = mListData.get(position);
        if (isEditMode || data.isEditMode()) {
            view = mInflater.inflate(R.layout.item_details_editmode, null);
        } else {
            view = mInflater.inflate(R.layout.item_details, null);
        }

        TextView labDate = view.findViewById(R.id.lab_details_item_date);
        TextView labWeight = view.findViewById(R.id.lab_details_item_weight);

        labDate.setText(Utils.formatTimeFull(data.getTime()));
        labWeight.setText("" + data.getWeight());
        if (isEditMode || data.isEditMode()) {
            Button btnChange = view.findViewById(R.id.btn_change);
            Button btnDelete = view.findViewById(R.id.btn_delete);
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showChangeDialog();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog();
                }
            });
        } else {
            Button btnChange = view.findViewById(R.id.btn_change);
            Button btnDelete = view.findViewById(R.id.btn_delete);
            btnChange.setOnClickListener(null);
            btnDelete.setOnClickListener(null);
        }
        return view;
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View layout = mContext.getLayoutInflater().inflate(R.layout.dialog_details_delete, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data temp = mListData.get(mChangePos);
                ArrayList<Data> list = new ArrayList<>();
                list.add(temp);

                DataManager.getInstance().delete(list);
                dialog.dismiss();
                exitEditMode();
            }
        });

        Button btnCancal = layout.findViewById(R.id.dialog_cacnel);
        btnCancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showChangeDialog() {
        Data temp = mListData.get(mChangePos);
        ArrayList<Data> list = new ArrayList<>();
        list.add(temp);

        final Data data = list.get(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View layout = mContext.getLayoutInflater().inflate(R.layout.dialog_main_input_weight, null);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
        final EditText editText = layout.findViewById(R.id.dialog_input_weight);
        editText.setText("" + data.getWeight());
        editText.setSelection(editText.getText().length());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    float weight = Float.parseFloat(editText.getText().toString());
                    if (weight < 5 || weight > 200) {
                        Utils.showToast("您输入的值太不合理了");
                        return;
                    }
                    data.setWeight(weight);
                    DataManager.getInstance().update(data);
                    dialog.dismiss();
                    exitEditMode();
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast("请检查输入的值");
                }
            }
        });

        Button btnCancel = layout.findViewById(R.id.dialog_cacnel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

        editText.requestFocus();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (editText != null) {
                    InputMethodManager input = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 200);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public Object getItem(int position) {
        return mListData.get(position);
    }

    public int getCount() {
        return mListData.size();
    }


    public ArrayList<Data> getListData() {
        return mListData;
    }


    public void enterEditMode() {
        isEditMode = true;
        notifyDataSetChanged();
        mCallback.longItemClick(isEditMode);
    }

    public void exitEditMode() {
        isEditMode = false;
        for (Data data : mListData) {
            data.setEditMode(false);
        }
        notifyDataSetChanged();
        mCallback.longItemClick(isEditMode);
    }
}

