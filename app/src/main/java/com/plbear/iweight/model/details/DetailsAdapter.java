package com.plbear.iweight.model.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yanyongjun on 2018/6/28.
 */

public class DetailsAdapter extends BaseAdapter {
    private final static String TAG = "DetailsAdapter";
    private Context mContext;
    private ArrayList<Data> mListData;
    private LayoutInflater mInflater;
    boolean isEditMode = false;
    private HashMap<Integer, Boolean> mSelectMap = new HashMap<>();
    private int mSelectCount = 0;
    private DetailsAdapter.OnItemClick mCallback;
    private ArrayList<Data> selectData;

    public ArrayList<Data> getSelectData() {
        ArrayList<Data> list = new ArrayList<>(mSelectCount);
        java.util.Iterator<Integer> it = mSelectMap.keySet().iterator();
        while (it.hasNext()) {
            int key = it.next();
            if (mSelectMap.get(key)) {
                list.add(mListData.get(key));
            }
        }
        return list;
    }

    public interface OnItemClick {
        void itemClick(HashMap<Integer, Boolean> mSelectMap, int selectCount);

        void longItemClick(boolean editMode);
    }

    public void notifyDataSetChanged() {
        DataManager db = DataManager.getInstance(mContext);
        mListData = db.queryAll();
        Utils.sortDataBigToSmall(mListData);
        super.notifyDataSetChanged();
    }


    public DetailsAdapter(Context context, ArrayList<Data> list, DetailsAdapter.OnItemClick callback) {
        mContext = context;
        mListData = list;
        mCallback = callback;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (isEditMode) {
            view = mInflater.inflate(R.layout.item_details_editmode, null);
        } else {
            view = mInflater.inflate(R.layout.item_details, null);
        }

        TextView labDate = view.findViewById(R.id.lab_details_item_date);
        TextView labWeight = view.findViewById(R.id.lab_details_item_weight);
        Data data = mListData.get(position);
        labDate.setText(Utils.formatTimeFull(data.getTime()));
        labWeight.setText("" + data.getWeight());
        if (isEditMode) {
            final CheckBox selectBox = view.findViewById(R.id.checkbox_details_item_select);
            selectBox.setVisibility(View.VISIBLE);
            selectBox.setOnClickListener(null);
            selectBox.setClickable(false);
            Boolean isCheck = mSelectMap.get(position);
            selectBox.setClickable(isCheck != null && true == isCheck);
            selectBox.setChecked(isCheck != null && true == isCheck);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean temp = mSelectMap.get(position);
                    boolean click = temp != null && true == temp;
                    mSelectMap.put(position, !click);
                    if (click) {
                        mSelectCount--;
                    } else {
                        mSelectCount++;
                    }
                    selectBox.setChecked(!click);
                    mCallback.itemClick(mSelectMap, mSelectCount);
                }
            });
        } else {
            view.setClickable(false);
            view.setOnClickListener(null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterEditMode();
                }
            });
        }
        return view;
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


    void enterEditMode() {
        if (!isEditMode) {
            isEditMode = true;
            mSelectMap.clear();
            notifyDataSetChanged();
        }
        mCallback.longItemClick(isEditMode);
    }

    void exitEditMode() {
        if (isEditMode) {
            isEditMode = false;
            mSelectMap.clear();
            mSelectCount = 0;
            notifyDataSetChanged();
        }
        mCallback.longItemClick(isEditMode);
    }

    /**
     * on select all click
     */
    void onSelectAllClick() {
        boolean isSelectAll = mSelectCount == mListData.size();
        if (isSelectAll) {
            mSelectMap.clear();
            mSelectCount = 0;
        } else {
            int i = 0;
            for (Data data : mListData) {
                mSelectMap.put(i++, true);
            }
            mSelectCount = mListData.size();
        }
        notifyDataSetChanged();
        mCallback.itemClick(mSelectMap, mSelectCount);
    }

}

