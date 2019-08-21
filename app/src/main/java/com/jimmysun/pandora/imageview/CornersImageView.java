package com.jimmysun.pandora.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.util.AttributeSet;

/**
 * 带圆角的ImageView
 *
 * @author SunQiang
 * @since 2019/4/8
 */
public class CornersImageView extends AspectRatioImageView {
    /**
     * 圆角类型：左上角
     */
    public static final int CORNER_TYPE_LEFT_TOP = 1;
    /**
     * 圆角类型：右上角
     */
    public static final int CORNER_TYPE_RIGHT_TOP = 2;
    /**
     * 圆角类型：左下角
     */
    public static final int CORNER_TYPE_LEFT_BOTTOM = 4;
    /**
     * 圆角类型：右下角
     */
    public static final int CORNER_TYPE_RIGHT_BOTTOM = 8;
    /**
     * 圆角类型：左边
     */
    public static final int CORNER_TYPE_LEFT = CORNER_TYPE_LEFT_TOP | CORNER_TYPE_LEFT_BOTTOM;
    /**
     * 圆角类型：上边
     */
    public static final int CORNER_TYPE_TOP = CORNER_TYPE_LEFT_TOP | CORNER_TYPE_RIGHT_TOP;
    /**
     * 圆角类型：右边
     */
    public static final int CORNER_TYPE_RIGHT = CORNER_TYPE_RIGHT_TOP | CORNER_TYPE_RIGHT_BOTTOM;
    /**
     * 圆角类型：下边
     */
    public static final int CORNER_TYPE_BOTTOM = CORNER_TYPE_LEFT_BOTTOM | CORNER_TYPE_RIGHT_BOTTOM;
    /**
     * 圆角类型：全部
     */
    public static final int CORNER_TYPE_ALL = CORNER_TYPE_TOP | CORNER_TYPE_BOTTOM;

    private int mCornerType;
    private int mRadius;

    private float[] mRadii;
    private RectF mRectF;
    private Path mPath;

    public CornersImageView(Context context) {
        super(context);
        init();
    }

    public CornersImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CornersImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRadii = new float[8];
        mRectF = new RectF();
        mPath = new Path();
    }

    /**
     * 设置圆角类型
     *
     * @param cornerType 圆角类型
     */
    public void setCornerType(int cornerType) {
        mCornerType = cornerType;
    }

    /**
     * 设置圆角半径
     *
     * @param radius 圆角半径(px)
     */
    public void setRadius(@Px int radius) {
        if (radius >= 0) {
            mRadius = radius;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if ((mCornerType & CORNER_TYPE_LEFT_TOP) > 0) {
            mRadii[0] = mRadii[1] = mRadius;
        }
        if ((mCornerType & CORNER_TYPE_RIGHT_TOP) > 0) {
            mRadii[2] = mRadii[3] = mRadius;
        }
        if ((mCornerType & CORNER_TYPE_RIGHT_BOTTOM) > 0) {
            mRadii[4] = mRadii[5] = mRadius;
        }
        if ((mCornerType & CORNER_TYPE_LEFT_BOTTOM) > 0) {
            mRadii[6] = mRadii[7] = mRadius;
        }
        mRectF.right = getWidth();
        mRectF.bottom = getHeight();
        mPath.addRoundRect(mRectF, mRadii, Path.Direction.CW);
        canvas.clipPath(mPath);
        super.onDraw(canvas);
    }
}
