package com.babieta.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.babieta.R;
import com.babieta.activity.MainActivity;
import com.babieta.adapter.SlidingListAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
			case 0: // 今日内容
				fragment = MainActivity.mainFragment;
				MainActivity.mainFragmentFlag = 1;
				break;
			case 1: // 分类浏览
				fragment = new SectionFragment();
				MainActivity.mainFragmentFlag = 1;
				break;
			case 2: // 我的收藏
				fragment = new CollectFragment();
				MainActivity.mainFragmentFlag = 0;
				break;
			case 3: // 设置
				fragment = new SettingFragment();
				MainActivity.mainFragmentFlag = 0;
				break;
			default:
				fragment = new MainFragment();
				break;
			}
			switchFragment(fragment);
			// 记录点击item并改变背景颜色
			adapter.setSelectedPosition(position);
			adapter.notifyDataSetInvalidated();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView = getListView();
		adapter = new SlidingListAdapter(getActivity(), this.getData(), R.layout.slidemenu_left_list,
				new String[] { "image", "text" }, new int[] { R.id.menu_list_image,
						R.id.menu_list_text });
		listView.setAdapter(adapter);
		listView.setDivider(null);
		listView.setDividerHeight(0);
		listView.setOnItemClickListener(new MyItemClickListener());
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int[] images = { R.drawable.menu_home, // 今日内容
				R.drawable.menu_management, // 分类浏览
				R.drawable.menu_collect, // 我的收藏
				R.drawable.menu_setting // 设置
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
