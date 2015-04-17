package com.bbt.babeltower.activity;

import com.avos.avoscloud.AVAnalytics;
import com.bbt.babeltower.R;
import com.bbt.babeltower.base.Netroid;
import com.bbt.babeltower.base.Util;
import com.bbt.babeltower.fragment.MainFragment;
import com.bbt.babeltower.layout.SwipeBackLayout;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends FragmentActivity {

	public static MainFragment mainFragment = new MainFragment();
	public static int mainFragmentFlag = 1;
	
	private static SlidingMenu slidingMenu;
	protected SwipeBackLayout layout;

	private long exitTime = System.currentTimeMillis();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainActivity", "onCreate called");

		Netroid.init(this); // �ⲿHTTP��ĳ�ʼ��

		setContentView(R.layout.activity_main);
		slidingMenu = Util.initSlidingMenu(this);
		switchFragment(MainActivity.mainFragment);
		initHeaderButtonSwitch(); // ������ť�л��˵�
	}

	@Override
	protected void onStart() {
		super.onStart();

		// LeanCloud ���� Android ���ͺ�Ӧ�õĴ����
		Intent intent = getIntent();
		AVAnalytics.trackAppOpened(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && slidingMenu.isMenuShowing()) { // �����˵���ʾ��ʱ��
			slidingMenu.toggle();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Util.showToast(MainActivity.this, "�ٰ�һ���˳�����");
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) { // �˵�����
			slidingMenu.toggle();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void switchFragment(Fragment fragment) {
		// getFragmentManager().beginTransaction().replace(R.id.content_fragment,
		// fragment).commit();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.content_fragment, fragment);
		// ���� addToBackStack()�� replace���񱻱��浽back
		// stack������û����Ի������񣬲�ͨ������BACK��������ǰһ��fragment
		transaction.addToBackStack(null);
		transaction.commit();
		slidingMenu.showContent();
	}

	public void initHeaderButtonSwitch() {
		ImageButton button = (ImageButton) findViewById(R.id.header_but);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				slidingMenu.toggle();
			}
		});
	}

	public static void toggleMenu() {
		slidingMenu.toggle();
	}

	public static void setMenuTouchModeToFullscreen() {
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	public static void setMenuTouchModeToMargin() {
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}
}
