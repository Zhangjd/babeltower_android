package com.babieta.layout;

import com.babieta.activity.MainActivity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//ViewPager子类,用作首页Focus内容循环展示
public class CarouselViewPager extends ViewPager {

	private float x1, y1, x2, y2;

	public CarouselViewPager(Context context) {
		super(context);
	}

	public CarouselViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 底层的View能够接收到这次的事件有一个前提条件：
	// 在父层级允许的情况下。假设不改变父层级的dispatch方法，
	// 在系统调用底层onTouchEvent之前,会先调用父View的onInterceptTouchEvent方法判断，
	// 父层View是不是要截获本次touch事件之后的action。
	// 如果onInterceptTouchEvent返回了true，那么本次touch事件之后的所有action都不会再向深层的View传递
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("event: " + event.getAction());

		// 阻止ListView截获touch事件
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
				return performClick();
			} else { // 滑动
				if (x1 < 50) { // 滑出菜单
					MainActivity.toggleMenu();
				} else if (distanceY > 200) { // 下拉刷新
					MainActivity.mainFragment.manualRefresh();
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

	@Override
	public boolean performClick() {
		MainActivity.mainFragment.handleFocusClick(this.getCurrentItem());
		return super.performClick();
	}
}
