package com.plbear.iweight.model.details.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plbear.iweight.R;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsRecyclerAdapter extends RecyclerView.Adapter<DetailsRecyclerAdapter.MyViewHolder> {
    private static final String TAG = DetailsRecyclerAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_EIDTMODE = 1;
    private static final int VIEW_TYPE_NORMAL = 0;
    private Context mContext = null;
    private ArrayList<Data> mListData = null;
    public boolean isEditMode = false;
    private LayoutInflater mInflater = null;

    public DetailsRecyclerAdapter(Context context, ArrayList<Data> listData) {
        mContext = context;
        mListData = listData;
        mInflater = LayoutInflater.from(mContext);
    }

    private int mChangePos = -1;
    public void setChangePos(int pos) {
        mChangePos = pos;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LogInfo.e(TAG,"onBindViewHolder"+holder+" "+position);
        Data data = mListData.get(position);
        holder.lab_details_item_date.setText(Utils.formatTimeFull(data.getTime()));
        holder.lab_details_item_weight.setText("" + data.getWeight());
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        LogInfo.e(TAG,"onViewRecycled:"+holder);
        super.onViewRecycled(holder);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogInfo.e(TAG,"onCreateViewHolder:"+viewType);
        //new Exception("onCreateViewHolder").printStackTrace();
        switch (viewType){
            case VIEW_TYPE_EIDTMODE: {
                View v = mInflater.inflate(R.layout.item_details_editmode,null);
                MyViewHolder holder = new MyViewHolder(v);
                holder.viewType = VIEW_TYPE_EIDTMODE;
                return holder;
            }
            case VIEW_TYPE_NORMAL:{
                View v = mInflater.inflate(R.layout.item_details,null);
                MyViewHolder holder = new MyViewHolder(v);
                holder.viewType = VIEW_TYPE_NORMAL;
                return holder;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = mListData.get(position).isEditMode() ? VIEW_TYPE_EIDTMODE: VIEW_TYPE_NORMAL;
        LogInfo.e(TAG,"getItemViewType"+type);
        return type;
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_details_delete, null);
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
        final View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_main_input_weight, null);
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

    public void exitEditMode() {
        for (Data data : mListData) {
            data.setEditMode(false);
        }
        notifyDataChanged();
    }


    public void notifyDataChanged(){
        DataManager db = DataManager.getInstance(mContext);
        ArrayList<Data> newList = db.queryAll();
        Utils.mergeWeightData(mListData, newList);
        Utils.sortDataBigToSmall(mListData);
        super.notifyDataSetChanged();
    }


    public ArrayList<Data> getListData(){
        return mListData;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        int viewType = VIEW_TYPE_NORMAL;
        @BindView(R.id.lab_details_item_date) TextView lab_details_item_date;
        @BindView(R.id.lab_details_item_weight) TextView lab_details_item_weight;
        @BindView(R.id.btn_delete) Button btn_delete;
        @BindView(R.id.btn_change) Button btn_change;

        public MyViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
            initView();
        }

        public void initView() {
            btn_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangeDialog();
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
                }
            });
        }
    }
}
