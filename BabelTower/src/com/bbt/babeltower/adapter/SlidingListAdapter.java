package com.bbt.babeltower.adapter;

import java.util.List;
import java.util.Map;

import com.bbt.babeltower.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
		// View view = super.getView(position, convertView, parent);

		if (position == 2) { // 中间空白那一项,没有波纹效果
			convertView = LayoutInflater.from(context).inflate(
					R.layout.slidemenu_left_list_noripple, parent, false);
		} else {
			convertView = LayoutInflater.from(context).inflate(resource, parent, false);
		}

		TextView text = (TextView) convertView.findViewById(R.id.menu_list_text);
		text.setText((String) data.get(position).get("text"));

		// Reference :
		// http://blog.csdn.net/books1958/article/details/39580699
		LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.menu_list_layout);
		if (selectedPosition == position) {
			layout.setBackgroundResource(R.drawable.slidemenu_item_selected);
			text.setTextColor(Color.rgb(255, 255, 255));
		} else {
			layout.setBackgroundResource(R.drawable.slidemenu_item_normal);
			text.setTextColor(Color.rgb(80, 80, 80));
		}

		LayoutParams lp = (LayoutParams) layout.getLayoutParams();
		if (position == 2) {
			lp.height = lp.height * 60 / 112;
			layout.setLayoutParams(lp);
			View divider = (View) convertView.findViewById(R.id.menu_list_divider);
			divider.setVisibility(View.GONE);
		} else {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			@SuppressWarnings("deprecation")
			int screenWidth = wm.getDefaultDisplay().getWidth();
			int menuWidth = (int) (screenWidth * 0.389);
			lp.height = menuWidth * 85 / 280;
			layout.setLayoutParams(lp);
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

	@Override
	public boolean isEnabled(int position) {
		if (position == 2) {
			return false;
		}
		return super.isEnabled(position);
	}
}
