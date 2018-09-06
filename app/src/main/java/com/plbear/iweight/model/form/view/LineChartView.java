package com.plbear.iweight.model.form.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.plbear.iweight.R;
import com.plbear.iweight.data.Data;
import com.plbear.iweight.data.DataManager;
import com.plbear.iweight.model.form.adapter.LineChartAdapter;
import com.plbear.iweight.utils.LogInfo;
import com.plbear.iweight.utils.Utils;

import java.util.ArrayList;

/**
 * Created by yanyongjun on 2018/6/30.
 */

public class LineChartView extends View {
    private String TAG = "LineChartView";
    private final int SPACES_COUNT = 5;
    private final int MSG_DATA_CHANGE = 0;
    private final int ALIGN_PADDING_LEFT = 120;
    private final int ALIGN_PADDING_TEXT_LEFT = 30;
    private final int ALIGN_PADDING_RIGHT = 20;
    private final int ALIGN_PADDING_TOP = 10;
    private final int ALIGN_PADDING_BOTTOM = 40;

    private Paint mBondLinePaint = new Paint();
    private Paint mLinePaint = new Paint();
    private Paint mDottedLinePaint = new Paint();
    private Paint mPolyLinePaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Paint mPointPaint = new Paint();
    private Paint mTargetPaint = new Paint();
    private Paint mTargetTextPaint = new Paint();
    private Paint mPolyLinePathPaint = new Paint();
    private DataManager mDataManager = DataManager.getInstance();

    private int mTop = 0;
    private int mLeft = 0;
    private int mRight = 0;
    private int mBottom = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mLineSpacing = 0;
    private int mCurMoveLength = 0;

    LineChartAdapter dataAdpater = null;
    private Context mContext;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DATA_CHANGE:
                    LineChartView.this.invalidate();
                    break;
            }
        }
    };

    public LineChartAdapter getDataAdpater(){
        return dataAdpater;
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dataAdpater = new LineChartAdapter(mContext);
        init();
    }

    private float mLastTouchPos = 0f;

    private void setTag(String tag) {
        TAG += tag;
        dataAdpater.setTag(TAG);
    }

    /**
     * 初始化画笔工具
     */
    private void initPaints() {
        mBondLinePaint.setColor(Color.BLACK);
        mBondLinePaint.setStyle(Paint.Style.STROKE);
        mBondLinePaint.setStrokeWidth(2f);
        mBondLinePaint.setAntiAlias(true);
        mBondLinePaint.setAlpha(90);

        mLinePaint.setColor(getResources().getColor(R.color.details_item_bg));
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1f);
        mLinePaint.setAlpha(90);
        mLinePaint.setAntiAlias(true);

        mDottedLinePaint.setColor(Color.BLACK);
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setStrokeWidth(3f);
        mDottedLinePaint.setAntiAlias(true);
        mDottedLinePaint.setAlpha(150);

        mPolyLinePaint.setColor(getResources().getColor(R.color.main_data_view_line));
        mPolyLinePaint.setStyle(Paint.Style.STROKE);
        mPolyLinePaint.setStrokeWidth(2f);
        mPolyLinePaint.setAntiAlias(true);

        mPolyLinePathPaint.setColor(getResources().getColor(R.color.main_data_view_path));
        mPolyLinePathPaint.setStyle(Paint.Style.FILL);
        mPolyLinePathPaint.setStrokeWidth(2f);
        mPolyLinePathPaint.setAntiAlias(true);
        mPolyLinePathPaint.setAlpha(120);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(38f);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAlpha(150);
        mTextPaint.setStrokeWidth(1f);

        mPointPaint.setColor(getResources().getColor(R.color.main_data_view_point));
        mPointPaint.setStrokeWidth(5f);

        mTargetPaint.setColor(getResources().getColor(R.color.main_data_view_target_line));
        mTargetPaint.setStyle(Paint.Style.STROKE);
        mTargetPaint.setStrokeWidth(2f);
        mTargetPaint.setAntiAlias(true);
        mTargetPaint.setAlpha(150);

        mTargetTextPaint.setColor(getResources().getColor(R.color.main_data_view_target_line));
        mTargetTextPaint.setTextSize(38f);
        mTargetTextPaint.setAntiAlias(true);
        mTargetTextPaint.setStyle(Paint.Style.FILL);
        mTargetTextPaint.setAlpha(150);
        mTargetTextPaint.setStrokeWidth(1f);
    }

    private void init() {
        dataAdpater.setTag(TAG);
        dataAdpater.registerDataListener(new LineChartAdapter.DataChangeListener() {
            @Override
            public void onChange() {
                Message msg = mHandler.obtainMessage(MSG_DATA_CHANGE);
                mHandler.sendMessage(msg);
            }
        });
        initPaints();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
            mWidth = mRight - mLeft - ALIGN_PADDING_LEFT - ALIGN_PADDING_RIGHT;
            mHeight = mBottom - mTop - ALIGN_PADDING_TOP - ALIGN_PADDING_BOTTOM;
            mLineSpacing = (mBottom - mTop - ALIGN_PADDING_BOTTOM - ALIGN_PADDING_TOP) / SPACES_COUNT;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawLine(ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP, (mRight - ALIGN_PADDING_RIGHT),
//                ALIGN_PADDING_TOP, mBondLinePaint);
        for (int i = 0; i <= SPACES_COUNT; i++) {
            canvas.drawLine(ALIGN_PADDING_LEFT, (ALIGN_PADDING_TOP + i * mLineSpacing), (mRight - ALIGN_PADDING_RIGHT), (ALIGN_PADDING_TOP + i * mLineSpacing),
                    mLinePaint);
        }
        canvas.drawLine(ALIGN_PADDING_LEFT, (ALIGN_PADDING_TOP + SPACES_COUNT * mLineSpacing),
                (mRight - ALIGN_PADDING_RIGHT), (ALIGN_PADDING_TOP + SPACES_COUNT * mLineSpacing), mBondLinePaint);
        drawDottedLine(canvas);
        drawPolyLine(canvas);
        drawWeightText(canvas);
        drawTimeText(canvas);
        if (dataAdpater.getShowPointCount() < 50) {
            drawPoint(canvas);
        }
        drawTargetLine(canvas);
        super.onDraw(canvas);
    }

    /**
     * 画目标体重的线和“目标值”这几个数值
     *
     * @param canvas
     */
    private void drawTargetLine(Canvas canvas) {
        float targetWeight = dataAdpater.getTargetWeight();
        LogInfo.i(TAG,"drawTargetLine:"+targetWeight);
        if (targetWeight <=0) {
            return;
        }
        int y = toYPoint(targetWeight);
        Point begin = new Point();
        begin.set(ALIGN_PADDING_LEFT, y);
        Point end = new Point();
        end.set(getWidth() - ALIGN_PADDING_RIGHT, y);
        drawDottedLine(canvas, begin, end, mTargetPaint);
        canvas.drawText(mContext.getResources().getString(R.string.main_data_view_target_weight), (end.x - 170), (end.y - 25), mTargetTextPaint);
    }

    /**
     * 画各个体重节点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        if(true){
            return;
        }
        if (dataAdpater == null || dataAdpater.getShowPointCount() == 0) {
            return;
        }
        if (dataAdpater.getShowDataList().size() == 1) {
            canvas.drawCircle(ALIGN_PADDING_LEFT,
                    toYPoint(dataAdpater.getShowDataList().get(0).getWeight()), 5f, mPointPaint);
            return;
        }
        //int beginId = mDataAdapter.getShowDataStartId();
        float steps = (mWidth / (dataAdpater.getShowPointCount() - 1));
        ArrayList<Data> list = dataAdpater.getShowDataList();
        int i = 0;
        for (Data data : list) {
            Point point = toViewPoint(data, i++, steps);
            canvas.drawCircle(point.x, point.y, 5f, mPointPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float curTouchPos = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
            }
            case MotionEvent.ACTION_UP: {
            }
            case MotionEvent.ACTION_MOVE: {
                mCurMoveLength += (curTouchPos - mLastTouchPos);
                dataAdpater.notifyDataPosSetChange((mCurMoveLength / mWidth * 7));
                break;
            }
        }
        mLastTouchPos = curTouchPos;
        return super.onTouchEvent(event);
    }

    /**
     * 画图下面的时间
     *
     * @param canvas
     */
    private void drawTimeText(Canvas canvas) {
        long minTime = dataAdpater.getTimeSmallest();
        long maxTime = dataAdpater.getTimeBiggest();
        canvas.drawText(Utils.formatTime(minTime), ALIGN_PADDING_LEFT, (40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT),
                mTextPaint);
        canvas.drawText(Utils.formatTime(maxTime), (ALIGN_PADDING_LEFT + mWidth - 110), (40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT),
                mTextPaint);
    }

    /**
     * 画y轴上的体重数字
     *
     * @param canvas
     */
    private void drawWeightText(Canvas canvas ) {
        if (dataAdpater == null) {
            return;
        }
        float maxvalue = dataAdpater.getWeightBiggest();
        float space = dataAdpater.getHeight() / SPACES_COUNT;
        canvas.drawText(String.format("%.1f", maxvalue), ALIGN_PADDING_TEXT_LEFT, (ALIGN_PADDING_TOP + 18), mTextPaint);
        for (int i = 1;i <= SPACES_COUNT;i++) {
            canvas.drawText(String.format("%.1f", maxvalue - i * space),
                    ALIGN_PADDING_TEXT_LEFT, (ALIGN_PADDING_TOP + 18 + i * mLineSpacing), mTextPaint);
        }
    }

    /**
     * 画中间的虚线
     *
     * @param canvas
     */
    private void drawDottedLine(Canvas canvas ) {
        if(true){
            return;
        }
        Path path = new Path();
        int space = 10;
        path.moveTo((mWidth / 2 + ALIGN_PADDING_LEFT), ALIGN_PADDING_TOP);
        int i = 1;
        while (i * space < mHeight) {
            path.lineTo((mWidth / 2 + ALIGN_PADDING_LEFT), (ALIGN_PADDING_TOP + i * space));
            path.moveTo((mWidth / 2 + ALIGN_PADDING_LEFT), (ALIGN_PADDING_TOP + (i + 1) * space));
            i += 2;
        }
        canvas.drawPath(path, mDottedLinePaint);
    }

    /**
     * 画虚线的一个共性的方法
     *
     * @param canvas
     * @param begin
     * @param end
     * @param paint
     */
    private void drawDottedLine(Canvas canvas , Point begin , Point end , Paint paint) {
        int height = (end.y - begin.y);
        int width = (end.x - begin.x);
        if (Math.abs(width) > Math.abs(height)) {
            Path path = new Path();
            int space = 10;
            int spaceX = space;

            if (end.x < begin.x) {
                spaceX = (-space);
            }
            int spaceY = spaceX / width * height;
            path.moveTo(begin.x, begin.y);
            int i = 1;
            while (i * space < Math.abs(width)) {
                path.lineTo(begin.x + i * spaceX, begin.y + i * spaceY);
                path.moveTo(begin.x + (i + 1) * spaceX, begin.y + (i + 1) * spaceY);
                i += 2;
            }
            canvas.drawPath(path, paint);
        } else {
            Path path = new Path();
            int space = 10;
            int spaceY = space;

            if (end.y < begin.y) {
                spaceY = (-space);
            }
            int spaceX = spaceY / height * width;
            path.moveTo(begin.x, begin.y);
            int i = 1;
            while (i * space < Math.abs(height)) {
                path.lineTo(begin.x + i * spaceX, begin.y + i * spaceY);
                path.moveTo(begin.x + (i + 1) * spaceX, begin.y + (i + 1) * spaceY);
                i += 2;
            }
            canvas.drawPath(path, paint);
        }

    }

    /**
     * 画体重的折线
     *
     * @param canvas
     */
    private void drawPolyLine(Canvas canvas ) {
        ArrayList<Data> list = dataAdpater.getShowDataList();
        if (list.size() == 0 || list.size() == 1) {
            return;
        }
        Path path = new Path();
        int beginsId = dataAdpater.getShowDataStartId();
        int steps = (mWidth / (dataAdpater.getShowPointCount() - 1));
        Point begin = toViewPoint(list.get(0), 0, steps);
        Point beginBegin = begin;
        Point end = null;
        path.moveTo(begin.x, begin.y);
        Point fixPoint_1 = new Point();
        Point fixPoint_2 = new Point();
        for (int i = 1;i<list.size();i++) {
            end = toViewPoint(list.get(i), i, steps);
            fixPoint_1.set((begin.x + end.x) / 2, begin.y);
            fixPoint_2.set((begin.x + end.x) / 2, end.y);
            path.cubicTo(fixPoint_1.x, fixPoint_1.y, fixPoint_2.x, fixPoint_2.y, end.x, end.y);
            begin = end;
        }
        canvas.drawPath(path, mPolyLinePaint);

        path.lineTo(end.x,mHeight+ALIGN_PADDING_TOP);
        path.lineTo(ALIGN_PADDING_LEFT,mHeight+ALIGN_PADDING_TOP);
        path.lineTo(ALIGN_PADDING_LEFT,beginBegin.y);
        canvas.drawPath(path,mPolyLinePathPaint);
    }

    /**
     * 将各个体重值转换为在view中的坐标值
     *
     * @param data
     * @return
     */
    private Point toViewPoint(Data data , int ids, float timeSteps) {
        Point point = new Point();
        point.set((int)(ALIGN_PADDING_LEFT + (ids * timeSteps)),(int)(
                 ALIGN_PADDING_TOP + ((dataAdpater.getWeightBiggest() - data.getWeight()) * mHeight / dataAdpater.getHeight())));
        return point;
    }

    private int toYPoint(float weight) {
        return (int)(ALIGN_PADDING_TOP + ((dataAdpater.getWeightBiggest() - weight) * mHeight / dataAdpater.getHeight()));
    }
}
