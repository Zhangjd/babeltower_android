package com.babieta.view;

import com.babieta.activity.MainActivity;

import android.R.integer;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

//����ѭ��չʾ��Carousel:��תľ��
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
			x1 = event.getRawX();// �õ����Ӧ��Ļ���Ͻǵ�����
			y1 = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:

			x2 = event.getRawX();
			y2 = event.getRawY();
			double distance = Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2) + Math.abs(y1 - y2)
					* Math.abs(y1 - y2));// ����֮��ľ���
			double distanceY = y2 - y1;
			if (distance < 15) { // �����С������click�¼�������
				Toast.makeText(getContext(), "click on: " + this.getCurrentItem(),
						Toast.LENGTH_SHORT).show();
				return true;
			} else { // ����
				if (x1 < 50) { // �����˵�
					MainActivity.toggleMenu();
				} else if (distanceY > 100) { //����ˢ��
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
