package com.phicode.xm.liquidview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class LiquidView extends View {
    private static final int DEFAULT_AMPLITUDE = 10;
    private static final double DEFAULT_OMEGA_RATIO = 1;
    private static final double DEFAULT_AMPLITUDE_RATIO = 0.05;
    private static final double DEFAULT_PHI_RATIO = 0.5;
    private static final double DEFAULT_LENGTH_RATIO = 0.5;
    private static final int WATER_WIDTH = 40;

    private int mWidth;
    private int mHeight;
    private int mBigCircleHeight;

    private Paint mWaterFallPaint;
    //大圆画笔
    private Paint mBigCirclePaint;
    //波浪画笔
    private Paint mWavePaint;

    private Path mWavePath;
    private Path mBigCirclePath;

    //y=Asin(ωx+φ)+h
    private double mOmega;
    private double mPhi;
    private double mAmplitude = DEFAULT_AMPLITUDE;
    private double mLength = 0;
    private double mOmegaRatio = 2;
    private double mPhiDiff = 0;
    private double mLengthDiff = 0;

    private int mWaterFallDelta = 0;
    private int mWaterDismissDelta = 0;

    public LiquidView(Context context) {
        this(context, null);
    }

    public LiquidView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiquidView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mWaterFallPaint = new Paint();
        mWaterFallPaint.setAntiAlias(true);
        mWaterFallPaint.setDither(true);
        mWaterFallPaint.setStyle(Paint.Style.FILL);
        mWaterFallPaint.setColor(Color.RED);

        mBigCirclePaint = new Paint();
        mBigCirclePaint.setColor(Color.RED);
        mBigCirclePaint.setDither(true);
        mBigCirclePaint.setAntiAlias(true);
        mBigCirclePaint.setStyle(Paint.Style.FILL);

        mWavePaint = new Paint();
        mWavePaint.setDither(true);
        mWavePaint.setAntiAlias(true);
        mWavePaint.setColor(Color.GREEN);
        mWavePaint.setStyle(Paint.Style.STROKE);
        mWavePaint.setStrokeWidth(2);

        mWavePath = new Path();
        mBigCirclePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        mBigCircleHeight = getHeight() / 2;
        mBigCirclePath.reset();
        mBigCirclePath.addCircle(mWidth / 2, 3 * mHeight / 4, mWidth / 2, Path.Direction.CW);
        if (mLength >= -mAmplitude) {
            drawWaterFall(canvas);
        }
        if (mWaterFallDelta > mHeight - 10) {
            drawWave(mBigCirclePath, canvas);
            if (mLength < -mAmplitude) {
                drawWaterDismiss(canvas);
            }
        }

    }

    private void drawWaterDismiss(Canvas canvas) {
        int top = mWaterDismissDelta;
        RectF rectF = new RectF(mWidth / 2 - WATER_WIDTH / 2, top, mWidth / 2 + WATER_WIDTH / 2, mHeight - 10);
        mWaterFallPaint.setColor(Color.RED);
        canvas.drawRect(rectF, mWaterFallPaint);
        mWaterDismissDelta += 50;
        if (mWaterDismissDelta <= mHeight - 10) {
            postInvalidate();
        }
    }

    private void drawWaterFall(Canvas canvas) {
        int bottom = mWaterFallDelta;
        RectF rectF = new RectF(mWidth / 2 - WATER_WIDTH / 2, 0, mWidth / 2 + WATER_WIDTH / 2, bottom);
        canvas.drawRect(rectF, mWaterFallPaint);
        mWaterFallDelta += 50;
        if (mWaterFallDelta <= mHeight - 10) {
            postInvalidate();
        }
    }

    private void drawWave(Path path, Canvas canvas) {
        mOmega = 2.0f * Math.PI / mWidth * DEFAULT_OMEGA_RATIO * mOmegaRatio;
        mPhi = mWidth * DEFAULT_PHI_RATIO + mPhiDiff;
        mAmplitude = mBigCircleHeight * DEFAULT_AMPLITUDE_RATIO;
        mLength = mHeight + mAmplitude / 2 - mLengthDiff;
        final int endX = mWidth;
        mWavePath.reset();
        mWavePath.moveTo(0, mHeight);
        mWavePath.lineTo(0, (float) (mAmplitude * Math.sin(mOmega * 0 + mPhi) + mLength));
        for (int beginX = 1; beginX < endX; beginX += 2) {
            float beginY = (float) (mAmplitude * Math.sin(mOmega * beginX + mPhi) + mLength);
            mWavePath.lineTo(beginX, beginY);
        }
        mWavePath.lineTo(mWidth, (float) (mAmplitude * Math.sin(mOmega * endX + mPhi) + mLength));
        mWavePath.lineTo(mWidth, mHeight);
        mWavePath.close();
        path.op(mWavePath, Path.Op.INTERSECT);
        canvas.drawPath(path, mBigCirclePaint);
        //canvas.drawPath(mWavePath, mWavePaint);
        mLengthDiff += mBigCircleHeight / 200;
        mOmegaRatio = mLengthDiff <= (mBigCircleHeight / 2) ? 1.4 - (0.6 * mLengthDiff / mBigCircleHeight) * 2
                : 1.4 - (0.6 * (mBigCircleHeight - mLengthDiff) / mBigCircleHeight) * 2;
        mPhiDiff += mOmega * 30 * (1 - (0.9 * mLengthDiff / mBigCircleHeight));
        if (mLength >= -mAmplitude) {
            postInvalidate();
        }
    }
}

