package com.babieta.layout;

import com.babieta.activity.MainActivity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//ViewPager����,������ҳFocus����ѭ��չʾ
public class CarouselViewPager extends ViewPager {

	private float x1, y1, x2, y2;

	public CarouselViewPager(Context context) {
		super(context);
	}

	public CarouselViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// �ײ��View�ܹ����յ���ε��¼���һ��ǰ��������
	// �ڸ��㼶���������¡����費�ı丸�㼶��dispatch������
	// ��ϵͳ���õײ�onTouchEvent֮ǰ,���ȵ��ø�View��onInterceptTouchEvent�����жϣ�
	// ����View�ǲ���Ҫ�ػ񱾴�touch�¼�֮���action��
	// ���onInterceptTouchEvent������true����ô����touch�¼�֮�������action��������������View����
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("event: " + event.getAction());

		// ��ֹListView�ػ�touch�¼�
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
				return performClick();
			} else { // ����
				if (x1 < 50) { // �����˵�
					MainActivity.toggleMenu();
				} else if (distanceY > 200) { // ����ˢ��
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
