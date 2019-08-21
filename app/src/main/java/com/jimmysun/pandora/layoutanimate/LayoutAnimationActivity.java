package com.jimmysun.pandora.layoutanimate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jimmysun.pandora.R;
import com.jimmysun.pandora.util.ViewUtils;

import java.util.Random;

public class LayoutAnimationActivity extends AppCompatActivity {
    private int i = 0;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_animation);
        container = findViewById(R.id.container);
        ViewUtils.setTranslationAnim(container, 500);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                TextView textView = new TextView(LayoutAnimationActivity.this);
                textView.setText("布局动画_" + i);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                textView.setGravity(Gravity.CENTER);
                Random random = new Random();
                textView.setBackgroundColor(0xff000000 + random.nextInt(0xffffff));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup
                        .LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                container.addView(textView, params);
            }
        });
        findViewById(R.id.btn_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (container.getChildCount() > 0) {
                    container.removeViewAt(container.getChildCount() - 1);
                }
            }
        });
    }
}
