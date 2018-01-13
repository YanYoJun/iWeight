package com.plbear.iweight.model.details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.plbear.iweight.Data.Data;
import com.plbear.iweight.R;
import com.plbear.iweight.Utils.Constant;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.Utils.OldUtils;
import com.plbear.iweight.Data.DataManager;
import com.plbear.iweight.activity.BaseActivity;
import com.plbear.iweight.model.main.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HuHu on 2016/11/23.
 */

public class DetailsActivity extends BaseActivity {
    private ListView mListView = null;
    private DetailsAdapter mAdapter = null;
    private ArrayList<Data> mDataList = null;
    private DataManager mDB = null;
    private ImageButton mBtnBack = null;
    private Button mBtnSelectAll = null;
    private Button mBtnChange = null;
    private Button mBtnDelete = null;
    private DetailsAdapter.OnItemClick mCallback = new DetailsAdapter.OnItemClick() {
        @Override
        public void itemClick(HashMap<Integer, Boolean> mSelectMap, int selectCount) {
            ILog.d(TAG,"itemClick:"+selectCount);
            int size = mDataList.size();
            if (size != selectCount) {
                mBtnSelectAll.setText(R.string.select_all);
            } else {
                mBtnSelectAll.setText(R.string.disselect_all);
            }
            if (selectCount == 1) {
                mBtnChange.setClickable(true);
                mBtnChange.setTextColor(Color.BLACK);
            } else {
                mBtnChange.setClickable(false);
                mBtnChange.setTextColor(getResources().getColor(R.color.details_item_bg));
            }
            if (selectCount > 0) {
                mBtnDelete.setClickable(true);
                mBtnDelete.setTextColor(Color.BLACK);
            } else {
                mBtnDelete.setClickable(false);
                mBtnDelete.setTextColor(getResources().getColor(R.color.details_item_bg));
            }
        }

        @Override
        public void longItemClick(boolean editMode) {
            if (editMode) {
                mBtnSelectAll.setVisibility(View.VISIBLE);
                mBtnChange.setVisibility(View.VISIBLE);
                mBtnDelete.setVisibility(View.VISIBLE);
                mBtnSelectAll.setText(R.string.select_all);
                mBtnChange.setClickable(false);
                mBtnChange.setTextColor(getResources().getColor(R.color.details_item_bg));
                mBtnDelete.setClickable(false);
                mBtnDelete.setTextColor(getResources().getColor(R.color.details_item_bg));
            } else {
                mBtnSelectAll.setVisibility(View.GONE);
                mBtnChange.setVisibility(View.GONE);
                mBtnDelete.setVisibility(View.GONE);
            }
        }
    };

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mAdapter.notifyDataSetChanged();
            super.onChange(selfChange, uri);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();
    }

    @Override
    protected void onStop() {
        this.getContentResolver().unregisterContentObserver(mObserver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        this.getContentResolver().registerContentObserver(Constant.CONTENT_URI,true,mObserver);
        super.onStart();
    }

    private void init() {
        mBtnSelectAll = (Button) findViewById(R.id.btn_details_select_all);
        mListView = (ListView) findViewById(R.id.listview_details);
        mBtnChange = (Button) findViewById(R.id.btn_details_change);
        mBtnDelete = (Button) findViewById(R.id.btn_details_delete);
        mDB = DataManager.getInstance(this);
        mDataList = mDB.queryAll();
        OldUtils.sortDataBigToSmall(mDataList);
        mAdapter = new DetailsAdapter(this, mDataList, mCallback);
        mListView.setAdapter(mAdapter);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ILog.d(TAG, "onItemClick:" + position);
            }
        });
        mBtnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onSelectAllClick();
            }
        });
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeDialog();
            }
        });
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_details_delete, (ViewGroup) findViewById(R.id.dialog_layout));
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Data> list = mAdapter.getSelectData();
                mDB.delete(list);
                dialog.dismiss();
                mAdapter.exitEditMode();
            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cacnel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View veiw) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showChangeDialog(){
        final ArrayList<Data> list = mAdapter.getSelectData();
        final Data data = list.get(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_main_input_weight, (ViewGroup) findViewById(R.id.dialog_layout));
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) layout.findViewById(R.id.dialog_submit);
        final EditText editText = (EditText) layout.findViewById(R.id.dialog_input_weight);
        editText.setText(data.getWeight()+"");
        editText.setSelection(editText.getText().length());
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float weight = Float.parseFloat(editText.getText().toString());
                    if (weight < 5 || weight > 200) {
                        Toast.makeText(DetailsActivity.this, "您输入的值太不合理了，在逗我玩吧~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    data.setWeight(weight);
                    mDB.update(data);
                    dialog.dismiss();
                    mAdapter.exitEditMode();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DetailsActivity.this, "输入值不合法，请重新输入~", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cacnel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View veiw) {
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
                    InputMethodManager imm = (InputMethodManager) DetailsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        DetailsAdapter adapter = (DetailsAdapter) mListView.getAdapter();
        if (adapter.isEditMode()) {
            adapter.exitEditMode();
            return;
        }
        super.onBackPressed();
    }


}
