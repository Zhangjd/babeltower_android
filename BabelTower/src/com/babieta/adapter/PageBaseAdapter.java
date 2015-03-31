package com.babieta.adapter;

import java.util.LinkedList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PageBaseAdapter extends PagerAdapter {
	protected LinkedList<View> views;

	// ����Ҫ������View�ĸ���
	@Override
	public int getCount() {
		return views.size();
	}

	// ���������£���һ������ǰ��ͼ��ӵ�container�У��ڶ������ص�ǰView
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	// �ӵ�ǰcontainer��ɾ��ָ��λ�ã�position����View;
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}
}
