package com.jimmysun.pandora.imageview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 保持横纵比的ImageView
 *
 * @author SunQiang
 * @since 2018/10/12
 */
public class AspectRatioImageView extends ImageView {
    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            float drawableRatio = (float) drawableWidth / drawableHeight;
            int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
            int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
            float measuredRatio = (float) measuredWidth / measuredHeight;
            if (drawableRatio > measuredRatio) {
                measuredHeight =
                        (int) Math.ceil((float) drawableHeight * measuredWidth / drawableWidth);
            } else {
                measuredWidth =
                        (int) Math.ceil((float) drawableWidth * measuredHeight / drawableHeight);
            }
            setMeasuredDimension(measuredWidth, measuredHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
