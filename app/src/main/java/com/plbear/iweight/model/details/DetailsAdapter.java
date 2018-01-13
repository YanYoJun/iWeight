package com.plbear.iweight.model.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.plbear.iweight.Data.Data;
import com.plbear.iweight.R;
import com.plbear.iweight.Utils.OldUtils;
import com.plbear.iweight.Data.DataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yanyongjun on 2016/11/23.
 */

public class DetailsAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<Data> mListData = null;
    private LayoutInflater mInflater = null;
    private boolean mIsEditMode = false;
    private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
    private int mSelectCount = 0;
    private final static String TAG = "DetailsAdapter";
    private OnItemClick mCallback = null;

    public interface OnItemClick {
        void itemClick(HashMap<Integer, Boolean> mSelectMap, int selectCount);

        void longItemClick(boolean editMode);
    }

    public DetailsAdapter(Context context, ArrayList<Data> list, OnItemClick callback) {
        mContext = context;
        mListData = list;
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
    }
    @Override
    public void notifyDataSetChanged(){
        DataManager db = DataManager.getInstance(mContext);
        mListData = db.queryAll();
        OldUtils.sortDataBigToSmall(mListData);
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (mIsEditMode) {
            view = mInflater.inflate(R.layout.item_details_editmode, null);
        } else {
            view = mInflater.inflate(R.layout.item_details, null);
        }
        TextView labDate = (TextView) view.findViewById(R.id.lab_details_item_date);
        TextView labWeight = (TextView) view.findViewById(R.id.lab_details_item_weight);
        Data data = mListData.get(position);
        labDate.setText(OldUtils.formatTimeFull(data.getTime()));
        labWeight.setText(String.valueOf(data.getWeight()));
        if (mIsEditMode) {
            final CheckBox selectBox = (CheckBox) view.findViewById(R.id.checkbox_details_item_select);
            selectBox.setVisibility(View.VISIBLE);
            selectBox.setOnClickListener(null);
            selectBox.setClickable(false);
            Boolean isCheck = mSelectMap.get(position);
            selectBox.setChecked((isCheck != null) && isCheck.booleanValue());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean temp = mSelectMap.get(position);
                    boolean click = (temp == null ? false : temp);
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
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    enterEditMode();
                    return true;
                }
            });
        }
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    public void enterEditMode() {
        if (!mIsEditMode) {
            mIsEditMode = true;
            mSelectMap.clear();
            notifyDataSetChanged();
        }
        mCallback.longItemClick(mIsEditMode);
    }

    public void exitEditMode() {
        if (mIsEditMode) {
            mIsEditMode = false;
            mSelectMap.clear();
            mSelectCount = 0;
            notifyDataSetChanged();
        }
        mCallback.longItemClick(mIsEditMode);

    }

    /**
     * on select all click
     */
    public void onSelectAllClick() {
        boolean isSelectAll = mSelectCount == mListData.size();
        if (isSelectAll) {
            mSelectMap.clear();
            mSelectCount = 0;
        } else {
            for (int i = 0; i < mListData.size(); i++) {
                mSelectMap.put(i, true);
            }
            mSelectCount = mListData.size();
        }
        notifyDataSetChanged();
        mCallback.itemClick(mSelectMap, mSelectCount);
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public ArrayList<Data> getSelectData() {
        ArrayList<Data> list = new ArrayList<Data>(mSelectCount);
        Iterator it = mSelectMap.keySet().iterator();
        while (it.hasNext()) {
            int key = (int) it.next();
            if (mSelectMap.get(key)) {
                list.add(mListData.get(key));
            }
        }
        return list;
    }
}
