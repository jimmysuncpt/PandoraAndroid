package com.jimmysun.pandora.viewpager;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jimmysun.pandora.R;

/**
 * 自定义ViewPager的Adapter
 *
 * @author SunQiang
 * @since 2019/4/3
 */
public class MyPagerAdapter extends PagerAdapter {
    private static final int[] bgIds = new int[] {
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_3,
            R.drawable.bg_4,
            R.drawable.bg_5
    };

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View rootView = LayoutInflater.from(container.getContext()).inflate(R.layout.adapter_my_pager, container, false);
        ImageView imageView = rootView.findViewById(R.id.iv_pager);
        imageView.setImageResource(bgIds[position]);
        container.addView(rootView);
        return rootView;
    }
}
