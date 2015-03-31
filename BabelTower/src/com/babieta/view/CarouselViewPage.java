package com.babieta.view;

import com.babieta.activity.MainActivity;

import android.R.integer;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

//内容循环展示（Carousel:旋转木马）
public class CarouselViewPage extends ViewPager {

	private float x1, y1, x2, y2;

	public CarouselViewPage(Context context) {
		super(context);
	}

	public CarouselViewPage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("event: " + event.getAction());

		getParent().requestDisallowInterceptTouchEvent(true);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x1 = event.getRawX();// 得到相对应屏幕左上角的坐标
			y1 = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:

			x2 = event.getRawX();
			y2 = event.getRawY();
			double distance = Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2) + Math.abs(y1 - y2)
					* Math.abs(y1 - y2));// 两点之间的距离
			double distanceY = y2 - y1;
			if (distance < 15) { // 距离较小，当作click事件来处理
				Toast.makeText(getContext(), "click on: " + this.getCurrentItem(),
						Toast.LENGTH_SHORT).show();
				return true;
			} else { // 滑动
				if (x1 < 50) { // 滑出菜单
					MainActivity.toggleMenu();
				} else if (distanceY > 100) { //下拉刷新
					MainActivity.mainFragment.listView.setRefreshing();
				} else {

				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
}
