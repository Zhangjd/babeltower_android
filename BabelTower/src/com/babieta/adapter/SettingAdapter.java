package com.babieta.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.babieta.R;

import android.content.Context;
import android.media.audiofx.BassBoost.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {

	private List<Map<String, Object>> listItems;
	private LayoutInflater layoutInflater;
	private Context context;

	public final class ListItemView { // custom widgets set
		public TextView setting_name;
		public TextView setting_info;
	}

	public SettingAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
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
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	// ListView�ڿ�ʼ���Ƶ�ʱ��ϵͳ�Զ�����getCount()���������ݺ�������ֵ�õ�ListView�ĳ��ȣ�
	// Ȼ�����������ȣ�����getView()��һ����ÿһ�С�
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = null;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.listview_setting, null);

			listItemView = new ListItemView();
			listItemView.setting_name = (TextView) convertView.findViewById(R.id.setting_name);
			// listItemView.setting_info = (TextView)
			// convertView.findViewById(R.id.setting_info);

			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		listItemView.setting_name.setText((String) listItems.get(position).get("setting_name"));
		// listItemView.setting_info.setText("test");

		return convertView;
	}

}
