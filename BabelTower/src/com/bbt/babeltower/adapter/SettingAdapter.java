package com.bbt.babeltower.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbt.babeltower.R;
import com.bbt.babeltower.base.MyApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {

	private List<Map<String, Object>> listItems;
	private LayoutInflater layoutInflater;

	public final class ListItemView { // custom widgets set
		public TextView setting_name;
		public TextView setting_info;
	}

	public SettingAdapter(Context context) {
		this.layoutInflater = LayoutInflater.from(context);
		this.listItems = new ArrayList<Map<String, Object>>();

		// Initial Settings list
		String[] settings = context.getResources().getStringArray(R.array.settingList);
		for (int i = 0; i < settings.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("setting_name", settings[i]);
			listItems.add(map);
		}
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	// ListView�ڿ�ʼ���Ƶ�ʱ��ϵͳ�Զ�����getCount()���������ݺ�������ֵ�õ�ListView�ĳ��ȣ�
	// Ȼ�����������ȣ�����getView()��һ����ÿһ�С�
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.listview_setting, parent, false);
			listItemView = new ListItemView();
			listItemView.setting_name = (TextView) convertView.findViewById(R.id.setting_name);
			convertView.setTag(listItemView);

			if (position == 1 && MyApplication.updateFlag == true) {
				ImageView imageView = (ImageView) convertView
						.findViewById(R.id.setting_update_tips);
				imageView.setVisibility(View.VISIBLE);
			}
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.setting_name.setText((String) listItems.get(position).get("setting_name"));
		return convertView;
	}

}
