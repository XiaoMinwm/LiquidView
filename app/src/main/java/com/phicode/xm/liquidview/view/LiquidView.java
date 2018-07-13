package com.phicode.xm.liquidview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

import com.phicode.xm.liquidview.BubbleGenerator;
import com.phicode.xm.liquidview.model.Bubble;
import com.phicode.xm.liquidview.util.CommonUtil;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LiquidView extends View {
    private static final String TAG = "LiquidView";
    //水波正弦参数
    private static final int DEFAULT_AMPLITUDE = 10;
    private static final double DEFAULT_OMEGA_RATIO = 0.8;
    private static final double DEFAULT_AMPLITUDE_RATIO = 0.05;
    private static final double DEFAULT_PHI_RATIO = 0.5;
    private static final double DEFAULT_LENGTH_RATIO = 0.5;
    //水注宽度
    private static final int WATER_WIDTH = 40;
    //抖动距离
    private static final int SHAKE_HEIGHT = 150;
    //动画初始到结束颜色
    private static final String BEGIN_COLOR = "#FFFF4500";
    public static final String MIDDLE_COLOR = "#FFFFD700";
    public static final String END_COLOR = "#FF1BCC48";

    private static final int DEFAULT_BUBBLE_BOUNCE_HEIGHT = 150;

    private int mWidth;
    private int mHeight;
    private int mBigCircleHeight;

    private int mColorDiff;

    private Paint mWaterFallPaint;
    //大圆画笔
    private Paint mBigCirclePaint;
    //波浪画笔
    private Paint mWavePaint;
    //气泡画笔
    private Paint mBubblePaint;
    //对勾画笔
    private Paint mTickPaint;

    private Path mWavePath;
    private Path mBigCirclePath;
    private Path mTickPath;
    private PathMeasure mTickPathMeasure;

    //y=Asin(ωx+φ)+h
    private double mOmega;
    private double mPhi;
    private double mAmplitude = DEFAULT_AMPLITUDE;
    private double mLength = 0;
    private double mOmegaRatio = 2;
    private double mPhiDiff = 0;
    private double mLengthDiff = 0;
    private double mWaterFillRatio = 0;
    private int mCurrentColor;

    //水柱下落和消失参数
    private int mWaterFallDelta = 0;
    private int mWaterDismissDelta = 0;

    //view的抖动效果
    private float mShakeAnimatorValue = 0;
    private float mTickAnimatorValue = 0;

    private LiquidState mState;
    private List<Bubble> mBubbles;
    private BubbleGenerator mBubbleGenerator = new BubbleGenerator();

    private ValueAnimator mBubbleAnimator = ValueAnimator.ofFloat(0, 1);
    private ValueAnimator mShakeAnimator = ValueAnimator.ofFloat(0, 1);
    private ValueAnimator mTickAnimator = ValueAnimator.ofFloat(0, 1);

    public LiquidView(Context context) {
        this(context, null);
    }

    public LiquidView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiquidView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        init();
    }

    private Handler mHandler = new Handler(Looper.myLooper());

    private void init() {
        mBubbleAnimator.setDuration(700);
        mBubbleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mBubbleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                if (mBubbles == null) {
                    return;
                }
                for (Bubble bubble : mBubbles) {
                    bubble.setAlpha((int) (255 * (1 - value)));
                    bubble.setBounceHeight(DEFAULT_BUBBLE_BOUNCE_HEIGHT * value);
                }
                postInvalidate();
            }
        });
        mShakeAnimator.setDuration(700);
        mShakeAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        mShakeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mShakeAnimatorValue = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });

        mBubbleGenerator.setBubbleCallback(new BubbleGenerator.BubbleCallback() {
            @Override
            public void generatorBubble(List<Bubble> bubbles) {
                mBubbles = bubbles;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBubbleAnimator.start();
                    }
                });
            }
        });
        mBubbleGenerator.start();

        mTickAnimator.setDuration(1000);
        mTickAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        mTickAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTickAnimatorValue = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
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

        mBubblePaint = new Paint();
        mBubblePaint.setDither(true);
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setStyle(Paint.Style.FILL);

        mTickPaint = new Paint();
        mTickPaint.setDither(true);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setStrokeJoin(Paint.Join.ROUND);
        mTickPaint.setColor(Color.WHITE);
        mTickPaint.setStrokeWidth(20);

        mWavePath = new Path();
        mBigCirclePath = new Path();
        mTickPath = new Path();
        mTickPathMeasure = new PathMeasure();

        mState = LiquidState.WATER_FALL;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        switch (mState) {
            case WATER_FALL:
                drawWaterFall(canvas);
                break;
            case LIQUID_FILL:
                drawWaterFall(canvas);
                drawWave(mBigCirclePath, canvas);
                drawBubble(canvas);
                break;
            case WATER_DISMISS:
                mBubbleGenerator.setGenerate(false);
                drawWave(mBigCirclePath, canvas);
                drawWaterDismiss(canvas);
                break;
            case CIRCLE_ANIMATE:
                drawBubble(canvas);
                shakeView(canvas);
                drawWave(mBigCirclePath, canvas);
                break;
            case MAKE_TICK:
                shakeView(canvas);
                shrinkView(canvas);
                drawWave(mBigCirclePath, canvas);
                drawTick(canvas);
                break;
        }
    }

    private void shrinkView(Canvas canvas) {
        if (mTickAnimatorValue < 0.5) {
            return;
        }
        canvas.scale((1f - (mTickAnimatorValue > 1 ? 1f : mTickAnimatorValue)) / 4f + 0.875f, (1f - (mTickAnimatorValue > 1 ? 1f : mTickAnimatorValue)) / 4f + 0.875f, mWidth / 2, mHeight - mBigCircleHeight / 2);
    }

    private void drawTick(Canvas canvas) {
        float tickLeftLength = (mWidth / 2f / 2.4f);
        mTickPath.moveTo(tickLeftLength * 1.4f, mHeight - mBigCircleHeight / 2);
        mTickPath.lineTo(tickLeftLength * (1.4f + (float) Math.sqrt(2) / 2f), (float) (mHeight - mBigCircleHeight / 2f + tickLeftLength * Math.sqrt(2) / 2f));
        mTickPath.lineTo((float) (tickLeftLength * (1.4f + (float) Math.sqrt(2) / 2f) + tickLeftLength * 2f * Math.sin(50f / 180f * Math.PI)), (float) (mHeight - mBigCircleHeight / 2f + tickLeftLength * Math.sqrt(2) / 2f) - (float) (tickLeftLength * 2f * Math.cos(50f / 180f * Math.PI)));
        Path dst = new Path();
        mTickPathMeasure.setPath(mTickPath, false);
        mTickPathMeasure.getSegment(0, mTickPathMeasure.getLength() * mTickAnimatorValue * 0.9f, dst, true);
        canvas.drawPath(dst, mTickPaint);
    }

    private void shakeView(Canvas canvas) {
        canvas.translate(0, -mShakeAnimatorValue * SHAKE_HEIGHT);
    }

    private void drawBubble(Canvas canvas) {
        if (!mBubbleGenerator.isInstantAndOnce() && (mLength > mHeight || mLength < mHeight / 2.0 || mBubbles == null)) {
            return;
        }
        float deltaH;
        if (mBubbleGenerator.isInstantAndOnce()) {
            deltaH = mWidth / 2f - 10f;
        } else {
            deltaH = (float) Math.abs((mWidth / 2.0 - (mHeight - mLength)));
        }
        float bubbleRange = (float) Math.sqrt((mWidth / 2.0) * (mWidth / 2.0) - deltaH * deltaH);
        for (Bubble bubble : mBubbles) {
            float bubbleX = bubble.getXMiddleRatio() * bubbleRange * 1.5f + mWidth / 2f;
            float bubbleY = !mBubbleGenerator.isInstantAndOnce() ? (float) (mAmplitude * Math.sin(mOmega * bubbleX + mPhi) + mLength) : mHeight / 2f;
            mBubblePaint.setColor(mCurrentColor);
            mBubblePaint.setAlpha(bubble.getAlpha());
            canvas.drawCircle(bubbleX < mWidth / 2.0 ? (bubbleX - bubble.getBounceHeight() * 0.2f) : (bubbleX + bubble.getBounceHeight() * 0.2f), bubbleY - bubble.getBounceHeight(), bubble.getSize() * (1 + bubble.getBounceHeight() / DEFAULT_BUBBLE_BOUNCE_HEIGHT / 2), mBubblePaint);
        }
    }

    private void drawWaterDismiss(Canvas canvas) {
        int top = mWaterDismissDelta;
        RectF rectF = new RectF(mWidth / 2 - WATER_WIDTH / 2, top, mWidth / 2 + WATER_WIDTH / 2, mHeight - mBigCircleHeight + 10);
        mWaterFallPaint.setColor(Color.parseColor(END_COLOR));
        canvas.drawRect(rectF, mWaterFallPaint);
        mWaterDismissDelta += 50;
        if (mWaterDismissDelta <= mHeight - mBigCircleHeight + 10) {
            postInvalidate();
        } else {
            mBubbleGenerator.setInstantAndOnce(true);
            mBubbleGenerator.setGenerate(true);
            mShakeAnimator.start();
            mState = LiquidState.CIRCLE_ANIMATE;
        }
    }

    private void drawWaterFall(Canvas canvas) {
        int bottom = mWaterFallDelta;
        RectF rectF = new RectF(mWidth / 2 - WATER_WIDTH / 2, 0, mWidth / 2 + WATER_WIDTH / 2, bottom);
        if (mWaterFillRatio < 0.8) {
            mWaterFallPaint.setColor(Color.parseColor(CommonUtil.caculateColor(BEGIN_COLOR, MIDDLE_COLOR, (float) (mWaterFillRatio / 0.8))));
            mCurrentColor = Color.parseColor(CommonUtil.caculateColor(BEGIN_COLOR, MIDDLE_COLOR, (float) (mWaterFillRatio / 0.8)));
        } else if (mWaterFillRatio <= 1.0 && mWaterFillRatio > 0.8) {
            mWaterFallPaint.setColor(Color.parseColor(CommonUtil.caculateColor(MIDDLE_COLOR, END_COLOR, (float) ((mWaterFillRatio - 0.8) / 0.2))));
            mCurrentColor = Color.parseColor(CommonUtil.caculateColor(MIDDLE_COLOR, END_COLOR, (float) ((mWaterFillRatio - 0.8) / 0.2)));
        }
        canvas.drawRect(rectF, mWaterFallPaint);
        mWaterFallDelta += 50;
        if (mWaterFallDelta <= mHeight - 10) {
            postInvalidate();
        } else if (mState == LiquidState.WATER_FALL) {
            mState = LiquidState.LIQUID_FILL;
            postInvalidate();
        }
    }

    private void drawWave(Path path, Canvas canvas) {
        mBigCircleHeight = getHeight() / 2;
        mBigCirclePath.reset();
        mBigCirclePath.addCircle(mWidth / 2, 3 * mHeight / 4, mBigCircleHeight / 2, Path.Direction.CW);

        mOmega = 2.0f * Math.PI / mWidth * DEFAULT_OMEGA_RATIO * mOmegaRatio;
        mPhi = mWidth * DEFAULT_PHI_RATIO + mPhiDiff;
        mAmplitude = mBigCircleHeight * DEFAULT_AMPLITUDE_RATIO;
        mLength = mHeight - mLengthDiff;
        final int endX = mWidth;
        mWavePath.reset();
        mWavePath.moveTo(0, mHeight);
        mWavePath.lineTo(0, (float) (mAmplitude * Math.sin(mOmega * 0 + mPhi) + mLength));
        mBubbleGenerator.setGenerate(true);
        for (int beginX = 1; beginX < endX; beginX += 2) {
            float beginY = (float) (mAmplitude * Math.sin(mOmega * beginX + mPhi) + mLength);
            mWavePath.lineTo(beginX, beginY);
        }
        mWavePath.lineTo(mWidth, (float) (mAmplitude * Math.sin(mOmega * endX + mPhi) + mLength));
        mWavePath.lineTo(mWidth, mHeight);
        mWavePath.close();
        path.op(mWavePath, Path.Op.INTERSECT);
        mWaterFillRatio = mLengthDiff / (mBigCircleHeight + 3 * mAmplitude / 2);
        if (mWaterFillRatio < 0.8) {
            mBigCirclePaint.setColor(Color.parseColor(CommonUtil.caculateColor(BEGIN_COLOR, MIDDLE_COLOR, (float) (mWaterFillRatio / 0.8))));
        } else if (mWaterFillRatio < 1.0 && mWaterFillRatio > 0.8) {
            mBigCirclePaint.setColor(Color.parseColor(CommonUtil.caculateColor(MIDDLE_COLOR, END_COLOR, (float) ((mWaterFillRatio - 0.8) / 0.2))));
        }
        canvas.drawPath(path, mBigCirclePaint);
        mLengthDiff += mBigCircleHeight / 200.0;
        mOmegaRatio = mLengthDiff <= (mBigCircleHeight / 2.0) ? 1.4 - (0.6 * mLengthDiff / mBigCircleHeight) * 2
                : 1.4 - (0.6 * (mBigCircleHeight - mLengthDiff) / mBigCircleHeight) * 2;
        mPhiDiff += mOmega * 20 * (1 - (0.9 * mLengthDiff / mBigCircleHeight));
        if (mLength >= mHeight / 2.0 - mAmplitude) {
            postInvalidate();
        } else if (mState == LiquidState.LIQUID_FILL) {
            mState = LiquidState.WATER_DISMISS;
            postInvalidate();
        } else if (mState == LiquidState.CIRCLE_ANIMATE && !mShakeAnimator.isRunning()) {
            mState = LiquidState.MAKE_TICK;
            mTickAnimator.start();
            postInvalidate();
        }
    }

    enum LiquidState {
        WATER_FALL, LIQUID_FILL, WATER_DISMISS, CIRCLE_ANIMATE, MAKE_TICK
    }
}

