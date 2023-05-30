package cn.miracle.dev_miracle_music_android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.miracle.dev_miracle_music_android.R;

public class AudioRoundProgressView extends View {

    private Paint mPaint;
    private float mRingRadius;
    private int mTotalProgress;
    private int mCurrProgress;

    public AudioRoundProgressView(Context context) {
        super(context);
    }

    public AudioRoundProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        @SuppressLint({"Recycle", "CustomViewStyleable"}) TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressView);
        float mRadius = typedArray.getDimension(R.styleable.RoundProgressView_radius, 22);
        float mStrokeWidth = typedArray.getDimension(R.styleable.RoundProgressView_strokeWidth, 3);
        int mPaintColor = typedArray.getColor(R.styleable.RoundProgressView_strokeColor, Color.GREEN);
        mRingRadius = mRadius + mStrokeWidth / 2;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    public AudioRoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AudioRoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int mCenterX = getWidth() / 2;
        int mCenterY = getHeight() / 2;

        if (mCurrProgress > 0) {
            @SuppressLint("DrawAllocation") RectF rectF = new RectF();
            rectF.left = (mCenterX - mRingRadius);
            rectF.top = (mCenterY - mRingRadius);
            rectF.right = mRingRadius * 2 + (mCenterX - mRingRadius);
            rectF.bottom = mRingRadius * 2 + (mCenterY - mRingRadius);
            canvas.drawArc(rectF, -90, ((float) mCurrProgress / mTotalProgress) * 360, false, mPaint);
        }
    }

    public void setAudioProgress(int currProgress, int totalProgress) {
        mCurrProgress = currProgress;
        mTotalProgress = totalProgress;

        postInvalidate();
    }

}
