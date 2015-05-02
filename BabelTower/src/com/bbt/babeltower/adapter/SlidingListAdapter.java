package com.bbt.babeltower.adapter;

import java.util.List;
import java.util.Map;

import com.bbt.babeltower.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlidingListAdapter extends BaseAdapter {
	private Context context;
	private List<? extends Map<String, ?>> data;
	private int resource;
	private int selectedPosition = 0;// 选中的位置

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public SlidingListAdapter(Context context, List<? extends Map<String, ?>> data, int resource) {
		this.context = context;
		this.data = data;
		this.resource = resource;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(resource, parent, false);

		TextView text = (TextView) convertView.findViewById(R.id.menu_list_text);
		text.setText((String) data.get(position).get("text"));

		// Reference :
		// http://blog.csdn.net/books1958/article/details/39580699
		LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.menu_list_layout);
		if (selectedPosition == position) {
			layout.setBackgroundResource(R.drawable.slidemenu_item_selected);
			text.setTextColor(context.getResources().getColor(R.color.black));
		} else {
			layout.setBackgroundResource(R.drawable.slidemenu_item_normal);
			text.setTextColor(context.getResources().getColor(R.color.babel_gray_a));
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
