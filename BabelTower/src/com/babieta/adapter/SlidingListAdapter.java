package com.babieta.adapter;

import java.util.List;
import java.util.Map;

import com.babieta.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SlidingListAdapter extends SimpleAdapter {
	private int selectedPosition = 0;// 选中的位置

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public SlidingListAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		TextView text = (TextView) view.findViewById(R.id.menu_list_text);
		// LinearLayout layout = (LinearLayout)
		// view.findViewById(R.id.menu_list_layout);

		// Reference :
		// http://blog.csdn.net/books1958/article/details/39580699
		if (selectedPosition == position) {
			view.setBackgroundResource(R.drawable.slidemenu_item_selected);
			text.setTextColor(Color.rgb(0, 0, 0));
		} else {
			view.setBackgroundResource(R.drawable.slidemenu_item_normal);
			text.setTextColor(Color.rgb(0, 0, 0));
		}
		return view;
	}
}
