package com.babieta.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.babieta.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

	// ListView在开始绘制的时候，系统自动调用getCount()函数，根据函数返回值得到ListView的长度，
	// 然后根据这个长度，调用getView()逐一画出每一行。
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.listview_setting, parent, false);
			listItemView = new ListItemView();
			listItemView.setting_name = (TextView) convertView.findViewById(R.id.setting_name);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.setting_name.setText((String) listItems.get(position).get("setting_name"));
		return convertView;
	}

}
