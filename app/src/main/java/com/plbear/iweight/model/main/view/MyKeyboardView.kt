package com.plbear.iweight.model.main.view

import android.content.Context
import android.graphics.*
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import com.plbear.iweight.R

/**
 * Created by yanyongjun on 2018/4/9.
 */
class MyKeyboardView(context: Context, attrs: AttributeSet) : KeyboardView(context, attrs) {
    private var mSubmitPaint = Paint()
    private var mSubmitPaintPress = Paint()
    private var mWhitePaint = Paint()
    private var mCancelPaint = Paint()
    private var mCancePressPaint = Paint()
    private var mKeyPaint = Paint()
    private var mKeyPressPaint = Paint()
    private var mBlackPaint = Paint()
    private var mDeleteBitmap = BitmapFactory.decodeResource(resources, R.drawable.delete)
    private var mLinePaint = Paint()

    companion object {
        val TEXT_SIZE = 50f
        val TEXT_SIZE_NUM = 60f
    }

    init {
        mSubmitPaint.color = resources.getColor(R.color.submit)
        mSubmitPaintPress.color = resources.getColor(R.color.submit_pressed)
//        mSubmitPaint.style = Paint.Style.STROKE
//        mSubmitPaint.strokeWidth = 2f

        mWhitePaint.color = resources.getColor(R.color.white)
        mWhitePaint.textSize = TEXT_SIZE

        mCancelPaint.color = resources.getColor(R.color.cancel)
        mCancePressPaint.color = resources.getColor(R.color.cancel_press)

        mKeyPaint.color = resources.getColor(R.color.key_normal)
        mKeyPressPaint.color = resources.getColor(R.color.key_press)

        mBlackPaint.color = resources.getColor(R.color.black)
        mBlackPaint.textSize = TEXT_SIZE_NUM

        var matrix = Matrix()
        matrix.postScale(0.2f, 0.2f)
        mDeleteBitmap = Bitmap.createBitmap(mDeleteBitmap, 0, 0, mDeleteBitmap.width, mDeleteBitmap.height, matrix, true)

        mLinePaint.color = resources.getColor(R.color.details_item_bg)
        mLinePaint.strokeWidth = 2f
        mLinePaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var keys = keyboard.keys
        for (key in keys) {
            if (key.label.equals("保存")) {
                if (key.pressed) {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mSubmitPaintPress)
                } else {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mSubmitPaint)
                }
                canvas?.drawText(key.label.toString(), key.x.toFloat() + key.width / 2 - TEXT_SIZE, key.y.toFloat() + key.height / 2 + TEXT_SIZE / 2 - 10, mWhitePaint)
            } else if (key.label.equals("取消")) {
                if (key.pressed) {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mCancePressPaint)
                } else {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mCancelPaint)
                }
                canvas?.drawText(key.label.toString(), key.x.toFloat() + key.width / 2 - TEXT_SIZE, key.y.toFloat() + key.height / 2 + TEXT_SIZE / 2 - 10, mWhitePaint)
            } else if (!key.label.equals("delete")) {
                if (key.pressed) {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mKeyPressPaint)
                } else {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mKeyPaint)
                }
                canvas?.drawText(key.label.toString(), key.x.toFloat() + key.width / 2 - TEXT_SIZE_NUM / 2 + 10, key.y.toFloat() + key.height / 2 + TEXT_SIZE_NUM / 2 - 10, mBlackPaint)
            } else {
                if (key.pressed) {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mKeyPressPaint)
                } else {
                    canvas?.drawRect(key.x.toFloat(), key.y.toFloat(), (key.x + key.width).toFloat(), (key.y + key.height).toFloat(), mKeyPaint)
                }
                canvas?.drawBitmap(mDeleteBitmap, (key.x + key.width / 2 - mDeleteBitmap.width / 2).toFloat(), (key.y + key.height / 2 - mDeleteBitmap.height/2).toFloat(), null)
            }
            canvas?.drawRect(key.x.toFloat(),key.y.toFloat(),(key.x+key.width).toFloat(),(key.y+key.height).toFloat(),mLinePaint)

        }
    }
}