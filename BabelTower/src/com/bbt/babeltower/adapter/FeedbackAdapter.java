package com.bbt.babeltower.adapter;

import com.bbt.babeltower.activity.FeedbackActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FeedbackAdapter extends BaseAdapter {
	Context context;
	LayoutInflater inflater;

	public FeedbackAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return FeedbackActivity.feedbackThread.getCommentsList().size();
	}

	@Override
	public Object getItem(int position) {
		return FeedbackActivity.feedbackThread.getCommentsList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
