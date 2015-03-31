package com.babieta.adapter;

import java.util.LinkedList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PageBaseAdapter extends PagerAdapter {
	protected LinkedList<View> views;

	// 返回要滑动的View的个数
	@Override
	public int getCount() {
		return views.size();
	}

	// 做了两件事，第一：将当前视图添加到container中，第二：返回当前View
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	// 从当前container中删除指定位置（position）的View;
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}
}
