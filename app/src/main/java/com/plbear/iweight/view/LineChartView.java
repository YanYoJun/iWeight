package com.plbear.iweight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.plbear.iweight.Data.Data;
import com.plbear.iweight.Data.DataManager;
import com.plbear.iweight.R;
import com.plbear.iweight.Utils.ILog;
import com.plbear.iweight.Utils.OldUtils;

import java.util.ArrayList;

/**
 * Created by koakira on 16/11/5.
 */

public class LineChartView extends View {
    private String TAG = "LineChartView:";
    private final int SPACES_COUNT = 5;
    private final int MSG_DATA_CHANGE = 0;
    private final int ALIGN_PADDING_LEFT = 20;
    private final int ALIGN_PADDING_RIGHT = 20;
    private final int ALIGN_PADDING_TOP = 10;
    private final int ALIGN_PADDING_BOTTOM = 40;

    private Paint mBondLinePaint = null;
    private Paint mLinePaint = null;
    private Paint mDottedLinePaint = null;
    private Paint mPolyLinePaint = null;
    private Paint mTextPaint = null;
    private Paint mPointPaint = null;
    private Paint mTargetPaint = null;
    private Paint mTargetTextPaint = null;
    private DataManager mDataManager = null;

    private int mTop = 0;
    private int mLeft = 0;
    private int mRight = 0;
    private int mBottom = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mLineSpacing = 0;
    private int mCurMoveLength = 0;


    private DataAdapter mDataAdapter = null;
    private Context mContext = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DATA_CHANGE:
                    ILog.d(TAG, "Msg_data_change");
                    LineChartView.this.invalidate();
                    break;
                default:
                    break;
            }
        }
    };

    public void setTag(String tag){
        TAG += tag;
        mDataAdapter.setTag(TAG);
    }

    /*public void setDataAdapter(DataAdapter adapter) {
        mDataAdapter = adapter;
        Message msg = mHandler.obtainMessage(MSG_DATA_CHANGE);
        mHandler.sendMessage(msg);
    }*/
    public DataAdapter getDataAdpater() {
        return mDataAdapter;
    }

    public LineChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    /**
     * 初始化画笔工具
     */
    private void initPaints() {
        mBondLinePaint = new Paint();
        mBondLinePaint.setColor(Color.BLACK);
        mBondLinePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mBondLinePaint.setStyle(Paint.Style.STROKE);
        mBondLinePaint.setStrokeWidth(3);
        mBondLinePaint.setAntiAlias(true);
        mBondLinePaint.setAlpha(130);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setAlpha(90);
        mLinePaint.setAntiAlias(true);

        mDottedLinePaint = new Paint();
        mDottedLinePaint.setColor(Color.BLACK);
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setStrokeWidth(3);
        mDottedLinePaint.setAntiAlias(true);
        mDottedLinePaint.setAlpha(150);

        mPolyLinePaint = new Paint();
        mPolyLinePaint.setColor(getResources().getColor(R.color.line_color));
        mPolyLinePaint.setStyle(Paint.Style.STROKE);
        mPolyLinePaint.setStrokeWidth(5);
        mPolyLinePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(38);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAlpha(150);
        mTextPaint.setStrokeWidth(1);

        mPointPaint = new Paint();
        mPointPaint.setColor(getResources().getColor(R.color.point_color));
        mPointPaint.setStrokeWidth(20);

        mTargetPaint = new Paint();
        mTargetPaint.setColor(getResources().getColor(R.color.target_weight));
        mTargetPaint.setStyle(Paint.Style.STROKE);
        mTargetPaint.setStrokeWidth(4);
        mTargetPaint.setAntiAlias(true);

        mTargetTextPaint = new Paint();
        mTargetTextPaint.setColor(getResources().getColor(R.color.target_weight));
        mTargetTextPaint.setTextSize(38);
        mTargetTextPaint.setAntiAlias(true);
        mTargetTextPaint.setStyle(Paint.Style.FILL);
        mTargetTextPaint.setAlpha(150);
        mTargetTextPaint.setStrokeWidth(1);

    }

    private void init() {
        mDataAdapter = new DataAdapter(mContext);
        ILog.e(TAG,"init");
        mDataAdapter.setTag(TAG);
        mDataAdapter.registerDataListener(new DataAdapter.DataChangeListener() {
            @Override
            public void onChange() {
                ILog.d(TAG, "LineChartView data changed");
                Message msg = mHandler.obtainMessage(MSG_DATA_CHANGE);
                mHandler.sendMessage(msg);
            }
        });
        initPaints();
        mDataManager = DataManager.getInstance(mContext);
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
            ILog.d(TAG, "mLeft:" + mLeft + " mRight:" + mRight + " mTop:" + mTop + " mBottom:" + mBottom + " mWeight:" + mWidth + " mHeight:" + mHeight);
            mLineSpacing = (mBottom - mTop - ALIGN_PADDING_BOTTOM - ALIGN_PADDING_TOP) / SPACES_COUNT;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ILog.d(TAG, "onDraw");
        canvas.drawLine(ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP, mRight - ALIGN_PADDING_RIGHT,
                ALIGN_PADDING_TOP, mBondLinePaint);
        for (int i = 1; i < SPACES_COUNT; i++) {
            canvas.drawLine(ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP + i * mLineSpacing, mRight -
                    ALIGN_PADDING_RIGHT, ALIGN_PADDING_TOP + i * mLineSpacing, mLinePaint);
        }
        canvas.drawLine(ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP + SPACES_COUNT * mLineSpacing, mRight -
                ALIGN_PADDING_RIGHT, ALIGN_PADDING_TOP + SPACES_COUNT * mLineSpacing, mBondLinePaint);

        drawDottedLine(canvas);
        drawPolyLine(canvas);
        drawWeightText(canvas);
        drawTimeText(canvas);
        int temp = mDataAdapter.getShowPointCount();
        ILog.e(TAG,"onDraw:"+temp);
        if(mDataAdapter.getShowPointCount() < 50){
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
        float targetWeight = mDataAdapter.getTargetWeight();
        if (targetWeight == -1) {
            ILog.e(TAG, "target weight is not set return");
            return;
        }
        int y = toYPoint(targetWeight);
        Point begin = new Point();
        begin.set(ALIGN_PADDING_TOP,y);
        Point end = new Point();
        end.set(getWidth() - ALIGN_PADDING_RIGHT,y);
        ILog.e(TAG,"begin:"+begin+" end:"+end);
        drawDottedLine(canvas, begin, end, mTargetPaint);
        canvas.drawText(mContext.getResources().getString(R.string.target_weight), end.x - 120, end.y + 40, mTargetTextPaint);
/*        ILog.e(TAG, "drawTargetLine");
        Data begin = new Data();
        begin.setWeight(mDataAdapter.getTargetWeight());
        begin.setTime(mDataAdapter.getTimeSmallest());

        Data end = new Data();
        end.setWeight(mDataAdapter.getTargetWeight());
        end.setTime(mDataAdapter.getTimeBiggest());

        int beginId = mDataAdapter.getShowDataStartId();
        float steps = mWidth / (mDataAdapter.getShowPointCount()-1);
        Point beginPoint = toViewPoint(begin, 0, steps);
        Point endPoint = toViewPoint(end,mDataAdapter.getShowPointCount()-1 , steps);
        drawDottedLine(canvas, beginPoint, endPoint, mTargetPaint);
        ILog.d(TAG, "yanlog drawTargetLine beginPoint:" + beginPoint + " endPoint:" + endPoint);
        ILog.d(TAG, "yanlog targetLine begin:" + begin + " :end:" + end);

        canvas.drawText(mContext.getResources().getString(R.string.target_weight), endPoint.x - 120, endPoint.y + 40, mTargetTextPaint);*/
        /*canvas.drawLine(beginPoint.x, beginPoint.y, endPoint.x, endPoint.y, mTargetPaint);*/
    }

    /**
     * 画各个体重节点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        if (mDataAdapter == null || mDataAdapter.getShowPointCount() == 0) {
            return;
        }
        if (mDataAdapter.getShowDataList().size() == 1) {
/*            canvas.drawCircle(ALIGN_PADDING_LEFT,
                    ALIGN_PADDING_TOP + mHeight / 2 10,mPointPaint);*/
            canvas.drawCircle(ALIGN_PADDING_LEFT,
                    toYPoint(mDataAdapter.getShowDataList().get(0).getWeight()), 10,mPointPaint);
            return;
        }
        //int beginId = mDataAdapter.getShowDataStartId();
        float steps = mWidth / (mDataAdapter.getShowPointCount()-1);
        ArrayList<Data> list = mDataAdapter.getShowDataList();
        int i = 0;
        for (Data data : list) {
            Point point = toViewPoint(data, i++, steps);
            canvas.drawCircle(point.x, point.y, 10, mPointPaint);
        }
    }

    private float mLastTouchPos = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float curTouchPos = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:
                mCurMoveLength += curTouchPos - mLastTouchPos;
                mDataAdapter.notifyDataPosSetChange(mCurMoveLength/mWidth * 7);
                break;
            default:
                break;
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
        if (mDataAdapter == null) {
            return;
        }
        long minTime = mDataAdapter.getTimeSmallest();
        long maxTime = mDataAdapter.getTimeBiggest();
        canvas.drawText(OldUtils.formatTime(minTime), ALIGN_PADDING_LEFT, 40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT,
                mTextPaint);
        canvas.drawText(OldUtils.formatTime((minTime + maxTime) / 2), ALIGN_PADDING_LEFT + mWidth / 2 - 60, 40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT,
                mTextPaint);
        canvas.drawText(OldUtils.formatTime(maxTime), ALIGN_PADDING_LEFT + mWidth - 110, 40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT,
                mTextPaint);

    }

    /**
     * 画y轴上的体重数字
     *
     * @param canvas
     */
    private void drawWeightText(Canvas canvas) {
        if (mDataAdapter == null) {
            return;
        }
        float maxvalue = mDataAdapter.getWeightBiggest();
        float space = mDataAdapter.getHeight() / SPACES_COUNT;
        canvas.drawText(String.format("%.1f", maxvalue), ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP + 40, mTextPaint);
        for (int i = 1; i < SPACES_COUNT; i++) {
            canvas.drawText(String.format("%.1f", maxvalue - i * space),
                    ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP + 40 + i * mLineSpacing, mTextPaint);
        }
    }


    /**
     * 画中间的虚线
     *
     * @param canvas
     */
    private void drawDottedLine(Canvas canvas) {
        Path path = new Path();
        int space = 10;
        path.moveTo(mWidth / 2 + ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP);
        for (int i = 1; i * space < mHeight; i += 2) {
            path.lineTo(mWidth / 2 + ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP + i * space);
            path.moveTo(mWidth / 2 + ALIGN_PADDING_LEFT, ALIGN_PADDING_TOP + (i + 1) * space);
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
    private void drawDottedLine(Canvas canvas, Point begin, Point end, Paint paint) {
        float height = end.y - begin.y;
        float width = end.x - begin.x;
        if (Math.abs(width) > Math.abs(height)) {
            Path path = new Path();
            int space = 10;
            float spaceX = space;

            if (end.x < begin.x) {
                spaceX = -space;
            }
            float spaceY = ((float) spaceX) / width * height;
            path.moveTo(begin.x, begin.y);
            for (int i = 1; i * space < Math.abs(width); i += 2) {
                path.lineTo(begin.x + i * spaceX, begin.y + i * spaceY);
                path.moveTo(begin.x + (i + 1) * spaceX, begin.y + (i + 1) * spaceY);
            }
            canvas.drawPath(path, paint);
        } else {
            Path path = new Path();
            int space = 10;
            float spaceY = space;

            if (end.y < begin.y) {
                spaceY = -space;
            }
            float spaceX = ((float) spaceY) / height * width;
            path.moveTo(begin.x, begin.y);
            for (int i = 1; i * space < Math.abs(height); i += 2) {
                path.lineTo(begin.x + i * spaceX, begin.y + i * spaceY);
                path.moveTo(begin.x + (i + 1) * spaceX, begin.y + (i + 1) * spaceY);
            }
            canvas.drawPath(path, paint);
        }

    }

    /**
     * 画体重的折线
     *
     * @param canvas
     */
    private void drawPolyLine(Canvas canvas) {
        ArrayList<Data> list = mDataAdapter.getShowDataList();
        if (list == null || list.size() == 0 || list.size() == 1) {
            return;
        }
        Path path = new Path();
        int beginsId = mDataAdapter.getShowDataStartId();
        float steps = mWidth / (mDataAdapter.getShowPointCount()-1);
        Point begin = toViewPoint(list.get(0), 0, steps);
        ILog.e(TAG,"drawPolyLine:"+begin.toString()+":"+list.get(0).toString());
        Point end = null;
        path.moveTo(begin.x, begin.y);
        Point fixPoint_1 = new Point();
        Point fixPoint_2 = new Point();
        for (int i = 1; i < list.size(); i++) {
            end = toViewPoint(list.get(i), i, steps);
            ILog.e(TAG,"drawPolyLine:"+end.toString()+":"+list.get(i).toString());
            fixPoint_1.set((begin.x + end.x) / 2, begin.y);
            fixPoint_2.set((begin.x + end.x) / 2, end.y);
            path.cubicTo(fixPoint_1.x, fixPoint_1.y, fixPoint_2.x, fixPoint_2.y, end.x, end.y);
            begin = end;
        }
        canvas.drawPath(path, mPolyLinePaint);
    }

    /**
     * 将各个体重值转换为在view中的坐标值
     *
     * @param data
     * @return
     */
    private Point toViewPoint(Data data, int ids, float timeSteps) {
        if (mDataAdapter == null) {
            return null;
        }
        Point point = new Point();
        point.set(ALIGN_PADDING_LEFT + (int) (ids * timeSteps), ALIGN_PADDING_TOP + (int) (((mDataAdapter.getWeightBiggest() - data.getWeight())
                * (float) mHeight / (mDataAdapter.getHeight()))));
        return point;
    }

    private int toYPoint(float weight){
        return  ALIGN_PADDING_TOP + (int) (((mDataAdapter.getWeightBiggest() - weight)
                * (float) mHeight / (mDataAdapter.getHeight())));
    }

}
