package com.plbear.iweight.model.details;

import android.app.AlertDialog;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Handler;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.base.Constant;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by yanyongjun on 2018/6/28.
 */

public class DetailsActivity extends BaseActivity {
    @BindView(R.id.btn_details_select_all)
    Button btn_details_select_all;

    @BindView(R.id.btn_details_change)
    Button btn_details_change;

    @BindView(R.id.btn_details_delete)
    Button btn_details_delete;

    @BindView(R.id.listview_details)
    ListView listView_details;

    private DetailsAdapter mAdapter;
    private ArrayList<Data> mDataList;
    private DetailsAdapter.OnItemClick mCallback = new DetailsAdapter.OnItemClick() {
        @Override
        public void itemClick(@NotNull HashMap<Integer, Boolean> mSelectMap, int selectCount) {
            int size = mDataList.size();
            if (size != selectCount) {
                btn_details_select_all.setText(R.string.details_title_select_all);
            } else {
                btn_details_select_all.setText(R.string.details_title_disselect_all);
            }
            if (selectCount == 1) {
                btn_details_change.setClickable(true);
                btn_details_change.setTextColor(Color.BLACK);
            } else {
                btn_details_change.setClickable(false);
                btn_details_change.setTextColor(getResources().getColor(R.color.details_item_bg));
            }
            if (selectCount > 0) {
                btn_details_delete.setClickable(true);
                btn_details_delete.setTextColor(Color.BLACK);
            } else {
                btn_details_delete.setClickable(false);
                btn_details_delete.setTextColor(getResources().getColor(R.color.details_item_bg));
            }
        }

        @Override
        public void longItemClick(boolean editMode) {
            if (editMode) {
                btn_details_select_all.setVisibility(View.VISIBLE);
                btn_details_change.setVisibility(View.VISIBLE);
                btn_details_delete.setVisibility(View.VISIBLE);
                btn_details_select_all.setText(R.string.details_title_select_all);
                btn_details_change.setClickable(false);
                btn_details_change.setTextColor(getResources().getColor(R.color.details_item_bg));
                btn_details_delete.setClickable(false);
                btn_details_delete.setTextColor(getResources().getColor(R.color.details_item_bg));
            } else {
                btn_details_change.setVisibility(View.GONE);
                btn_details_delete.setVisibility(View.GONE);
            }
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
        return R.layout.activity_details;
    }

    @Override
    public void afterLayout() {
        init();
    }

    @Override
    protected void onStop() {
        getContentResolver().unregisterContentObserver(mObserver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        getContentResolver().registerContentObserver(Constant.CONTENT_URI, true, mObserver);
        super.onStart();
    }

    private void init() {
        mDataList = DataManager.getInstance().queryAll();
        Utils.sortDataBigToSmall(mDataList);
        mAdapter = new DetailsAdapter(this, mDataList, mCallback);
        listView_details.setAdapter(mAdapter);
        listView_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        btn_details_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isEditMode) {
                    mAdapter.onSelectAllClick();
                } else {
                    mAdapter.enterEditMode();
                }
            }
        });

        btn_details_select_all.setText("编辑");
        btn_details_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeDialog();
            }
        });
        btn_details_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
    }

    void onClick_back(View v) {
        finish();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = getLayoutInflater().inflate(R.layout.dialog_details_delete, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        Button btnSubmit = (Button) findViewById(R.id.dialog_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Data> list = mAdapter.getSelectData();
                DataManager.getInstance().delete(list);
                dialog.dismiss();
                mAdapter.exitEditMode();
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
        ArrayList<Data> list = mAdapter.getSelectData();
        final Data data = list.get(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View layout = getLayoutInflater().inflate(R.layout.dialog_main_input_weight, null);
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
                    mAdapter.exitEditMode();
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast("请检查输入的值");
                }

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
                            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            input.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                        }
                    }
                }, 200);

            }
        });
    }

     public void onBackPressed() {
         DetailsAdapter adapter = (DetailsAdapter) listView_details.getAdapter();
        if(adapter.isEditMode){
            adapter.exitEditMode();
            btn_details_select_all.setText("编辑");
            return;
        }
        super.onBackPressed();
    }

}
