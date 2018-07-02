package com.plbear.iweight.model.main.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.plbear.iweight.R;

import java.util.List;

/**
 * Created by yanyongjun on 2018/6/27.
 */

public class MyKeyboradView extends KeyboardView {
    private final static float TEXT_SIZE = 50f;
    private final static float TEXT_SIZE_NUM = 60f;
    private Paint mSubmitPaint = new Paint();
    private Paint mSubmitPaintPress = new Paint();
    private Paint mWhitePaint = new Paint();
    private Paint mCancelPaint = new Paint();
    private Paint mCancePressPaint = new Paint();
    private Paint mKeyPaint = new Paint();
    private Paint mKeyPressPaint = new Paint();
    private Paint mBlackPaint = new Paint();
    private Bitmap mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard_delete);
    private Paint mLinePaint = new Paint();

    public MyKeyboradView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = getResources();
        mSubmitPaint.setColor(resources.getColor(R.color.keyboard_key_submit_normal));
        mSubmitPaintPress.setColor(resources.getColor(R.color.keyboard_key_submit_pressed));
//        mSubmitPaint.style = Paint.Style.STROKE
//        mSubmitPaint.strokeWidth = 2f

        mWhitePaint.setColor( resources.getColor(R.color.white));
        mWhitePaint.setTextSize(TEXT_SIZE);

        mCancelPaint.setColor(resources.getColor(R.color.keyboard_key_cancel_normal));
        mCancePressPaint.setColor(resources.getColor(R.color.keyboard_key_cancel_pressed));

        mKeyPaint.setColor(resources.getColor(R.color.keyboard_key_normal));
        mKeyPressPaint.setColor(resources.getColor(R.color.keyboard_key_pressed));

        mBlackPaint.setColor(resources.getColor(R.color.black));
        mBlackPaint.setTextSize(TEXT_SIZE_NUM);

        Matrix matrix = new Matrix();
        matrix.postScale(0.2f, 0.2f);
        mDeleteBitmap = Bitmap.createBitmap(mDeleteBitmap, 0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight(), matrix, true);

        mLinePaint.setColor(resources.getColor(R.color.details_item_bg));
        mLinePaint.setStrokeWidth( 2f);
        mLinePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            if (key.label.equals("保存")) {
                if (key.pressed) {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mSubmitPaintPress);
                } else {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mSubmitPaint);
                }
                canvas.drawText(key.label.toString(), key.x + key.width / 2 - TEXT_SIZE, key.y + key.height / 2 + TEXT_SIZE / 2 - 10, mWhitePaint);
            } else if (key.label.equals("取消")) {
                if (key.pressed) {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mCancePressPaint);
                } else {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mCancelPaint);
                }
                canvas.drawText(key.label.toString(), key.x + key.width / 2 - TEXT_SIZE, key.y + key.height / 2 + TEXT_SIZE / 2 - 10, mWhitePaint);
            } else if (!key.label.equals("回退")) {
                if (key.pressed) {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mKeyPressPaint);
                } else {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mKeyPaint);
                }
                canvas.drawText(key.label.toString(), key.x + key.width / 2 - TEXT_SIZE_NUM / 2 + 10, key.y + key.height / 2 + TEXT_SIZE_NUM / 2 - 10, mBlackPaint);
            } else {
                if (key.pressed) {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mKeyPressPaint);
                } else {
                    canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mKeyPaint);
                }
                canvas.drawBitmap(mDeleteBitmap, (key.x + key.width / 2 - mDeleteBitmap.getWidth() / 2), (key.y + key.height / 2 - mDeleteBitmap.getHeight() / 2), null);
            }
            canvas.drawRect(key.x, key.y, (key.x + key.width), (key.y + key.height), mLinePaint);

        }
    }
}
