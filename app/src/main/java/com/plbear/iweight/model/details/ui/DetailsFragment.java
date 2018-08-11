package com.plbear.iweight.model.details.ui;

import android.database.ContentObserver;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseFragment;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.model.details.adapter.DetailsAdapter;
import com.plbear.iweight.model.details.view.SlideCutListView;
import com.plbear.iweight.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by yanyongjun on 2018/6/28.
 */

public class DetailsFragment extends BaseFragment {
    @BindView(R.id.listview_details)
    SlideCutListView mListView;

    private DetailsAdapter mAdapter;
    private ArrayList<Data> mDataList;
    private DetailsAdapter.OnItemClick mCallback = new DetailsAdapter.OnItemClick() {
        @Override
        public void itemClick(HashMap<Integer, Boolean> mSelectMap, int selectCount) {
        }

        @Override
        public void longItemClick(boolean editMode) {
        }
    };

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            mAdapter.notifyDataSetChanged();
            super.onChange(selfChange);
        }
    };

    @Override
    public int getLayout() {
        return R.layout.framgent_details;
    }

    @Override
    public void afterLayout() {
        init();
    }

    @Override
    public void onStop() {
        mActivity.getContentResolver().unregisterContentObserver(mObserver);
        super.onStop();
    }

    @Override
    public void onStart() {
        mActivity.getContentResolver().registerContentObserver(Constant.CONTENT_URI, true, mObserver);
        super.onStart();
    }

    @Override
    public void onResume() {
        loginfo("Details onREsume");
        //mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void init() {
        mListView = mActivity.findViewById(R.id.listview_details);

        mDataList = DataManager.getInstance().queryAll();
        Utils.sortDataBigToSmall(mDataList);
        mAdapter = new DetailsAdapter(mActivity, mDataList, mCallback);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        mListView.setRemoveListener(new SlideCutListView.RemoveListener() {
            @Override
            public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
                if (direction == SlideCutListView.RemoveDirection.LEFT) {

                    Data data = mDataList.get(position);
                    loginfo("yanlog removeItem left:"+position+" data:"+data);
                    data.setEditMode(true);
                } else {

                    Data data = mDataList.get(position);
                    loginfo("yanlog removeItem right:"+position+" data:"+data);
                    data.setEditMode(false);
                }
                mAdapter.setChangePos(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

}
