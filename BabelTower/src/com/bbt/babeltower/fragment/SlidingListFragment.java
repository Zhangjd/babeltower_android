package com.bbt.babeltower.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbt.babeltower.R;
import com.bbt.babeltower.activity.MainActivity;
import com.bbt.babeltower.adapter.SlidingListAdapter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class SlidingListFragment extends ListFragment {

	private ListView listView;
	private SlidingListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.slidemenu_left_fragment, container, false);
	}

	private class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Fragment fragment = null;
			switch (position) {
			case 0: // ��������
				fragment = MainActivity.mainFragment;
				MainActivity.mainFragmentFlag = 1;
				break;
			case 1: // �������
				fragment = new SectionFragment();
				MainActivity.mainFragmentFlag = 0;
				break;
			case 3: // �ҵ��ղ�
				fragment = new CollectFragment();
				MainActivity.mainFragmentFlag = 0;
				break;
			case 4: // ����
				fragment = new SettingFragment();
				MainActivity.mainFragmentFlag = 0;
				break;
			default:
				break;
			}
			switchFragment(fragment);
			// ��¼���item���ı䱳����ɫ
			adapter.setSelectedPosition(position);
			adapter.notifyDataSetInvalidated();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView = getListView();
		adapter = new SlidingListAdapter(getActivity(), this.getData(),
				R.layout.slidemenu_left_list);
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setDividerHeight(0);
		listView.setOnItemClickListener(new MyItemClickListener());

		// ��̬����Logo���±߾�
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		int screenHeight = wm.getDefaultDisplay().getHeight();
		int screenWidth = wm.getDefaultDisplay().getWidth();
		int menuWidth = (int) (screenWidth * 0.389);
		RelativeLayout logo = (RelativeLayout) getActivity().findViewById(R.id.slidingmenu_logo);
		LayoutParams lp = (LayoutParams) logo.getLayoutParams();
		lp.setMargins(0, (int)(screenHeight * 183 / 1280.0), 0, (int)(screenHeight * 215 / 1280.0));
		logo.setLayoutParams(lp);
		Button button = (Button) getActivity().findViewById(R.id.slidingmenu_logo_img);
		button.getLayoutParams().height = (int) (menuWidth * 0.40 * 123 / 111);
		button.getLayoutParams().width = (int) (menuWidth * 0.40);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int[] images = { R.drawable.menu_home, // ��������
				R.drawable.menu_management, // �������
				R.drawable.babeltower_icon_source, // �ٵ�
				R.drawable.menu_collect, // �ҵ��ղ�
				R.drawable.menu_setting // ����
		};
		String[] textStrings = getResources().getStringArray(R.array.menuList);
		for (int i = 0; i < textStrings.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", images[i]);
			map.put("text", textStrings[i]);
			list.add(map);
		}
		return list;
	}

	private void switchFragment(Fragment fragment) {
		MainActivity mainActivity = (MainActivity) getActivity();
		mainActivity.switchFragment(fragment);
	}

}
