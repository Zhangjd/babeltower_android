package com.bbt.babeltower.layout;

import com.bbt.babeltower.R;
import com.bbt.babeltower.activity.MainActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("InflateParams")
public class IndicatorLayout extends LinearLayout implements OnPageChangeListener {
	Context context;
	int count;

	public IndicatorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void setViewPage(ViewPager viewPager) {
		count = viewPager.getAdapter().getCount();
		if (count == 0)
			return;
		if (this.getChildCount() >= count){
			for (int index = count; index < this.getChildCount(); index++) {
				this.removeViewAt(index);
			}
			return;
		}
		for (int i = 0; i < count; i++) {
			View view = LayoutInflater.from(context).inflate(R.layout.indicate_image, null);
			this.addView(view);
		}
		// this.getChildAt(0).setSelected(true);
		this.getChildAt(viewPager.getCurrentItem()).setSelected(true);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		for (int i = 0; i < count; i++) {
			if (i == arg0) {
				this.getChildAt(i).setSelected(true);
			} else {
				this.getChildAt(i).setSelected(false);
			}
		}
		MainActivity.mainFragment.stopViewPagerTask();
		MainActivity.mainFragment.startViewPagerTask();
	}
}
