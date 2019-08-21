package com.jimmysun.pandora.util;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.ViewGroup;

/**
 * View相关的工具
 *
 * @author SunQiang
 * @since 2018/8/15
 */
public class ViewUtils {

    /**
     * 设置layout添加和移除View的平移动画
     *
     * @param viewGroup 设置动画的layout
     * @param duration  时长（ms）
     */
    public static void setTranslationAnim(ViewGroup viewGroup, int duration) {
        if (viewGroup != null) {
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.setDuration(LayoutTransition.APPEARING, duration);
            layoutTransition.setDuration(LayoutTransition.DISAPPEARING, duration);
            // 出现动画
            PropertyValuesHolder translationHolder = PropertyValuesHolder.ofFloat("translationX",
                    ScreenUtils.getScreenWidth(viewGroup.getContext()), 0);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat("alpha", 0, 1);
            ObjectAnimator appearAnimator = ObjectAnimator.ofPropertyValuesHolder(viewGroup,
                    translationHolder, alphaHolder);
            layoutTransition.setAnimator(LayoutTransition.APPEARING, appearAnimator);
            // 消失动画
            translationHolder = PropertyValuesHolder.ofFloat("translationX", 0, ScreenUtils
                    .getScreenWidth(viewGroup.getContext()));
            alphaHolder = PropertyValuesHolder.ofFloat("alpha", 1, 0);
            ObjectAnimator disappearAnimator = ObjectAnimator.ofPropertyValuesHolder(viewGroup,
                    translationHolder, alphaHolder);
            layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, disappearAnimator);
            viewGroup.setLayoutTransition(layoutTransition);
        }
    }

    /**
     * 关闭RecyclerView刷新动画
     */
    public static void closeRecyclerViewAnimator(RecyclerView recyclerView) {
        if (recyclerView != null) {
            try {
                RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
                if (animator != null) {
                    animator.setAddDuration(0);
                    animator.setChangeDuration(0);
                    animator.setMoveDuration(0);
                    animator.setRemoveDuration(0);
                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
