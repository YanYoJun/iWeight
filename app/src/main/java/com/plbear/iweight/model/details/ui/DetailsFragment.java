package com.plbear.iweight.model.details.ui;

import android.database.ContentObserver;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseFragment;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.model.details.adapter.DetailsRecyclerAdapter;
import com.plbear.iweight.model.details.view.SlideCutRecyclerView;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by yanyongjun on 2018/6/28.
 */

public class DetailsFragment extends BaseFragment {
    @BindView(R.id.listview_details)
    SlideCutRecyclerView mListView;

    private DetailsRecyclerAdapter mAdapter;
    private ArrayList<Data> mDataList;

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            mAdapter.notifyDataChanged();
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
        mAdapter = new DetailsRecyclerAdapter(mActivity, mDataList);
        mListView.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL,false);
        mListView.setLayoutManager(linearLayoutManager);

        mListView.setRemoveListener(new SlideCutRecyclerView.RemoveListener() {
            @Override
            public void removeItem(SlideCutRecyclerView.RemoveDirection direction, int position) {
                LogInfo.e(TAG,"mListView removeItem:"+position);
                if (direction == SlideCutRecyclerView.RemoveDirection.LEFT) {

                    Data data = mDataList.get(position);
                    data.setEditMode(true);
                } else {

                    Data data = mDataList.get(position);
                    data.setEditMode(false);
                }
                mAdapter.setChangePos(position);
                mAdapter.notifyDataChanged();
            }
        });
    }

}
