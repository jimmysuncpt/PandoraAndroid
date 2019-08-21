package com.jimmysun.pandora.viewpager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.jimmysun.pandora.R;

/**
 * 带回弹和阻尼效果的ViewPager
 *
 * @author SunQiang
 * @since 2019/4/3
 */
public class BounceBackViewPager extends ViewPager {

    /**
     * maximum z distance to translate child view
     */
    final static int DEFAULT_OVER_SCROLL_TRANSLATION = 150;

    /**
     * duration of over scroll animation in ms
     */
    final private static int DEFAULT_OVER_SCROLL_ANIMATION_DURATION = 400;

    @SuppressWarnings("unused")
    private final static String DEBUG_TAG = ViewPager.class.getSimpleName();
    private final static int INVALID_POINTER_ID = -1;
    final private OverScrollEffect mOverScrollEffect = new OverScrollEffect();
    final private Camera mCamera = new Camera();
    final private int mTouchSlop;
    private OnPageChangeListener mScrollListener;
    private float mLastMotionX;
    private int mActivePointerId;
    private int mScrollPosition;
    private float mScrollPositionOffset;
    private float mOverScrollTranslation;
    private int mOverScrollAnimationDuration;
    private int mLastPosition = 0;

    public BounceBackViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStaticTransformationsEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        super.setOnPageChangeListener(new MyOnPageChangeListener());
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.BounceBackViewPager);
        mOverScrollTranslation =
                typedArray.getDimension(R.styleable.BounceBackViewPager_translation,
                        DEFAULT_OVER_SCROLL_TRANSLATION);
        mOverScrollAnimationDuration =
                typedArray.getInt(R.styleable.BounceBackViewPager_releaseDuration,
                        DEFAULT_OVER_SCROLL_ANIMATION_DURATION);
        typedArray.recycle();
    }

    public int getOverScrollAnimationDuration() {
        return mOverScrollAnimationDuration;
    }

    public void setOverScrollAnimationDuration(int mOverscrollAnimationDuration) {
        this.mOverScrollAnimationDuration = mOverscrollAnimationDuration;
    }

    public float getOverScrollTranslation() {
        return mOverScrollTranslation;
    }

    public void setOverScrollTranslation(int mOverscrollTranslation) {
        this.mOverScrollTranslation = mOverscrollTranslation;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mScrollListener = listener;
    }

    ;

    private void invalidateVisibleChildren(final int position) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).invalidate();

        }
        //this.invalidate();
        // final View child = getChildAt(position);
        // final View previous = getChildAt(position - 1);
        // final View next = getChildAt(position + 1);
        // if (child != null) {
        // child.invalidate();
        // }
        // if (previous != null) {
        // previous.invalidate();
        // }
        // if (next != null) {
        // next.invalidate();
        // }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mLastMotionX = ev.getX();
                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    break;
                }
                case MotionEventCompat.ACTION_POINTER_DOWN: {
                    final int index = MotionEventCompat.getActionIndex(ev);
                    mLastMotionX = MotionEventCompat.getX(ev, index);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                    break;
                }
            }
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean callSuper = false;

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                callSuper = true;
                mLastMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                callSuper = true;
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionX = MotionEventCompat.getX(ev, index);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId != INVALID_POINTER_ID) {
                    // Scroll to follow the motion event
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev,
                            mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, activePointerIndex);
                    final float deltaX = mLastMotionX - x;
                    final float oldScrollX = getScrollX();
                    final int width = getWidth();
                    final int widthWithMargin = width + getPageMargin();
                    final int lastItemIndex = getLastScrollPosition();
                    final int currentItemIndex = getCurrentItem();
                    final float leftBound = Math.max(0, (currentItemIndex - 1) * widthWithMargin);
                    final float rightBound =
                            Math.min(currentItemIndex + 1, lastItemIndex) * widthWithMargin;
                    final float scrollX = oldScrollX + deltaX;
                    if (mScrollPositionOffset == 0) {
                        if (scrollX < leftBound) {
                            if (leftBound == 0) {
                                final float over = deltaX + mTouchSlop;
                                mOverScrollEffect.setPull(over / width);
                            }
                        } else if (scrollX > rightBound) {
                            if (rightBound == lastItemIndex * widthWithMargin) {
                                final float over = scrollX - rightBound - mTouchSlop;
                                mOverScrollEffect.setPull(over / width);
                            }
                        }
                    } else {
                        mLastMotionX = x;
                    }
                } else {
                    mOverScrollEffect.onRelease();
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                callSuper = true;
                mActivePointerId = INVALID_POINTER_ID;
                mOverScrollEffect.onRelease();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex =
                        (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastMotionX = ev.getX(newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    callSuper = true;
                }
                break;
            }
        }

        if (mOverScrollEffect.isOverScrolling() && !callSuper) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        if (child.getWidth() == 0) {
            return false;
        }
        final int position = child.getLeft() / child.getWidth();
        final boolean isFirstOrLast =
                position == 0 || (getAdapter() != null && position == getAdapter().getCount() - 1);
        if (mOverScrollEffect.isOverScrolling() && isFirstOrLast) {
            final float dx = getWidth() / 2f;
            final float dy = getHeight() / 2f;
            t.getMatrix().reset();
            final float translateX =
                    mOverScrollTranslation * (mOverScrollEffect.mOverScroll > 0 ?
                            Math.min(mOverScrollEffect.mOverScroll, 1) :
                            Math.max(mOverScrollEffect.mOverScroll, -1));
            mCamera.save();
            mCamera.translate(-translateX, 0, 0);
            mCamera.getMatrix(t.getMatrix());
            mCamera.restore();
            t.getMatrix().preTranslate(-dx, -dy);
            t.getMatrix().postTranslate(dx, dy);

            if (getChildCount() == 1) {
                this.invalidate();
            } else {
                child.invalidate();
            }
            return true;
        }
        return false;
    }

    protected int getLastScrollPosition() {
        if (getAdapter() != null) {
            return getAdapter().getCount() - 1;
        } else {
            return 0;
        }
    }

    /**
     * @author renard, extended by Piotr Zawadzki
     */
    private class OverScrollEffect {
        private float mOverScroll;
        private Animator mAnimator;

        /**
         * @param deltaDistance [0..1] 0->no overscroll, 1>full overscroll
         */
        public void setPull(final float deltaDistance) {
            mOverScroll = deltaDistance;
            invalidateVisibleChildren(mLastPosition);
        }

        /**
         * called when finger is released. starts to animate back to default position
         */
        private void onRelease() {
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimation(0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                mAnimator.cancel();
            } else {
                startAnimation(0);
            }
        }

        private void startAnimation(final float target) {
            mAnimator = ObjectAnimator.ofFloat(this, "pull", mOverScroll, target);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            final float scale = Math.abs(target - mOverScroll);
            mAnimator.setDuration((long) (mOverScrollAnimationDuration * scale));
            mAnimator.start();
        }

        private boolean isOverScrolling() {
            if (mScrollPosition == 0 && mOverScroll < 0) {
                return true;
            }
            final boolean isLast = getLastScrollPosition() == mScrollPosition;
            return isLast && mOverScroll > 0;
        }
    }

    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mScrollListener != null) {
                mScrollListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            mScrollPosition = position;
            mScrollPositionOffset = positionOffset;
            mLastPosition = position;
            invalidateVisibleChildren(position);
        }

        @Override
        public void onPageSelected(int position) {

            if (mScrollListener != null) {
                mScrollListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(final int state) {

            if (mScrollListener != null) {
                mScrollListener.onPageScrollStateChanged(state);
            }
            if (state == SCROLL_STATE_IDLE) {
                mScrollPositionOffset = 0;
            }
        }
    }
}