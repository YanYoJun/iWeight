package com.plbear.iweight.model.main.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Typeface
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import com.plbear.iweight.data.DataManager
import com.plbear.iweight.R
import com.plbear.iweight.data.Data
import com.plbear.iweight.model.main.adapter.LineChartAdapter
import com.plbear.iweight.utils.MyLog
import com.plbear.iweight.utils.Utils

/**
 * Created by koakira on 16/11/normal_5.
 */

class LineChartView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private var TAG = "LineChartView:"
    private val SPACES_COUNT = 5
    private val MSG_DATA_CHANGE = 0
    private val ALIGN_PADDING_LEFT = 20
    private val ALIGN_PADDING_RIGHT = 20
    private val ALIGN_PADDING_TOP = 10
    private val ALIGN_PADDING_BOTTOM = 40

    private  var mBondLinePaint: Paint = Paint()
    private  var mLinePaint: Paint = Paint()
    private  var mDottedLinePaint: Paint = Paint()
    private  var mPolyLinePaint: Paint = Paint()
    private  var mTextPaint: Paint = Paint()
    private  var mPointPaint: Paint = Paint()
    private  var mTargetPaint: Paint = Paint()
    private  var mTargetTextPaint: Paint = Paint()
    private  var mDataManager: DataManager = DataManager.getInstance()

    private var mTop = 0
    private var mLeft = 0
    private var mRight = 0
    private var mBottom = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mLineSpacing = 0
    private var mCurMoveLength = 0


    /*public void setDataAdapter(LineChartAdapter adapter) {
        mDataAdapter = adapter;
        Message msg = mHandler.obtainMessage(MSG_DATA_CHANGE);
        mHandler.sendMessage(msg);
    }*/
    var dataAdpater = LineChartAdapter()
        private set
    private var mContext: Context? = null
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_DATA_CHANGE -> {
                    MyLog.d(TAG, "Msg_data_change")
                    this@LineChartView.invalidate()
                }
                else -> {
                }
            }
        }
    }

    private var mLastTouchPos = 0f

    fun setTag(tag: String) {
        TAG += tag
        dataAdpater!!.setTag(TAG)
    }

    init {
        mContext = context
        init()
    }

    /**
     * 初始化画笔工具
     */
    private fun initPaints() {
        mBondLinePaint.color = Color.BLACK
        mBondLinePaint.typeface = Typeface.DEFAULT_BOLD
        mBondLinePaint.style = Paint.Style.STROKE
        mBondLinePaint.strokeWidth = 3f
        mBondLinePaint.isAntiAlias = true
        mBondLinePaint.alpha = 130

        mLinePaint.color = Color.BLACK
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = 3f
        mLinePaint.alpha = 90
        mLinePaint.isAntiAlias = true

        mDottedLinePaint.color = Color.BLACK
        mDottedLinePaint.style = Paint.Style.STROKE
        mDottedLinePaint.strokeWidth = 3f
        mDottedLinePaint.isAntiAlias = true
        mDottedLinePaint.alpha = 150

        mPolyLinePaint.color = resources.getColor(R.color.line_color)
        mPolyLinePaint.style = Paint.Style.STROKE
        mPolyLinePaint.strokeWidth = 5f
        mPolyLinePaint.isAntiAlias = true

        mTextPaint.color = Color.BLACK
        mTextPaint.textSize = 38f
        mTextPaint.isAntiAlias = true
        mTextPaint.style = Paint.Style.FILL
        mTextPaint.alpha = 150
        mTextPaint.strokeWidth = 1f

        mPointPaint.color = resources.getColor(R.color.point_color)
        mPointPaint.strokeWidth = 20f

        mTargetPaint.color = resources.getColor(R.color.target_weight)
        mTargetPaint.style = Paint.Style.STROKE
        mTargetPaint.strokeWidth = 4f
        mTargetPaint.isAntiAlias = true

        mTargetTextPaint.color = resources.getColor(R.color.target_weight)
        mTargetTextPaint.textSize = 38f
        mTargetTextPaint.isAntiAlias = true
        mTargetTextPaint.style = Paint.Style.FILL
        mTargetTextPaint.alpha = 150
        mTargetTextPaint.strokeWidth = 1f

    }

    private fun init() {
        MyLog.e(TAG, "init")
        dataAdpater!!.setTag(TAG)
        dataAdpater!!.registerDataListener(object : LineChartAdapter.DataChangeListener {
            override fun onChange() {
                MyLog.d(TAG, "LineChartView data changed")
                val msg = mHandler.obtainMessage(MSG_DATA_CHANGE)
                mHandler.sendMessage(msg)
            }
        })
        initPaints()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mLeft = left
            mTop = top
            mRight = right
            mBottom = bottom
            mWidth = mRight - mLeft - ALIGN_PADDING_LEFT - ALIGN_PADDING_RIGHT
            mHeight = mBottom - mTop - ALIGN_PADDING_TOP - ALIGN_PADDING_BOTTOM
            MyLog.d(TAG, "mLeft:$mLeft mRight:$mRight mTop:$mTop mBottom:$mBottom mWeight:$mWidth mHeight:$mHeight")
            mLineSpacing = (mBottom - mTop - ALIGN_PADDING_BOTTOM - ALIGN_PADDING_TOP) / SPACES_COUNT
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawLine(ALIGN_PADDING_LEFT.toFloat(), ALIGN_PADDING_TOP.toFloat(), (mRight - ALIGN_PADDING_RIGHT).toFloat(),
                ALIGN_PADDING_TOP.toFloat(), mBondLinePaint)
        for (i in 1 until SPACES_COUNT) {
            canvas.drawLine(ALIGN_PADDING_LEFT.toFloat(), (ALIGN_PADDING_TOP + i * mLineSpacing).toFloat(), (mRight - ALIGN_PADDING_RIGHT).toFloat(), (ALIGN_PADDING_TOP + i * mLineSpacing).toFloat(), mLinePaint!!)
        }
        canvas.drawLine(ALIGN_PADDING_LEFT.toFloat(), (ALIGN_PADDING_TOP + SPACES_COUNT * mLineSpacing).toFloat(), (mRight - ALIGN_PADDING_RIGHT).toFloat(), (ALIGN_PADDING_TOP + SPACES_COUNT * mLineSpacing).toFloat(), mBondLinePaint!!)
        drawDottedLine(canvas)
        drawPolyLine(canvas)
        drawWeightText(canvas)
        drawTimeText(canvas)
        val temp = dataAdpater!!.showPointCount
        if (dataAdpater!!.showPointCount < 50) {
            drawPoint(canvas)
        }
        drawTargetLine(canvas)

        super.onDraw(canvas)
    }


    /**
     * 画目标体重的线和“目标值”这几个数值
     *
     * @param canvas
     */
    private fun drawTargetLine(canvas: Canvas) {
        val targetWeight = dataAdpater!!.targetWeight
        if (targetWeight == -1f) {
            MyLog.e(TAG, "target weight is not set return")
            return
        }
        val y = toYPoint(targetWeight)
        val begin = Point()
        begin.set(ALIGN_PADDING_TOP, y)
        val end = Point()
        end.set(width - ALIGN_PADDING_RIGHT, y)
        MyLog.e(TAG, "begin:$begin end:$end")
        drawDottedLine(canvas, begin, end, mTargetPaint)
        canvas.drawText(mContext!!.resources.getString(R.string.target_weight), (end.x - 120).toFloat(), (end.y + 40).toFloat(), mTargetTextPaint!!)
    }

    /**
     * 画各个体重节点
     *
     * @param canvas
     */
    private fun drawPoint(canvas: Canvas) {
        if (dataAdpater == null || dataAdpater!!.showPointCount == 0) {
            return
        }
        if (dataAdpater!!.showDataList.size == 1) {
            /*            canvas.drawCircle(ALIGN_PADDING_LEFT,
                    ALIGN_PADDING_TOP + mHeight / normal_2 10,mPointPaint);*/
            canvas.drawCircle(ALIGN_PADDING_LEFT.toFloat(),
                    toYPoint(dataAdpater!!.showDataList[0].weight).toFloat(), 10f, mPointPaint!!)
            return
        }
        //int beginId = mDataAdapter.getShowDataStartId();
        val steps = (mWidth / (dataAdpater!!.showPointCount - 1)).toFloat()
        val list = dataAdpater!!.showDataList
        var i = 0
        for (data in list) {
            val point = toViewPoint(data, i++, steps)
            canvas.drawCircle(point!!.x.toFloat(), point.y.toFloat(), 10f, mPointPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val curTouchPos = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_MOVE -> {
                mCurMoveLength += (curTouchPos - mLastTouchPos).toInt()
                dataAdpater!!.notifyDataPosSetChange((mCurMoveLength / mWidth * 7).toFloat())
            }
            else -> {
            }
        }
        mLastTouchPos = curTouchPos
        return super.onTouchEvent(event)
    }

    /**
     * 画图下面的时间
     *
     * @param canvas
     */
    private fun drawTimeText(canvas: Canvas) {
        val minTime = dataAdpater.timeSmallest
        val maxTime = dataAdpater.timeBiggest
        canvas.drawText(Utils.formatTime(minTime), ALIGN_PADDING_LEFT.toFloat(), (40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT).toFloat(),
                mTextPaint)
        canvas.drawText(Utils.formatTime((minTime + maxTime) / 2), (ALIGN_PADDING_LEFT + mWidth / 2 - 60).toFloat(), (40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT).toFloat(),
                mTextPaint)
        canvas.drawText(Utils.formatTime(maxTime), (ALIGN_PADDING_LEFT + mWidth - 110).toFloat(), (40 + ALIGN_PADDING_TOP + mLineSpacing * SPACES_COUNT).toFloat(),
                mTextPaint)

    }

    /**
     * 画y轴上的体重数字
     *
     * @param canvas
     */
    private fun drawWeightText(canvas: Canvas) {
        if (dataAdpater == null) {
            return
        }
        val maxvalue = dataAdpater!!.weightBiggest
        val space = dataAdpater!!.height / SPACES_COUNT
        canvas.drawText(String.format("%.1f", maxvalue), ALIGN_PADDING_LEFT.toFloat(), (ALIGN_PADDING_TOP + 40).toFloat(), mTextPaint)
        for (i in 1 until SPACES_COUNT) {
            canvas.drawText(String.format("%.1f", maxvalue - i * space),
                    ALIGN_PADDING_LEFT.toFloat(), (ALIGN_PADDING_TOP + 40 + i * mLineSpacing).toFloat(), mTextPaint)
        }
    }


    /**
     * 画中间的虚线
     *
     * @param canvas
     */
    private fun drawDottedLine(canvas: Canvas) {
        val path = Path()
        val space = 10
        path.moveTo((mWidth / 2 + ALIGN_PADDING_LEFT).toFloat(), ALIGN_PADDING_TOP.toFloat())
        var i = 1
        while (i * space < mHeight) {
            path.lineTo((mWidth / 2 + ALIGN_PADDING_LEFT).toFloat(), (ALIGN_PADDING_TOP + i * space).toFloat())
            path.moveTo((mWidth / 2 + ALIGN_PADDING_LEFT).toFloat(), (ALIGN_PADDING_TOP + (i + 1) * space).toFloat())
            i += 2
        }
        canvas.drawPath(path, mDottedLinePaint!!)
    }

    /**
     * 画虚线的一个共性的方法
     *
     * @param canvas
     * @param begin
     * @param end
     * @param paint
     */
    private fun drawDottedLine(canvas: Canvas, begin: Point, end: Point, paint: Paint?) {
        val height = (end.y - begin.y).toFloat()
        val width = (end.x - begin.x).toFloat()
        if (Math.abs(width) > Math.abs(height)) {
            val path = Path()
            val space = 10
            var spaceX = space.toFloat()

            if (end.x < begin.x) {
                spaceX = (-space).toFloat()
            }
            val spaceY = spaceX.toFloat() / width * height
            path.moveTo(begin.x.toFloat(), begin.y.toFloat())
            var i = 1
            while (i * space < Math.abs(width)) {
                path.lineTo(begin.x + i * spaceX, begin.y + i * spaceY)
                path.moveTo(begin.x + (i + 1) * spaceX, begin.y + (i + 1) * spaceY)
                i += 2
            }
            canvas.drawPath(path, paint)
        } else {
            val path = Path()
            val space = 10
            var spaceY = space.toFloat()

            if (end.y < begin.y) {
                spaceY = (-space).toFloat()
            }
            val spaceX = spaceY.toFloat() / height * width
            path.moveTo(begin.x.toFloat(), begin.y.toFloat())
            var i = 1
            while (i * space < Math.abs(height)) {
                path.lineTo(begin.x + i * spaceX, begin.y + i * spaceY)
                path.moveTo(begin.x + (i + 1) * spaceX, begin.y + (i + 1) * spaceY)
                i += 2
            }
            canvas.drawPath(path, paint!!)
        }

    }

    /**
     * 画体重的折线
     *
     * @param canvas
     */
    private fun drawPolyLine(canvas: Canvas) {
        val list = dataAdpater.showDataList
        if (list.size == 0 || list.size == 1) {
            return
        }
        val path = Path()
        val beginsId = dataAdpater.showDataStartId
        val steps = (mWidth / (dataAdpater.showPointCount - 1)).toFloat()
        var begin = toViewPoint(list[0], 0, steps)
        var end: Point? = null
        path.moveTo(begin.x.toFloat(), begin.y.toFloat())
        val fixPoint_1 = Point()
        val fixPoint_2 = Point()
        for (i in 1 until list.size) {
            end = toViewPoint(list[i], i, steps)
            fixPoint_1.set((begin.x + end.x) / 2, begin.y)
            fixPoint_2.set((begin.x + end.x) / 2, end.y)
            path.cubicTo(fixPoint_1.x.toFloat(), fixPoint_1.y.toFloat(), fixPoint_2.x.toFloat(), fixPoint_2.y.toFloat(), end.x.toFloat(), end.y.toFloat())
            begin = end
        }
        canvas.drawPath(path, mPolyLinePaint!!)
    }

    /**
     * 将各个体重值转换为在view中的坐标值
     *
     * @param data
     * @return
     */
    private fun toViewPoint(data: Data, ids: Int, timeSteps: Float): Point {
        val point = Point()
        point.set(ALIGN_PADDING_LEFT + (ids * timeSteps).toInt(), ALIGN_PADDING_TOP + ((dataAdpater.weightBiggest - data.weight) * mHeight.toFloat() / dataAdpater.height).toInt())
        return point
    }

    private fun toYPoint(weight: Float): Int {
        return ALIGN_PADDING_TOP + ((dataAdpater.weightBiggest - weight) * mHeight.toFloat() / dataAdpater.height).toInt()
    }

}
